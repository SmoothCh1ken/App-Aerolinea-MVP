package modelo.modulo_reportes;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/** Calcula los indicadores del tablero ejecutivo (RF-34) leyendo la base en el momento de la consulta. */
public class TableroDAO {

    public IndicadoresTablero obtener(FiltroReporte f) throws SQLException {
        IndicadoresTablero t = new IndicadoresTablero();
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            ventas(con, f, t);
            servicios(con, f, t);
            ocupacion(con, f, t);
            reservas(con, f, t);
            vuelos(con, f, t);
            pagos(con, f, t);
            reembolsos(con, f, t);
        } finally {
            DaoUtil.cerrar(con);
        }
        t.setActualizadoEn(LocalDateTime.now());
        return t;
    }

    private void ventas(Connection con, FiltroReporte f, IndicadoresTablero t) throws SQLException {
        ConsultaBuilder q = new ConsultaBuilder().fechas(f, "r.fecha_creacion").geografia(f);
        String sql = "SELECT COUNT(*), COALESCE(SUM(tp.precio_base - tp.descuento + tp.impuestos), 0) "
                   + "FROM Tramo_pasajero tp " + SqlComun.JOIN_RESERVA_DE_TRAMO
                   + "JOIN Vuelo v ON v.id_vuelo = tp.id_vuelo " + SqlComun.JOIN_RUTA_DE_VUELO
                   + "WHERE r.estado = 'Confirmada'" + q.condiciones();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            q.asignar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    t.setTiquetesVendidos(rs.getLong(1));
                    t.setVentasTotal(rs.getBigDecimal(2));
                }
            }
        }
    }

    private void servicios(Connection con, FiltroReporte f, IndicadoresTablero t) throws SQLException {
        ConsultaBuilder q = new ConsultaBuilder().fechas(f, "r.fecha_creacion").geografia(f);
        String sql = "SELECT COALESCE(SUM(sc.cantidad * sc.valor_unitario), 0) "
                   + "FROM Servicio_contratado sc "
                   + "JOIN Tramo_pasajero tp ON tp.id_tramo_pasajero = sc.id_tramo_pasajero "
                   + SqlComun.JOIN_RESERVA_DE_TRAMO
                   + "JOIN Vuelo v ON v.id_vuelo = tp.id_vuelo " + SqlComun.JOIN_RUTA_DE_VUELO
                   + "WHERE r.estado = 'Confirmada'" + q.condiciones();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            q.asignar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    t.setIngresosServicios(rs.getBigDecimal(1));
                }
            }
        }
    }

    private void ocupacion(Connection con, FiltroReporte f, IndicadoresTablero t) throws SQLException {
        ConsultaBuilder q = new ConsultaBuilder().fechas(f, "v.fecha_hora_salida").geografia(f);
        String sql = "SELECT COALESCE(SUM(x.cupos), 0), COALESCE(SUM(x.ocupados), 0) FROM ("
                   + "SELECT " + SqlComun.CUPOS_DEL_VUELO + " AS cupos, "
                   + SqlComun.OCUPADOS_DEL_VUELO + " AS ocupados "
                   + SqlComun.FROM_VUELO
                   + "WHERE v.estado <> 'Cancelado'" + q.condiciones() + ") x";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            q.asignar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long cupos = rs.getLong(1);
                    long ocupados = rs.getLong(2);
                    t.setOcupacionPromedio(cupos == 0 ? 0 : ocupados * 100.0 / cupos);
                }
            }
        }
    }

    private void reservas(Connection con, FiltroReporte f, IndicadoresTablero t) throws SQLException {
        ConsultaBuilder q = new ConsultaBuilder().fechas(f, "r.fecha_creacion").geografiaDeReserva(f, "r.id_reserva");
        String sql = "SELECT r.estado, COUNT(*) FROM Reserva r WHERE 1 = 1" + q.condiciones()
                   + " GROUP BY r.estado";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            q.asignar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String estado = rs.getString(1);
                    if ("Confirmada".equals(estado)) {
                        t.setReservasConfirmadas(rs.getLong(2));
                    } else if ("Pendiente de pago".equals(estado)) {
                        t.setReservasPendientes(rs.getLong(2));
                    }
                }
            }
        }
    }

    private void vuelos(Connection con, FiltroReporte f, IndicadoresTablero t) throws SQLException {
        ConsultaBuilder q = new ConsultaBuilder().fechas(f, "v.fecha_hora_salida").geografia(f);
        String sql = "SELECT v.estado, COUNT(*) " + SqlComun.FROM_VUELO
                   + "WHERE 1 = 1" + q.condiciones() + " GROUP BY v.estado";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            q.asignar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String estado = rs.getString(1);
                    if ("Reprogramado".equals(estado)) {
                        t.setVuelosReprogramados(rs.getLong(2));
                    } else if ("Cancelado".equals(estado)) {
                        t.setVuelosCancelados(rs.getLong(2));
                    }
                }
            }
        }
    }

    private void pagos(Connection con, FiltroReporte f, IndicadoresTablero t) throws SQLException {
        ConsultaBuilder q = new ConsultaBuilder().fechas(f, "p.fecha_hora").geografiaDeReserva(f, "p.id_reserva");
        String sql = "SELECT p.estado, COUNT(*) FROM Pago p WHERE 1 = 1" + q.condiciones()
                   + " GROUP BY p.estado";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            q.asignar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                long total = 0;
                while (rs.next()) {
                    long n = rs.getLong(2);
                    total += n;
                    if ("Aprobado".equals(rs.getString(1))) {
                        t.setPagosAprobados(n);
                    }
                }
                t.setPagosTotal(total);
            }
        }
    }

    private void reembolsos(Connection con, FiltroReporte f, IndicadoresTablero t) throws SQLException {
        ConsultaBuilder q = new ConsultaBuilder().fechas(f, "rb.fecha_solicitud").geografiaDeReserva(f, "rb.id_reserva");
        String sql = "SELECT COALESCE(SUM(rb.valor_reembolso), 0) FROM Reembolso rb WHERE 1 = 1" + q.condiciones();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            q.asignar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal v = rs.getBigDecimal(1);
                    t.setReembolsos(v == null ? BigDecimal.ZERO : v);
                }
            }
        }
    }
}
