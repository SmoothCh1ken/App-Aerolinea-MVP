package modelo.modulo_reportes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Consultas de los reportes gerenciales (RF-32) con sus filtros (RF-33).
 * Aquí solo hay SQL de lectura: este módulo nunca modifica datos.
 */
public class ReportesDAO {

    /** Convierte una fila del ResultSet en los valores de una fila del reporte. */
    private interface FilaMapper {
        Object[] mapear(ResultSet rs) throws SQLException;
    }

    // =====================================================================
    // LISTAS PARA LOS FILTROS
    // =====================================================================

    public List<OpcionFiltro> listarCiudades() throws SQLException {
        return listarOpciones("SELECT id_ciudad, CONCAT(nombre, ' (', pais, ')') FROM Ciudad ORDER BY nombre");
    }

    public List<OpcionFiltro> listarRutas() throws SQLException {
        return listarOpciones(
                "SELECT ru.id_ruta, CONCAT(ao.codigo_iata, ' - ', ad.codigo_iata) "
              + "FROM Ruta ru "
              + "JOIN Aeropuerto ao ON ao.id_aeropuerto = ru.id_aeropuerto_origen "
              + "JOIN Aeropuerto ad ON ad.id_aeropuerto = ru.id_aeropuerto_destino "
              + "ORDER BY ao.codigo_iata, ad.codigo_iata");
    }

