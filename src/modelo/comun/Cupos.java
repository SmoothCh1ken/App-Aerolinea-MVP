package modelo.comun;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Regla única de disponibilidad, compartida por Consulta, Reserva y Cambios.
 *
 * Cupos libres = cupos_totales de Vuelo_tarifa - cupos ocupados, donde un cupo está ocupado si:
 *  - el pasajero ocupa asiento (Categoria_pasajero.ocupa_cupo = 1: los menores de 2 años no), y
 *  - su reserva está Confirmada, o está "Pendiente de pago" y todavía no venció su plazo.
 * Las reservas Canceladas, Expiradas o con el plazo vencido liberan el cupo (RF-19, CA-24)
 * sin depender de que alguien ejecute antes la limpieza de reservas expiradas.
 */
public final class Cupos {

    private Cupos() { }

    /**
     * Subconsulta SQL (entre paréntesis) con los cupos ocupados de un vuelo y una tarifa.
     * @param colVuelo  columna o marcador con el id del vuelo (ej. "v.id_vuelo" o "?")
     * @param colTarifa columna o marcador con el id de la tarifa (ej. "vt.id_tarifa" o "?")
     */
    public static String sqlOcupados(String colVuelo, String colTarifa) {
        return "(SELECT COUNT(*) FROM Tramo_pasajero tp_o "
             + "JOIN Reserva_pasajero rp_o ON rp_o.id_reserva_pasajero = tp_o.id_reserva_pasajero "
             + "JOIN Categoria_pasajero cp_o ON cp_o.id_categoria = rp_o.id_categoria "
             + "JOIN Reserva r_o ON r_o.id_reserva = rp_o.id_reserva "
             + "WHERE tp_o.id_vuelo = " + colVuelo + " AND tp_o.id_tarifa = " + colTarifa + " "
             + "AND cp_o.ocupa_cupo = 1 "
             + "AND (r_o.estado = 'Confirmada' "
             + "     OR (r_o.estado = 'Pendiente de pago' AND r_o.fecha_expiracion >= NOW())))";
    }

    /** Cupos ocupados de un vuelo y tarifa. */
    public static int ocupados(Connection con, int idVuelo, int idTarifa) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT " + sqlOcupados("?", "?"))) {
            ps.setInt(1, idVuelo);
            ps.setInt(2, idTarifa);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
