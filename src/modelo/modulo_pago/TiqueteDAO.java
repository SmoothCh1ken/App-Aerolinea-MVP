package modelo.modulo_pago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Emisión del tiquete electrónico (RF-20). */
public class TiqueteDAO {

    /**
     * Emite un tiquete por cada pasajero de la reserva que aún no tenga uno.
     * Solo debe llamarse cuando el pago fue aprobado (RN de RF-20).
     * @return los números de tiquete emitidos, p. ej. TKT-RES-002-003
     */
    public List<String> emitirTiquetes(Connection con, int idReserva, String codigoReserva)
            throws SQLException {
        List<Integer> pendientes = new ArrayList<>();
        String sqlPend = "SELECT rp.id_reserva_pasajero FROM Reserva_pasajero rp "
                + "LEFT JOIN Tiquete t ON t.id_reserva_pasajero = rp.id_reserva_pasajero "
                + "WHERE rp.id_reserva = ? AND t.id_tiquete IS NULL ORDER BY rp.id_reserva_pasajero";
        try (PreparedStatement ps = con.prepareStatement(sqlPend)) {
            ps.setInt(1, idReserva);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) pendientes.add(rs.getInt(1));
            }
        }

        List<String> numeros = new ArrayList<>();
        String sqlIns = "INSERT INTO Tiquete (id_reserva_pasajero, numero_tiquete, estado) "
                      + "VALUES (?, ?, 'Emitido')";
        for (int idRp : pendientes) {
            String numero = String.format("TKT-%s-%03d", codigoReserva, idRp);
            try (PreparedStatement ps = con.prepareStatement(sqlIns)) {
                ps.setInt(1, idRp);
                ps.setString(2, numero);
                ps.executeUpdate();
            }
            numeros.add(numero);
        }
        return numeros;
    }
}
