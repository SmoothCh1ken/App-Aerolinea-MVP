package modelo.modulo_pago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Registra (y despacha) la confirmación de compra enviada al correo del comprador. */
public class NotificacionDAO {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** @return true si el correo se envió; false si falló o el comprador no tiene correo válido. */
    public boolean registrarConfirmacion(Connection con, ResumenCompra r, List<String> tiquetes)
            throws SQLException {
        String correo = r.getCorreoComprador();
        boolean correoValido = correo != null && correo.contains("@");

        String asunto = "Confirmación de compra " + r.getCodigoReserva();
        String mensaje = construirMensaje(r, tiquetes);
        boolean enviado = correoValido && ServicioCorreo.enviar(correo, asunto, mensaje);

        String sql = "INSERT INTO Notificacion (id_reserva, id_persona, id_vuelo, tipo, asunto, "
                   + "mensaje, correo_destino, estado) VALUES (?, ?, ?, 'Confirmaci\u00f3n de compra', ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getIdReserva());
            ps.setInt(2, r.getIdComprador());
            ps.setNull(3, Types.INTEGER);
            ps.setString(4, asunto);
            ps.setString(5, mensaje);
            ps.setString(6, correoValido ? correo : "");
            ps.setString(7, enviado ? "Enviada" : "Fallida");
            ps.executeUpdate();
        }
        return enviado;
    }

    private String construirMensaje(ResumenCompra r, List<String> tiquetes) {
        NumberFormat moneda = NumberFormat.getInstance(Locale.forLanguageTag("es-CO"));
        StringBuilder sb = new StringBuilder();
        sb.append("Hola ").append(r.getNombreComprador()).append(",\n");
        sb.append("Tu pago fue aprobado y la reserva ").append(r.getCodigoReserva())
          .append(" quedó confirmada.\n\nItinerario:\n");
        for (LineaCompra l : r.getLineas()) {
            sb.append("- ").append(l.getSentido()).append(": vuelo ").append(l.getNumeroVuelo())
              .append(" ").append(l.getOrigen()).append(" -> ").append(l.getDestino())
              .append(", salida ").append(l.getFechaSalida().toLocalDateTime().format(FORMATO_FECHA))
              .append(" (").append(l.getPasajero()).append(")\n");
        }
        sb.append("\nTiquetes electrónicos: ").append(String.join(", ", tiquetes));
        sb.append("\nTotal pagado: $").append(moneda.format(r.getTotal())).append(" COP");
        return sb.toString();
    }
}
