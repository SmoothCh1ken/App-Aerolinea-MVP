package controlador.modulo_cambios;

import modelo.modulo_cambios.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Reglas de negocio del módulo 4. Se apoya en el DAO para leer/guardar datos.
 * Cuando una regla no se cumple lanza una Exception con el mensaje para el usuario.
 */
public class CambiosController {

    private final CambiosDAO dao = new CambiosDAO();
    private final NotificadorCambios notificador = new NotificadorCambios();
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Formato de dinero para los correos: $1.234.567 */
    private String dinero(BigDecimal valor) {
        NumberFormat nf = NumberFormat.getInstance(Locale.forLanguageTag("es-CO"));
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(2);
        return "$" + nf.format(valor);
    }

    // ---------- métodos de apoyo ----------

    /** Un vuelo cancelado o reprogramado hace que el cambio sea "operacional" (sin cargos). */
    private boolean esOperacional(TramoInfoDTO t) {
        return !t.estadoVuelo.equals("Programado");
    }

    /** Texto "el vuelo AV289 (reprogramado)" con los vuelos que la AEROLÍNEA modificó; null si ninguno. */
    private String vuelosModificadosPorAerolinea(java.util.List<TramoInfoDTO> tramos) {
        ArrayList<String> vuelos = new ArrayList<>();
        for (TramoInfoDTO t : tramos) {
            if (esOperacional(t)) {
                String v = t.numeroVuelo + " (" + t.estadoVuelo.toLowerCase() + ")";
                if (!vuelos.contains(v)) {
                    vuelos.add(v);
                }
            }
        }
        if (vuelos.isEmpty()) {
            return null;
        }
        return (vuelos.size() == 1 ? "el vuelo " : "los vuelos ") + String.join(", ", vuelos);
    }

    private long horasHasta(LocalDateTime fecha) {
        return Duration.between(LocalDateTime.now(), fecha).toHours();
    }

    private void exigirConfirmada(ReservaInfoDTO r) throws Exception {
        if (!r.estado.equals("Confirmada")) {
            throw new Exception("La reserva está en estado '" + r.estado + "' y no admite cambios ni cancelaciones.");
        }
    }