    private List<OpcionFiltro> listarOpciones(String sql) throws SQLException {
        List<OpcionFiltro> lista = new ArrayList<>();
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new OpcionFiltro(rs.getInt(1), rs.getString(2)));
                }
            }
        } finally {
            DaoUtil.cerrar(con);
        }
        return lista;
    }

    // =====================================================================
    // REPORTES
    // =====================================================================

    public ResultadoReporte generar(TipoReporte tipo, FiltroReporte f) throws SQLException {
        switch (tipo) {
            case VENTAS: return ventas(f);
            case OCUPACION: return ocupacion(f);
            case CANCELACIONES: return cancelaciones(f);
            case REPROGRAMACIONES: return reprogramaciones(f);
            case AERONAVES: return aeronaves(f);
            default: return servicios(f);
        }
    }

    /** Tiquetes vendidos e ingresos por período (solo reservas confirmadas). */
    private ResultadoReporte ventas(FiltroReporte f) throws SQLException {
        ConsultaBuilder q = new ConsultaBuilder().fechas(f, "r.fecha_creacion").geografia(f);
        String sql = "SELECT DATE_FORMAT(r.fecha_creacion, '" + f.getPeriodo().getFormatoSql() + "') AS periodo, "
                   + "COUNT(*) AS tiquetes, "
                   + "SUM(tp.precio_base - tp.descuento) AS tarifas, "
                   + "SUM(tp.impuestos) AS impuestos, "
                   + "SUM(tp.precio_base - tp.descuento + tp.impuestos) AS total "
                   + "FROM Tramo_pasajero tp " + SqlComun.JOIN_RESERVA_DE_TRAMO
                   + "JOIN Vuelo v ON v.id_vuelo = tp.id_vuelo " + SqlComun.JOIN_RUTA_DE_VUELO
                   + "WHERE r.estado = 'Confirmada'" + q.condiciones()
                   + " GROUP BY 1 ORDER BY 1";
        return ejecutar("Ventas",
                new String[] {"Período", "Tiquetes vendidos", "Tarifas", "Impuestos", "Total"},
                new int[] {2, 3, 4}, sql, q,
                rs -> new Object[] {rs.getString("periodo"), rs.getLong("tiquetes"),
                        rs.getBigDecimal("tarifas"), rs.getBigDecimal("impuestos"), rs.getBigDecimal("total")});
    }

    /** Cupos totales y ocupados de cada vuelo. */
    private ResultadoReporte ocupacion(FiltroReporte f) throws SQLException {
        ConsultaBuilder q = new ConsultaBuilder().fechas(f, "v.fecha_hora_salida").geografia(f);
        String sql = "SELECT v.numero_vuelo, CONCAT(ao.codigo_iata, ' - ', ad.codigo_iata) AS ruta, "
                   + "v.fecha_hora_salida, v.estado, "
                   + SqlComun.CUPOS_DEL_VUELO + " AS cupos, "
                   + SqlComun.OCUPADOS_DEL_VUELO + " AS ocupados "
                   + SqlComun.FROM_VUELO
                   + "WHERE 1 = 1" + q.condiciones()
                   + " ORDER BY v.fecha_hora_salida";
        return ejecutar("Ocupación de vuelos",
                new String[] {"Vuelo", "Ruta", "Salida", "Estado", "Cupos", "Ocupados", "Ocupación (%)"},
                new int[] {}, sql, q,
                rs -> {
                    int cupos = rs.getInt("cupos");
                    int ocupados = rs.getInt("ocupados");
                    double porcentaje = cupos == 0 ? 0 : ocupados * 100.0 / cupos;
                    return new Object[] {rs.getString("numero_vuelo"), rs.getString("ruta"),
                            corto(rs.getString("fecha_hora_salida")), rs.getString("estado"),
                            cupos, ocupados, porcentaje};
                });
    }

    /** Reservas canceladas por período, con penalidades y reembolsos de la cancelación. */
    private ResultadoReporte cancelaciones(FiltroReporte f) throws SQLException {
        String fecha = "COALESCE(r.fecha_cancelacion, r.fecha_creacion)";
        ConsultaBuilder q = new ConsultaBuilder().fechas(f, fecha).geografiaDeReserva(f, "r.id_reserva");
        String sql = "SELECT DATE_FORMAT(" + fecha + ", '" + f.getPeriodo().getFormatoSql() + "') AS periodo, "
                   + "COUNT(DISTINCT r.id_reserva) AS reservas, "
                   + "COALESCE(SUM(rb.valor_penalidad), 0) AS penalidades, "
                   + "COALESCE(SUM(rb.valor_reembolso), 0) AS reembolsos "
                   + "FROM Reserva r "
                   + "LEFT JOIN Reembolso rb ON rb.id_reserva = r.id_reserva AND rb.id_cambio IS NULL "
                   + "WHERE r.estado = 'Cancelada'" + q.condiciones()
                   + " GROUP BY 1 ORDER BY 1";
        return ejecutar("Cancelaciones de reservas",
                new String[] {"Período", "Reservas canceladas", "Penalidades", "Reembolsos"},
                new int[] {2, 3}, sql, q,
                rs -> new Object[] {rs.getString("periodo"), rs.getLong("reservas"),
                        rs.getBigDecimal("penalidades"), rs.getBigDecimal("reembolsos")});
    }

    /** Vuelos que no están "Programado": reprogramados o cancelados, con su impacto. */
    private ResultadoReporte reprogramaciones(FiltroReporte f) throws SQLException {
        ConsultaBuilder q = new ConsultaBuilder().fechas(f, "v.fecha_hora_salida").geografia(f);
        String sql = "SELECT v.numero_vuelo, CONCAT(ao.codigo_iata, ' - ', ad.codigo_iata) AS ruta, "
                   + "v.fecha_hora_salida, v.estado, "
                   + SqlComun.PASAJEROS_CONFIRMADOS_DEL_VUELO + " AS afectados, "
                   + "(SELECT COUNT(*) FROM Notificacion n WHERE n.id_vuelo = v.id_vuelo) AS notificaciones, "
                   + "(SELECT COUNT(*) FROM Cambio_reserva cr WHERE cr.id_vuelo_anterior = v.id_vuelo "
                   + "AND cr.motivo = 'Operacional') AS cambios "
                   + SqlComun.FROM_VUELO
                   + "WHERE v.estado <> 'Programado'" + q.condiciones()
                   + " ORDER BY v.fecha_hora_salida";
        return ejecutar("Vuelos reprogramados y cancelados",
                new String[] {"Vuelo", "Ruta", "Salida", "Estado", "Pasajeros afectados",
                        "Notificaciones", "Cambios operacionales"},
                new int[] {}, sql, q,
                rs -> new Object[] {rs.getString("numero_vuelo"), rs.getString("ruta"),
                        corto(rs.getString("fecha_hora_salida")), rs.getString("estado"),
                        rs.getLong("afectados"), rs.getLong("notificaciones"), rs.getLong("cambios")});
    }

    /** Vuelos, horas de vuelo y pasajeros transportados por aeronave (sin vuelos cancelados). */
    private ResultadoReporte aeronaves(FiltroReporte f) throws SQLException {
        ConsultaBuilder q = new ConsultaBuilder().fechas(f, "v.fecha_hora_salida").geografia(f);
        String sql = "SELECT t.matricula, t.modelo, COUNT(*) AS vuelos, "
                   + "ROUND(SUM(t.minutos) / 60, 1) AS horas, SUM(t.pasajeros) AS pasajeros FROM ("
                   + "SELECT ae.id_aeronave, ae.matricula, ae.modelo, "
                   + "TIMESTAMPDIFF(MINUTE, v.fecha_hora_salida, v.fecha_hora_llegada) AS minutos, "
                   + SqlComun.PASAJEROS_CONFIRMADOS_DEL_VUELO + " AS pasajeros "
                   + "FROM Vuelo v JOIN Aeronave ae ON ae.id_aeronave = v.id_aeronave "
                   + SqlComun.JOIN_RUTA_DE_VUELO
                   + "WHERE v.estado <> 'Cancelado'" + q.condiciones()
                   + ") t GROUP BY t.id_aeronave, t.matricula, t.modelo ORDER BY horas DESC";
        return ejecutar("Utilización de aeronaves",
                new String[] {"Matrícula", "Modelo", "Vuelos", "Horas de vuelo", "Pasajeros transportados"},
                new int[] {}, sql, q,
                rs -> new Object[] {rs.getString("matricula"), rs.getString("modelo"),
                        rs.getLong("vuelos"), rs.getDouble("horas"), rs.getLong("pasajeros")});
    }

    /** Unidades e ingresos de cada servicio complementario vendido en reservas confirmadas. */
    private ResultadoReporte servicios(FiltroReporte f) throws SQLException {
        ConsultaBuilder q = new ConsultaBuilder().fechas(f, "r.fecha_creacion").geografia(f);
        String sql = "SELECT s.tipo, s.nombre, SUM(sc.cantidad) AS unidades, "
                   + "SUM(sc.cantidad * sc.valor_unitario) AS ingresos "
                   + "FROM Servicio_contratado sc "
                   + "JOIN Servicio s ON s.id_servicio = sc.id_servicio "
                   + "JOIN Tramo_pasajero tp ON tp.id_tramo_pasajero = sc.id_tramo_pasajero "
                   + SqlComun.JOIN_RESERVA_DE_TRAMO
                   + "JOIN Vuelo v ON v.id_vuelo = tp.id_vuelo " + SqlComun.JOIN_RUTA_DE_VUELO
                   + "WHERE r.estado = 'Confirmada'" + q.condiciones()
                   + " GROUP BY s.id_servicio, s.tipo, s.nombre ORDER BY ingresos DESC";
        return ejecutar("Ingresos por servicios adicionales",
                new String[] {"Tipo", "Servicio", "Unidades", "Ingresos"},
                new int[] {3}, sql, q,
                rs -> new Object[] {rs.getString("tipo"), rs.getString("nombre"),
                        rs.getLong("unidades"), rs.getBigDecimal("ingresos")});
    }

    // =====================================================================
    // APOYO
    // =====================================================================

    private ResultadoReporte ejecutar(String titulo, String[] columnas, int[] moneda,
                                      String sql, ConsultaBuilder q, FilaMapper mapper) throws SQLException {
        ResultadoReporte resultado = new ResultadoReporte(titulo, columnas, moneda);
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                q.asignar(ps);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        resultado.agregarFila(mapper.mapear(rs));
                    }
                }
            }
        } finally {
            DaoUtil.cerrar(con);
        }
        return resultado;
    }

    /** "2026-11-22 08:00:00" -> "2026-11-22 08:00" (se lee como texto para evitar zonas horarias). */
    private static String corto(String fecha) {
        return fecha != null && fecha.length() >= 16 ? fecha.substring(0, 16) : fecha;
    }
}
