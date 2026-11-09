package modelo.modulo_seguridad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Acceso a Rol y Rol_modulo. */
public class RolDAO {

    public List<Rol> listar() throws SQLException {
        List<Rol> roles = new ArrayList<>();
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement("SELECT id_rol, nombre FROM Rol ORDER BY nombre");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(new Rol(rs.getInt(1), rs.getString(2)));
                }
            }
        } finally {
            DaoUtil.cerrar(con);
        }
        return roles;
    }

    /** RF-27: módulos a los que puede entrar un rol. */
    public Set<Modulo> modulosDeRol(int idRol) throws SQLException {
        Set<Modulo> modulos = EnumSet.noneOf(Modulo.class);
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement("SELECT modulo FROM Rol_modulo WHERE id_rol = ?")) {
                ps.setInt(1, idRol);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        try {
                            modulos.add(Modulo.valueOf(rs.getString(1)));
                        } catch (IllegalArgumentException ignorado) {
                            // módulo que esta versión del programa no conoce: se ignora
                        }
                    }
                }
            }
        } finally {
            DaoUtil.cerrar(con);
        }
        return modulos;
    }
}