    /** Precio base de un tramo en el vuelo nuevo = precio del vuelo x factor de la categoría del pasajero. */
    private BigDecimal precioNuevo(OpcionCambioDTO o, TramoInfoDTO t) {
        return o.precio.multiply(t.factor).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal impuestoNuevo(OpcionCambioDTO o, BigDecimal precioBase) {
        return precioBase.multiply(o.porcImpuesto).divide(CIEN, 2, RoundingMode.HALF_UP);
    }

    // ---------- consultar la reserva ----------

    public ReservaInfoDTO consultar(String codigo, String apellido) throws Exception {
        if (codigo.trim().isEmpty() || apellido.trim().isEmpty()) {
            throw new Exception("Ingrese el código de reserva y el apellido del titular.");
        }
        ReservaInfoDTO r = dao.buscarReserva(codigo.trim().toUpperCase(), apellido.trim());
        if (r == null) {
            throw new Exception("Código de reserva o apellido incorrectos.");
        }
        return r;
    }

    // ---------- cambiar de vuelo ----------

    /** Vuelos alternos para un tramo (solo si la tarifa permite cambios). */
    public ArrayList<OpcionCambioDTO> opciones(ReservaInfoDTO r, String sentido) throws Exception {
        exigirConfirmada(r);

        ArrayList<TramoInfoDTO> tramos = r.tramos(sentido);
        if (tramos.isEmpty()) {
            throw new Exception("La reserva no tiene tramo de " + sentido.toLowerCase() + ".");
        }
        TramoInfoDTO ref = tramos.get(0);

        // Si el cambio es voluntario, se revisan las condiciones de la tarifa
        if (!esOperacional(ref)) {
            for (TramoInfoDTO t : tramos) {
                if (!t.permiteCambios) {
                    throw new Exception("La tarifa " + t.tarifa + " no permite modificaciones.");
                }
                if (horasHasta(t.salida) < t.horasMinimas) {
                    throw new Exception("La tarifa " + t.tarifa + " exige modificar con al menos "
                            + t.horasMinimas + " horas de antelación.");
                }
            }
        }

        // Cuántos pasajeros ocupan cupo
        int plazas = 0;
        for (TramoInfoDTO t : tramos) {
            if (t.ocupaCupo) {
                plazas++;
            }
        }

        // El otro tramo (si la reserva es ida y regreso)
        String sentidoOtro = sentido.equals("Ida") ? "Regreso" : "Ida";
        ArrayList<TramoInfoDTO> otros = r.tramos(sentidoOtro);

        ArrayList<OpcionCambioDTO> resultado = new ArrayList<>();
        for (OpcionCambioDTO o : dao.buscarOpciones(ref.idRuta, ref.idVuelo, ref.idTarifa, plazas)) {
            if (!otros.isEmpty()) {
                TramoInfoDTO otro = otros.get(0);
                // El regreso siempre debe ser después de la ida
                if (sentido.equals("Ida") && !o.llegada.isBefore(otro.salida)) {
                    continue;
                }
                if (sentido.equals("Regreso") && !o.salida.isAfter(otro.llegada)) {
                    continue;
                }
            }
            resultado.add(o);
        }
        return resultado;
    }

    /**
     * Calcula lo que cuesta el cambio:
     *   diferencia = (precio nuevo) - (precio actual), sumado por pasajero
     *   neto       = diferencia + cargo de cambio
     * neto > 0 el cliente paga; neto < 0 queda saldo a favor.
     */
    public CotizacionDTO cotizarCambio(ReservaInfoDTO r, String sentido, OpcionCambioDTO o) {
        ArrayList<TramoInfoDTO> tramos = r.tramos(sentido);
        boolean operacional = esOperacional(tramos.get(0));

        CotizacionDTO q = new CotizacionDTO();
        q.motivo = operacional ? "Operacional" : "Voluntario";
        if (operacional) {
            q.avisoAerolinea = "No se cobra cargo por cambio porque la aerolínea modificó "
                    + vuelosModificadosPorAerolinea(tramos) + ".";
        }

        for (TramoInfoDTO t : tramos) {
            BigDecimal base = precioNuevo(o, t);
            BigDecimal impuesto = impuestoNuevo(o, base);
            BigDecimal totalNuevo = base.subtract(t.descuento).add(impuesto);

            q.diferencia = q.diferencia.add(totalNuevo.subtract(t.total()));

            // El cargo se cobra por cada pasajero que ocupa cupo (y no si el cambio es operacional)
            if (!operacional && t.ocupaCupo) {
                q.cargo = q.cargo.add(t.cargoCambio);
            }
        }
        q.neto = q.diferencia.add(q.cargo);
        return q;
    }

    /** Aplica el cambio y envía los correos de confirmación. Devuelve el id del cambio y el estado de los correos. */
    public ResultadoCambio cambiar(ReservaInfoDTO r, String sentido, OpcionCambioDTO o) throws Exception {
        // Se vuelve a validar que el vuelo siga disponible
        OpcionCambioDTO elegida = null;
        for (OpcionCambioDTO x : opciones(r, sentido)) {
            if (x.idVuelo == o.idVuelo) {
                elegida = x;
            }
        }
        if (elegida == null) {
            throw new Exception("El vuelo seleccionado ya no está disponible para esta reserva.");
        }

        // Calcular los precios nuevos de cada tramo para que el DAO los guarde
        for (TramoInfoDTO t : r.tramos(sentido)) {
            t.nuevoPrecioBase = precioNuevo(elegida, t);
            t.nuevoImpuestos = impuestoNuevo(elegida, t.nuevoPrecioBase);
        }

        CotizacionDTO q = cotizarCambio(r, sentido, elegida);
        TramoInfoDTO referencia = r.tramos(sentido).get(0);

        ResultadoCambio resultado = new ResultadoCambio();
        resultado.idCambio = dao.registrarCambio(r, sentido, referencia, elegida, q);

        // Un solo correo al comprador (si falla, el cambio ya quedó registrado)
        String asunto = q.neto.signum() < 0
                ? "Modificación de vuelo y confirmación de reembolso - Reserva " + r.codigo
                : "Modificación de vuelo - Reserva " + r.codigo;
        resultado.correos.agregar(notificador.avisarReserva(r, NotificadorCambios.TIPO_CAMBIO_VUELO,
                asunto, mensajeCambio(r, sentido, referencia, elegida, q, resultado.idCambio)));
        return resultado;
    }

    private String mensajeCambio(ReservaInfoDTO r, String sentido, TramoInfoDTO anterior,
                                 OpcionCambioDTO nuevo, CotizacionDTO q, int idCambio) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hola ").append(r.titular).append(",\n\n");
        if (q.neto.signum() > 0) {
            sb.append("Registramos el cambio #").append(idCambio).append(" que solicitaste en tu reserva ")
              .append(r.codigo).append(". Quedará aplicado una vez se pague la diferencia.\n\n");
        } else {
            sb.append("El cambio #").append(idCambio).append(" que solicitaste en tu reserva ")
              .append(r.codigo).append(" fue aplicado correctamente.\n\n");
        }
        sb.append("Tramo modificado: ").append(sentido).append("\n");
        sb.append("Vuelo anterior: ").append(anterior.numeroVuelo).append(" ").append(anterior.origen)
          .append(" -> ").append(anterior.destino).append(", salida ").append(anterior.salida.format(FORMATO)).append("\n");
        sb.append("Vuelo nuevo: ").append(nuevo.numeroVuelo).append(" ").append(anterior.origen)
          .append(" -> ").append(anterior.destino).append(", salida ").append(nuevo.salida.format(FORMATO)).append("\n");
        sb.append("Pasajeros: ");
        ArrayList<TramoInfoDTO> tramos = r.tramos(sentido);
        for (int i = 0; i < tramos.size(); i++) {
            sb.append(i > 0 ? ", " : "").append(tramos.get(i).pasajero);
        }
        sb.append("\n\nCargo por cambio: ").append(dinero(q.cargo));
        sb.append("\nDiferencia de tarifa: ").append(dinero(q.diferencia));
        if (q.avisoAerolinea != null) {
            sb.append("\n").append(q.avisoAerolinea);
        }
        if (q.neto.signum() > 0) {
            sb.append("\nValor a pagar: ").append(dinero(q.neto));
        } else if (q.neto.signum() < 0) {
            sb.append("\nSaldo a favor: ").append(dinero(q.neto.negate()));
            sb.append("\n\nConfirmación de reembolso: registramos la solicitud de reembolso por ese saldo a favor")
              .append(" (sin penalidad). Estado: Solicitado.");
        }
        sb.append("\n\nAtentamente,\nAirColombia");
        return sb.toString();
    }

