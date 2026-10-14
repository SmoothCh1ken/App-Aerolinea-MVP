package modelo.modulo_pago;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Arma el resumen de compra (RF-17) a partir de la reserva. */
public class ResumenCompraDAO {

    private static final String SQL_RESERVA =
            "SELECT r.id_reserva, r.codigo_reserva, r.estado, r.fecha_expiracion, "
          + "       p.id_persona, CONCAT(p.nombres, ' ', p.apellidos) AS comprador, p.correo "
          + "FROM Reserva r JOIN Persona p ON p.id_persona = r.id_comprador "
          + "WHERE r.codigo_reserva = ?";

    private static final String SQL_LINEAS =
            "SELECT CONCAT(pe.nombres, ' ', pe.apellidos) AS pasajero, cp.nombre AS categoria, "
          + "       tp.sentido, v.numero_vuelo, ao.codigo_iata AS origen, ad.codigo_iata AS destino, "
          + "       v.fecha_hora_salida, t.nombre AS tarifa, "
          + "       tp.precio_base, tp.descuento, tp.impuestos, "
          + "       COALESCE((SELECT SUM(sc.cantidad * sc.valor_unitario) FROM Servicio_contratado sc "
          + "                 WHERE sc.id_tramo_pasajero = tp.id_tramo_pasajero), 0) AS servicios, "
          + "       (SELECT GROUP_CONCAT(CONCAT(s.nombre, ' x', sc2.cantidad) SEPARATOR ', ') "
          + "          FROM Servicio_contratado sc2 JOIN Servicio s ON s.id_servicio = sc2.id_servicio "
          + "         WHERE sc2.id_tramo_pasajero = tp.id_tramo_pasajero) AS detalle_servicios "
          + "FROM Tramo_pasajero tp "
          + "JOIN Reserva_pasajero rp ON rp.id_reserva_pasajero = tp.id_reserva_pasajero "
          + "JOIN Persona pe ON pe.id_persona = rp.id_persona "
          + "JOIN Categoria_pasajero cp ON cp.id_categoria = rp.id_categoria "
          + "JOIN Vuelo v ON v.id_vuelo = tp.id_vuelo "
          + "JOIN Ruta ru ON ru.id_ruta = v.id_ruta "
          + "JOIN Aeropuerto ao ON ao.id_aeropuerto = ru.id_aeropuerto_origen "
          + "JOIN Aeropuerto ad ON ad.id_aeropuerto = ru.id_aeropuerto_destino "
          + "JOIN Tarifa t ON t.id_tarifa = tp.id_tarifa "
          + "WHERE rp.id_reserva = ? "
          + "ORDER BY rp.id_reserva_pasajero, tp.sentido";

    /** @return el resumen, o null si no existe una reserva con ese código. */
    public ResumenCompra buscarPorCodigo(String codigo) throws SQLException {
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            ResumenCompra resumen = null;

            try (PreparedStatement ps = con.prepareStatement(SQL_RESERVA)) {
                ps.setString(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        resumen = new ResumenCompra(
                                rs.getInt("id_reserva"), rs.getString("codigo_reserva"),
                                rs.getString("estado"), rs.getTimestamp("fecha_expiracion"),
                                rs.getInt("id_persona"), rs.getString("comprador"),
                                rs.getString("correo"));
                    }
                }
            }
            if (resumen == null) return null;

            try (PreparedStatement ps = con.prepareStatement(SQL_LINEAS)) {
                ps.setInt(1, resumen.getIdReserva());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        BigDecimal servicios = rs.getBigDecimal("servicios");
                        resumen.agregarLinea(new LineaCompra(
                                rs.getString("pasajero"), rs.getString("categoria"),
                                rs.getString("sentido"), rs.getString("numero_vuelo"),
                                rs.getString("origen"), rs.getString("destino"),
                                rs.getTimestamp("fecha_hora_salida"), rs.getString("tarifa"),
                                rs.getBigDecimal("precio_base"), rs.getBigDecimal("descuento"),
                                rs.getBigDecimal("impuestos"),
                                servicios == null ? BigDecimal.ZERO : servicios,
                                rs.getString("detalle_servicios")));
                    }
                }
            }
            return resumen;
        } finally {
            DaoUtil.cerrar(con);
        }
    }
}
