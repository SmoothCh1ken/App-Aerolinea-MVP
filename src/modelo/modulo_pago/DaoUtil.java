package modelo.modulo_pago;

import java.sql.Connection;
import java.sql.SQLException;
import modelo.ConexionDB;

/** Utilidades de conexión compartidas por los DAO del módulo de pago. */
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
}
