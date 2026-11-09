package modelo.modulo_pago;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lógica de negocio del pago (RF-18, RF-19 y RF-20).
 * Actualizada para generación y envío de PDF.
 */
public class ProcesadorPago {

    public static final BigDecimal VALOR_MILLA_COP = new BigDecimal("30");

    private final PagoDAO pagoDAO = new PagoDAO();
    private final TiqueteDAO tiqueteDAO = new TiqueteDAO();
    private final NotificacionDAO notificacionDAO = new NotificacionDAO();
    
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static int millasNecesarias(BigDecimal total) {
        return total.divide(VALOR_MILLA_COP, 0, RoundingMode.UP).intValue();
    }

    public ResultadoPago procesar(ResumenCompra resumen, SolicitudPago solicitud) {
        ResultadoPago res = new ResultadoPago();
        Connection con = null;
        List<TiqueteDAO.DatosTiquete> datosPdf = null; // se llena solo si el pago es aprobado
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
                default: { 
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
                
                // Datos reales (documento, clase, vuelo) para los PDF; el correo se envía tras el commit
                datosPdf = tiqueteDAO.consultarDatosParaPDF(con, resumen.getIdReserva());

                res.setReservaConfirmada(true);
            } else {
                res.setReservaConfirmada(false);
                res.setPuedeReintentar(!Pago.PENDIENTE.equals(estado));
                res.setMensaje(mensaje + " La reserva permanece bloqueada hasta "
                        + resumen.getFechaExpiracion().toLocalDateTime().toString().replace('T', ' ') + ".");
            }
            con.commit();

            // Solo con el pago ya confirmado en BD: PDF + correo + registro de la notificación.
            // Un fallo aquí NUNCA revierte el pago.
            if (Pago.APROBADO.equals(estado)) {
                res.setCorreoEnviado(enviarYRegistrarCorreo(resumen, res.getTiquetes(), datosPdf));
            }
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

    /**
     * Genera un PDF por pasajero y tramo (en RAM), envía UN solo correo con todos los
     * adjuntos y deja constancia en la tabla Notificacion con el resultado real.
     */
    private boolean enviarYRegistrarCorreo(ResumenCompra resumen, List<String> tiquetes,
                                           List<TiqueteDAO.DatosTiquete> datos) {
        boolean enviado = false;
        try {
            String correo = resumen.getCorreoComprador();
            if (correo != null && correo.contains("@") && datos != null && !datos.isEmpty()) {
                List<byte[]> pdfs = new ArrayList<>();
                List<String> nombres = new ArrayList<>();
                // Un PDF por tiquete (pasajero) con una página por tramo: Ida y Regreso
                Map<String, List<TiqueteDAO.DatosTiquete>> porTiquete = new LinkedHashMap<>();
                for (TiqueteDAO.DatosTiquete d : datos) {
                    porTiquete.computeIfAbsent(d.numeroTiquete, k -> new ArrayList<>()).add(d);
                }
                for (Map.Entry<String, List<TiqueteDAO.DatosTiquete>> e : porTiquete.entrySet()) {
                    TiqueteDAO.DatosTiquete p = e.getValue().get(0);
                    List<GeneradorTiquetePDF.Tramo> tramos = new ArrayList<>();
                    for (TiqueteDAO.DatosTiquete d : e.getValue()) {
                        tramos.add(new GeneradorTiquetePDF.Tramo(d.sentido, d.numeroVuelo, d.origen,
                                d.destino, d.salida.toLocalDateTime().format(FORMATO_FECHA), d.clase));
                    }
                    pdfs.add(GeneradorTiquetePDF.generar(resumen.getCodigoReserva(), p.numeroTiquete,
                            p.pasajero, p.documento, tramos));
                    nombres.add("Tiquete_" + p.numeroTiquete + ".pdf");
                }
                String asunto = "Confirmación de compra " + resumen.getCodigoReserva();
                String cuerpo = notificacionDAO.construirMensaje(resumen, tiquetes)
                        + "\n\nAdjuntamos tus tiquetes de abordaje en PDF (un archivo por pasajero, con ida y regreso). ¡Feliz viaje!";
                enviado = ServicioCorreo.enviarMultiplesAdjuntos(correo, asunto, cuerpo, pdfs, nombres);
            }
        } catch (Exception e) {
            System.err.println("[Procesador Pago] Error generando/enviando tiquetes PDF: " + e.getMessage());
            e.printStackTrace();
        }

        Connection con2 = null;
        try {
            con2 = DaoUtil.abrir();
            notificacionDAO.registrarResultado(con2, resumen, tiquetes, enviado);
        } catch (SQLException e) {
            System.err.println("[Procesador Pago] No se pudo registrar la notificación: " + e.getMessage());
        } finally {
            DaoUtil.cerrar(con2);
        }
        return enviado;
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