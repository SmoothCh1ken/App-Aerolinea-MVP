package modelo.modulo_seguridad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/** Lee la política de seguridad (Politica_seguridad). Si falta un valor se usa uno por defecto. */
public class PoliticaDAO {

    public PoliticaSeguridad cargar() throws SQLException {
        Map<String, String> valores = new HashMap<>();
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement("SELECT clave, valor FROM Politica_seguridad");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    valores.put(rs.getString(1), rs.getString(2));
                }
            }
        } finally {
            DaoUtil.cerrar(con);
        }
        return new PoliticaSeguridad(
                entero(valores, "MAX_INTENTOS", 3),
                entero(valores, "MINUTOS_BLOQUEO", 5),
                entero(valores, "MINUTOS_INACTIVIDAD", 10));
    }

    private int entero(Map<String, String> valores, String clave, int defecto) {
        try {
            int v = Integer.parseInt(valores.get(clave).trim());
            return v > 0 ? v : defecto;
        } catch (RuntimeException e) { // clave ausente o texto que no es un número
            return defecto;
        }
    }
}
