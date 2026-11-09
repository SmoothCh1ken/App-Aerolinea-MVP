package modelo.modulo_cambios;

/**
 * Reserva afectada por un vuelo cancelado o reprogramado. El aviso se envía una sola vez,
 * al correo del COMPRADOR de la reserva (los pasajeros, incluidos niños y bebés, no reciben nada).
 */
public class DestinatarioDTO {
    public int idNotificacion, idReserva, idComprador;
    public String codigoReserva, titular, correo;
    /** Pasajeros de la reserva que viajan en el vuelo afectado, separados por coma. */
    public String pasajeros;
    /** Tramos afectados: "Ida", "Regreso" o "Ida y Regreso". */
    public String sentidos;
}
