package modelo.modulo_cambios;

import modelo.modulo_pago.ServicioCorreo;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Envía los correos reales del módulo 4 (vuelo cancelado/reprogramado por la aerolínea, cambio de
 * vuelo y cancelación de reserva por solicitud del cliente) y deja constancia del resultado en la
 * tabla Notificacion. Siempre se escribe al COMPRADOR de la reserva, un solo correo por reserva.
 * Un fallo de correo nunca deshace la operación de negocio.
 */
public class NotificadorCambios {

    /*
     * Valores del ENUM Notificacion.tipo. 'Cambio de vuelo' y 'Cancelación de reserva' se agregan con
     * el script actualizar_enum_notificacion.sql; así un trámite del cliente nunca se registra
     * como "Vuelo cancelado" / "Vuelo reprogramado", que son decisiones de la aerolínea.
     */
    public static final String TIPO_CAMBIO_VUELO = "Cambio de vuelo";
    public static final String TIPO_CANCELACION_RESERVA = "Cancelación de reserva";

    private final CambiosDAO dao = new CambiosDAO();

    private boolean correoValido(String correo) {
        return correo != null && correo.contains("@");
    }

    /**
     * Avisa al comprador de cada reserva afectada por un vuelo cancelado o reprogramado
     * por la aerolínea: un correo por reserva, que lista los pasajeros afectados.
     */
    public ResumenNotificacion avisarVuelo(ArrayList<DestinatarioDTO> reservas, String asunto, String mensaje) {
        ResumenNotificacion resumen = new ResumenNotificacion();

        for (DestinatarioDTO d : reservas) {
            String cuerpo = "Hola " + d.titular + ",\n\n" + mensaje
                    + "\n\nReserva: " + d.codigoReserva
                    + "\nTramo(s) afectado(s): " + d.sentidos
                    + "\nPasajero(s) afectado(s): " + d.pasajeros
                    + "\n\nAtentamente,\nAirColombia";

            boolean enviado = correoValido(d.correo) && ServicioCorreo.enviar(d.correo, asunto, cuerpo);
            try {
                dao.actualizarEstadoNotificacion(d.idNotificacion, enviado);
            } catch (SQLException e) {
                System.err.println("[Notificador] No se pudo actualizar la notificación "
                        + d.idNotificacion + ": " + e.getMessage());
            }
            resumen.agregar(enviado);
        }
        return resumen;
    }

    /** Envía un correo al comprador de la reserva y lo registra en Notificacion. */
    public boolean avisarReserva(ReservaInfoDTO r, String tipo, String asunto, String mensaje) {
        boolean valido = correoValido(r.correo);
        boolean enviado = valido && ServicioCorreo.enviar(r.correo, asunto, mensaje);
        try {
            dao.registrarNotificacion(r.idReserva, r.idComprador, tipo, asunto, mensaje,
                    valido ? r.correo : "", enviado);
        } catch (SQLException e) {
            System.err.println("[Notificador] No se pudo registrar la notificación '" + tipo + "': "
                    + e.getMessage());
        }
        return enviado;
    }
}