    // ---------- cancelar y reembolsar ----------

    /**
     * reembolso = total de los tiquetes - penalidad de la tarifa.
     * Si el vuelo fue cancelado o reprogramado no hay penalidad.
     */
    public CotizacionDTO cotizarReembolso(ReservaInfoDTO r) throws Exception {
        exigirConfirmada(r);

        boolean operacional = false;
        for (TramoInfoDTO t : r.tramos) {
            if (esOperacional(t)) {
                operacional = true;
            }
        }

        CotizacionDTO q = new CotizacionDTO();
        q.motivo = operacional ? "Cancelación operacional" : "Cancelación voluntaria";
        if (operacional) {
            q.avisoAerolinea = "No se aplica penalidad porque la aerolínea modificó "
                    + vuelosModificadosPorAerolinea(r.tramos) + ".";
        }

        for (TramoInfoDTO t : r.tramos) {
            q.base = q.base.add(t.total());
            if (operacional) {
                continue;
            }
            if (!t.permiteReembolso) {
                throw new Exception("La tarifa " + t.tarifa + " no permite reembolso.");
            }
            if (horasHasta(t.salida) < t.horasMinimas) {
                throw new Exception("La tarifa " + t.tarifa + " exige cancelar con al menos "
                        + t.horasMinimas + " horas de antelación.");
            }
            BigDecimal penalidadTramo = t.total().multiply(t.penalidad).divide(CIEN, 2, RoundingMode.HALF_UP);
            q.penalidad = q.penalidad.add(penalidadTramo);
        }
        q.reembolso = q.base.subtract(q.penalidad);
        return q;
    }

    /**
     * Cancela la reserva por solicitud del cliente y envía UN solo correo al comprador con la
     * confirmación de la cancelación y del reembolso. No tiene relación con las cancelaciones
     * de vuelo hechas por la aerolínea (esas pasan por cancelarVuelo / reprogramarVuelo).
     */
    public ResumenNotificacion cancelar(ReservaInfoDTO r) throws Exception {
        CotizacionDTO q = cotizarReembolso(r);
        dao.cancelarReserva(r, q);

        String asunto = q.reembolso.signum() > 0
                ? "Cancelación de reserva " + r.codigo + " y confirmación de reembolso"
                : "Cancelación de reserva " + r.codigo;
        ResumenNotificacion correos = new ResumenNotificacion();
        correos.agregar(notificador.avisarReserva(r, NotificadorCambios.TIPO_CANCELACION_RESERVA,
                asunto, mensajeCancelacion(r, q)));
        return correos;
    }

