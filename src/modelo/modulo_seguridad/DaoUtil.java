package modelo.modulo_seguridad;

import java.sql.Connection;
import java.sql.SQLException;
import modelo.ConexionDB;

/** Utilidades de conexión compartidas por los DAO del módulo de seguridad. */
final class DaoUtil {

    private DaoUtil() { }

    static Connection abrir() throws SQLException {
        Connection con = ConexionDB.conectar();
        if (con == null) {
            throw new SQLException("No fue posible conectar con la base de datos aerolinea_app_db.");
        }
        return con;
    }

    static void cerrar(Connection con) {
        if (con != null) {
            try { con.close(); } catch (SQLException ignorada) { }
        }
    }

    /** "2026-11-22 08:00:00" -> "2026-11-22 08:00" (las fechas se leen como texto para evitar zonas horarias). */
    static String corto(String fecha) {
        return fecha != null && fecha.length() >= 16 ? fecha.substring(0, 16) : fecha;
    }
}
