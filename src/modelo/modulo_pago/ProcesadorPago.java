package modelo.modulo_pago;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Lógica de negocio del pago (RF-18, RF-19 y RF-20).
 *
 * Flujo (todo dentro de UNA transacción):
 *  1. Bloquea la reserva y verifica que siga "Pendiente de pago" y dentro del plazo.
 *  2. Evalúa el pago según el medio (bono y millas se validan contra la BD;
 *     PSE, tarjeta y transferencia usan una pasarela SIMULADA).
 *  3. Registra el pago con su estado.
 *  4. Si fue "Aprobado": confirma la reserva, emite los tiquetes y registra el correo.
 *     Si no: la reserva queda bloqueada ("Pendiente de pago") hasta fecha_expiracion;
 *     cuando vence, liberarReservasExpiradas() libera los cupos.
 */
public class ProcesadorPago {

    /** Equivalencia usada para pagar con millas: 1 milla = $30 COP (ajustable). */
    public static final BigDecimal VALOR_MILLA_COP = new BigDecimal("30");

    private final PagoDAO pagoDAO = new PagoDAO();
    private final TiqueteDAO tiqueteDAO = new TiqueteDAO();
    private final NotificacionDAO notificacionDAO = new NotificacionDAO();

    /** Millas necesarias para cubrir un valor (se redondea hacia arriba). */
    public static int millasNecesarias(BigDecimal total) {
        return total.divide(VALOR_MILLA_COP, 0, RoundingMode.UP).intValue();
    }