    private String mensajeCancelacion(ReservaInfoDTO r, CotizacionDTO q) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hola ").append(r.titular).append(",\n\n");
        sb.append("Recibimos tu solicitud: tu reserva ").append(r.codigo)
          .append(" fue cancelada y sus tiquetes quedaron anulados.\n\nItinerario cancelado:\n");
        for (TramoInfoDTO t : r.tramos) {
            sb.append("- ").append(t.sentido).append(": vuelo ").append(t.numeroVuelo).append(" ")
              .append(t.origen).append(" -> ").append(t.destino).append(", salida ")
              .append(t.salida.format(FORMATO)).append(" (").append(t.pasajero).append(")\n");
        }
        sb.append("\nConfirmación de reembolso:\n");
        sb.append("Valor de los tiquetes: ").append(dinero(q.base)).append("\n");
        sb.append("Penalidad: ").append(dinero(q.penalidad)).append("\n");
        if (q.avisoAerolinea != null) {
            sb.append(q.avisoAerolinea).append("\n");
        }
        if (q.reembolso.signum() > 0) {
            sb.append("Valor a reembolsar: ").append(dinero(q.reembolso)).append("\n");
            sb.append("Estado de la solicitud de reembolso: Solicitado\n");
        } else {
            sb.append("No hay valor a reembolsar.\n");
        }
        sb.append("\nAtentamente,\nAirColombia");
        return sb.toString();
    }

    public ArrayList<String> historial(int idReserva) throws Exception {
        return dao.historial(idReserva);
    }

    // ---------- operaciones sobre vuelos ----------

    public ArrayList<VueloInfoDTO> vuelos() throws Exception {
        return dao.listarVuelos();
    }

    public VueloInfoDTO vuelo(int id) throws Exception {
        return dao.buscarVuelo(id);
    }

    /** Cancela el vuelo, registra las notificaciones y envía el correo a cada pasajero afectado. */
    public ResumenNotificacion cancelarVuelo(VueloInfoDTO v) throws Exception {
        if (v.estado.equals("Cancelado")) {
            throw new Exception("El vuelo ya está cancelado.");
        }
        String asunto = "Cancelación de Vuelo " + v.numero;
        String mensaje = "Lamentamos informar que su vuelo " + v.numero + " (" + v.ruta + ") del "
                + v.salida.format(FORMATO)
                + " fue cancelado. Puede reubicarse en otro vuelo o solicitar el reembolso sin penalidad.";
        ArrayList<DestinatarioDTO> afectados =
                dao.operarVuelo(v.id, "Cancelado", null, null, "Vuelo cancelado", asunto, mensaje);
        return notificador.avisarVuelo(afectados, asunto, mensaje);
    }

    /** Reprograma el vuelo, registra las notificaciones y envía el correo a cada pasajero afectado. */
    public ResumenNotificacion reprogramarVuelo(VueloInfoDTO v, int horas) throws Exception {
        if (v.estado.equals("Cancelado")) {
            throw new Exception("No se puede reprogramar un vuelo cancelado.");
        }
        if (horas == 0) {
            throw new Exception("Indique un número de horas distinto de cero.");
        }
        LocalDateTime nuevaSalida = v.salida.plusHours(horas);
        LocalDateTime nuevaLlegada = v.llegada.plusHours(horas);
        String asunto = "Cambio Operacional Vuelo " + v.numero;
        String mensaje = "Su vuelo " + v.numero + " (" + v.ruta + ") programado para el " + v.salida.format(FORMATO)
                + " fue reprogramado para el " + nuevaSalida.format(FORMATO)
                + ". Puede aceptar el cambio, reubicarse en otro vuelo o solicitar el reembolso sin penalidad.";
        ArrayList<DestinatarioDTO> afectados =
                dao.operarVuelo(v.id, "Reprogramado", nuevaSalida, nuevaLlegada, "Vuelo reprogramado", asunto, mensaje);
        return notificador.avisarVuelo(afectados, asunto, mensaje);
    }
}
