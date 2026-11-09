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

    /** Datos de un tiquete (un pasajero en un tramo) listos para dibujar el PDF. */
    public static class DatosTiquete {
        public final String numeroTiquete, pasajero, documento, sentido,
                numeroVuelo, origen, destino, clase;
        public final java.sql.Timestamp salida;

        DatosTiquete(String numeroTiquete, String pasajero, String documento, String sentido,
                     String numeroVuelo, String origen, String destino,
                     java.sql.Timestamp salida, String clase) {
            this.numeroTiquete = numeroTiquete; this.pasajero = pasajero;
            this.documento = documento; this.sentido = sentido;
            this.numeroVuelo = numeroVuelo; this.origen = origen;
            this.destino = destino; this.salida = salida; this.clase = clase;
        }
    }

    private static final String SQL_DATOS_PDF =
            "SELECT t.numero_tiquete, CONCAT(pe.nombres, ' ', pe.apellidos) AS pasajero, "
          + "       pe.numero_documento, tp.sentido, v.numero_vuelo, "
          + "       CONCAT(co.nombre, ' (', ao.codigo_iata, ')') AS origen, "
          + "       CONCAT(cd.nombre, ' (', ad.codigo_iata, ')') AS destino, "
          + "       v.fecha_hora_salida, cs.nombre AS clase "
          + "FROM Tiquete t "
          + "JOIN Reserva_pasajero rp ON rp.id_reserva_pasajero = t.id_reserva_pasajero "
          + "JOIN Persona pe ON pe.id_persona = rp.id_persona "
          + "JOIN Tramo_pasajero tp ON tp.id_reserva_pasajero = rp.id_reserva_pasajero "
          + "JOIN Vuelo v ON v.id_vuelo = tp.id_vuelo "
          + "JOIN Ruta ru ON ru.id_ruta = v.id_ruta "
          + "JOIN Aeropuerto ao ON ao.id_aeropuerto = ru.id_aeropuerto_origen "
          + "JOIN Aeropuerto ad ON ad.id_aeropuerto = ru.id_aeropuerto_destino "
          + "JOIN Ciudad co ON co.id_ciudad = ao.id_ciudad "
          + "JOIN Ciudad cd ON cd.id_ciudad = ad.id_ciudad "
          + "JOIN Tarifa ta ON ta.id_tarifa = tp.id_tarifa "
          + "JOIN Clase_servicio cs ON cs.id_clase = ta.id_clase "
          + "WHERE rp.id_reserva = ? "
          + "ORDER BY rp.id_reserva_pasajero, tp.sentido";

    /**
     * Un registro por pasajero y por tramo (Ida / Regreso) de la reserva.
     * Debe llamarse DESPUÉS de emitirTiquetes (hace JOIN con Tiquete).
     */
    public List<DatosTiquete> consultarDatosParaPDF(Connection con, int idReserva)
            throws SQLException {
        List<DatosTiquete> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(SQL_DATOS_PDF)) {
            ps.setInt(1, idReserva);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new DatosTiquete(
                            rs.getString("numero_tiquete"), rs.getString("pasajero"),
                            rs.getString("numero_documento"), rs.getString("sentido"),
                            rs.getString("numero_vuelo"), rs.getString("origen"),
                            rs.getString("destino"), rs.getTimestamp("fecha_hora_salida"),
                            rs.getString("clase")));
                }
            }
        }
        return lista;
    }
}