    public ResultadoPago procesar(ResumenCompra resumen, SolicitudPago solicitud) {
        ResultadoPago res = new ResultadoPago();
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            con.setAutoCommit(false);

            BigDecimal total = resumen.getTotal();
            Pago pago = new Pago();
            pago.setIdReserva(resumen.getIdReserva());
            pago.setIdMedioPago(solicitud.getMedioPago().getId());
            pago.setValor(total);

            // 1. Estado de la reserva
            PagoDAO.EstadoReserva er = pagoDAO.bloquearReserva(con, resumen.getIdReserva());
            if (er == null) {
                con.rollback();
                return resultado(res, "Error", "La reserva ya no existe.", null, false);
            }
            if (!ResumenCompra.ESTADO_PENDIENTE.equals(er.estado)) {
                con.rollback();
                return resultado(res, "Error",
                        "La reserva está en estado \"" + er.estado + "\" y no admite pagos.", null, false);
            }
            if (er.vencida) {
                pagoDAO.liberarReserva(con, resumen.getIdReserva());
                pago.setEstado(Pago.EXPIRADO);
                pagoDAO.insertar(con, pago);
                con.commit();
                return resultado(res, Pago.EXPIRADO,
                        "El plazo de la reserva venció. Se liberaron los cupos asociados.", null, false);
            }

            // 2. Evaluar el pago según el medio
            String estado;
            String mensaje;
            String referencia = null;

            switch (solicitud.getMedioPago().getTipo()) {
                case BONO: {
                    String codigo = solicitud.getCodigoBono() == null ? "" : solicitud.getCodigoBono().trim();
                    PagoDAO.Bono bono = pagoDAO.obtenerBono(con, codigo);
                    if (bono == null) {
                        estado = Pago.RECHAZADO; mensaje = "El bono \"" + codigo + "\" no existe.";
                    } else if (!bono.activo) {
                        estado = Pago.RECHAZADO; mensaje = "El bono está inactivo.";
                    } else if (bono.vencimiento.toLocalDate().isBefore(LocalDate.now())) {
                        estado = Pago.RECHAZADO; mensaje = "El bono está vencido.";
                    } else if (bono.saldo.compareTo(total) < 0) {
                        estado = Pago.RECHAZADO; mensaje = "El saldo del bono no cubre el total de la compra.";
                    } else if (!pagoDAO.descontarBono(con, bono.id, total)) {
                        estado = Pago.RECHAZADO; mensaje = "No fue posible descontar el saldo del bono.";
                    } else {
                        estado = Pago.APROBADO; mensaje = "Pago con bono corporativo aprobado.";
                        pago.setIdBono(bono.id);
                        referencia = "BONO-USE-" + codigo;
                    }
                    break;
                }
                case MILLAS: {
                    int necesarias = millasNecesarias(total);
                    int saldo = pagoDAO.obtenerSaldoMillas(con, resumen.getIdComprador());
                    if (saldo < 0) {
                        estado = Pago.RECHAZADO; mensaje = "El comprador no tiene cuenta de millas.";
                    } else if (saldo < necesarias) {
                        estado = Pago.RECHAZADO;
                        mensaje = "Millas insuficientes: necesita " + necesarias + " y tiene " + saldo + ".";
                    } else if (!pagoDAO.descontarMillas(con, resumen.getIdComprador(), necesarias)) {
                        estado = Pago.RECHAZADO; mensaje = "No fue posible descontar las millas.";
                    } else {
                        estado = Pago.APROBADO;
                        mensaje = "Pago con millas aprobado (" + necesarias + " millas).";
                        pago.setMillasUsadas(necesarias);
                        referencia = "MILLAS-" + necesarias;
                    }
                    break;
                }
                default: { // PSE, TARJETA, TRANSFERENCIA -> pasarela simulada
                    estado = solicitud.getResultadoSimulado() == null ? Pago.APROBADO : solicitud.getResultadoSimulado();
                    mensaje = mensajeDePasarela(estado);
                    String sufijo = String.valueOf(System.currentTimeMillis() % 100000000L);
                    switch (solicitud.getMedioPago().getTipo()) {
                        case PSE: referencia = "PSE-" + sufijo; break;
                        case TARJETA: referencia = "TRX-" + sufijo; break;
                        default:
                            String comp = solicitud.getReferenciaCliente();
                            referencia = "TRANS-" + (comp == null || comp.isEmpty() ? sufijo : comp);
                    }
                }
            }

            // 3. Registrar el pago
            pago.setEstado(estado);
            pago.setReferenciaExterna(referencia);
            pagoDAO.insertar(con, pago);

            // 4. Consecuencias del resultado
            res.setEstadoPago(estado);
            res.setMensaje(mensaje);
            res.setReferencia(referencia);

            if (Pago.APROBADO.equals(estado)) {
                pagoDAO.confirmarReserva(con, resumen.getIdReserva());
                List<String> tiquetes = tiqueteDAO.emitirTiquetes(con, resumen.getIdReserva(), resumen.getCodigoReserva());
                res.getTiquetes().addAll(tiquetes);
                res.setCorreoEnviado(notificacionDAO.registrarConfirmacion(con, resumen, tiquetes));
                res.setReservaConfirmada(true);
            } else {
                // CA-23: la reserva sigue bloqueada hasta fecha_expiracion
                res.setReservaConfirmada(false);
                res.setPuedeReintentar(!Pago.PENDIENTE.equals(estado));
                res.setMensaje(mensaje + " La reserva permanece bloqueada hasta "
                        + resumen.getFechaExpiracion().toLocalDateTime().toString().replace('T', ' ') + ".");
            }
            con.commit();
            return res;

        } catch (SQLException e) {
            if (con != null) { try { con.rollback(); } catch (SQLException ignorada) { } }
            return resultado(res, Pago.ERROR_COMUNICACION,
                    "Error de comunicación con la base de datos: " + e.getMessage()
                    + ". La reserva permanece bloqueada; puede reintentar.", null, true);
        } finally {
            DaoUtil.cerrar(con);
        }
    }

    private String mensajeDePasarela(String estado) {
        if (Pago.APROBADO.equals(estado)) return "Pago aprobado por la entidad financiera.";
        if (Pago.RECHAZADO.equals(estado)) return "La entidad financiera rechazó el pago.";
        if (Pago.CANCELADO.equals(estado)) return "El pago fue cancelado.";
        if (Pago.EXPIRADO.equals(estado)) return "El tiempo para completar el pago en la entidad expiró.";
        if (Pago.PENDIENTE.equals(estado)) return "El pago está pendiente de confirmación por la entidad.";
        return "No hubo comunicación con la entidad financiera.";
    }

    private ResultadoPago resultado(ResultadoPago r, String estado, String mensaje,
                                    String referencia, boolean reintento) {
        r.setEstadoPago(estado);
        r.setMensaje(mensaje);
        r.setReferencia(referencia);
        r.setPuedeReintentar(reintento);
        return r;
    }
}
