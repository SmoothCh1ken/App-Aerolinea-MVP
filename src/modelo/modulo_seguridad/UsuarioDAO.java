package modelo.modulo_seguridad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a Usuario_sistema. Aquí solo hay SQL: las reglas están en AutenticacionService y UsuarioService.
 * Las fechas de bloqueo se calculan con NOW() de MySQL, así no depende de la hora del computador.
 */
public class UsuarioDAO {

    private static final String SELECT_BASE =
            "SELECT u.id_usuario, u.usuario, u.nombres, u.apellidos, u.numero_documento, "
          + "u.correo_institucional, u.cargo, u.id_rol, r.nombre AS rol, u.estado, u.intentos_fallidos, "
          + "(u.estado = 'Bloqueado' AND u.bloqueado_hasta IS NOT NULL AND u.bloqueado_hasta > NOW()) AS bloqueo_vigente, "
          + "CASE WHEN u.bloqueado_hasta IS NULL THEN 0 "
          + "     ELSE GREATEST(0, TIMESTAMPDIFF(SECOND, NOW(), u.bloqueado_hasta)) END AS segundos_restantes, "
          + "u.ultimo_acceso, u.secreto_mfa, u.sal_clave, u.hash_clave "
          + "FROM Usuario_sistema u JOIN Rol r ON r.id_rol = u.id_rol ";

    private Usuario leer(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setUsuario(rs.getString("usuario"));
        u.setNombres(rs.getString("nombres"));
        u.setApellidos(rs.getString("apellidos"));
        u.setDocumento(rs.getString("numero_documento"));
        u.setCorreo(rs.getString("correo_institucional"));
        u.setCargo(rs.getString("cargo"));
        u.setIdRol(rs.getInt("id_rol"));
        u.setRolNombre(rs.getString("rol"));
        u.setEstado(rs.getString("estado"));
        u.setIntentosFallidos(rs.getInt("intentos_fallidos"));
        u.setBloqueoVigente(rs.getBoolean("bloqueo_vigente"));
        u.setMinutosRestantes((rs.getInt("segundos_restantes") + 59) / 60); // se redondea hacia arriba
        u.setUltimoAcceso(DaoUtil.corto(rs.getString("ultimo_acceso")));
        u.setSecretoMfa(rs.getString("secreto_mfa"));
        u.setSalClave(rs.getString("sal_clave"));
        u.setHashClave(rs.getString("hash_clave"));
        return u;
    }

    public Usuario buscarPorUsuario(String nombreUsuario) throws SQLException {
        return buscarUno(SELECT_BASE + "WHERE LOWER(u.usuario) = LOWER(?)", nombreUsuario);
    }

    public Usuario buscarPorId(int id) throws SQLException {
        return buscarUno(SELECT_BASE + "WHERE u.id_usuario = ?", id);
    }

    private Usuario buscarUno(String sql, Object parametro) throws SQLException {
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setObject(1, parametro);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? leer(rs) : null;
                }
            }
        } finally {
            DaoUtil.cerrar(con);
        }
    }

    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement(SELECT_BASE + "ORDER BY u.usuario");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(leer(rs));
                }
            }
        } finally {
            DaoUtil.cerrar(con);
        }
        return lista;
    }

    /** @return el id de la cuenta creada. */
    public int insertar(Usuario u, String sal, String hash) throws SQLException {
        String sql = "INSERT INTO Usuario_sistema (usuario, nombres, apellidos, numero_documento, "
                   + "correo_institucional, cargo, id_rol, sal_clave, hash_clave, secreto_mfa) "
                   + "VALUES (?,?,?,?,?,?,?,?,?,?)";
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, u.getUsuario());
                ps.setString(2, u.getNombres());
                ps.setString(3, u.getApellidos());
                ps.setString(4, u.getDocumento());
                ps.setString(5, u.getCorreo());
                ps.setString(6, u.getCargo());
                ps.setInt(7, u.getIdRol());
                ps.setString(8, sal);
                ps.setString(9, hash);
                ps.setString(10, u.getSecretoMfa());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    return keys.next() ? keys.getInt(1) : 0;
                }
            }
        } finally {
            DaoUtil.cerrar(con);
        }
    }

    public void actualizarPerfil(int idUsuario, String cargo, int idRol) throws SQLException {
        ejecutar("UPDATE Usuario_sistema SET cargo = ?, id_rol = ? WHERE id_usuario = ?", cargo, idRol, idUsuario);
    }

    /** Activa o desactiva la cuenta y, de paso, quita cualquier bloqueo e intentos fallidos. */
    public void establecerEstado(int idUsuario, String estado) throws SQLException {
        ejecutar("UPDATE Usuario_sistema SET estado = ?, intentos_fallidos = 0, bloqueado_hasta = NULL "
               + "WHERE id_usuario = ?", estado, idUsuario);
    }

    public void actualizarClave(int idUsuario, String sal, String hash) throws SQLException {
        ejecutar("UPDATE Usuario_sistema SET sal_clave = ?, hash_clave = ? WHERE id_usuario = ?",
                sal, hash, idUsuario);
    }

    public void actualizarSecretoMfa(int idUsuario, String secreto) throws SQLException {
        ejecutar("UPDATE Usuario_sistema SET secreto_mfa = ? WHERE id_usuario = ?", secreto, idUsuario);
    }

    /** Suma un intento fallido y devuelve el total acumulado. */
    public int incrementarIntentos(int idUsuario) throws SQLException {
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE Usuario_sistema SET intentos_fallidos = intentos_fallidos + 1 WHERE id_usuario = ?")) {
                ps.setInt(1, idUsuario);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT intentos_fallidos FROM Usuario_sistema WHERE id_usuario = ?")) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            }
        } finally {
            DaoUtil.cerrar(con);
        }
    }

    public void bloquear(int idUsuario, int minutos) throws SQLException {
        ejecutar("UPDATE Usuario_sistema SET estado = 'Bloqueado', "
               + "bloqueado_hasta = DATE_ADD(NOW(), INTERVAL ? MINUTE) WHERE id_usuario = ?", minutos, idUsuario);
    }

    /** Autenticación completa: se reinician los intentos y se anota el último acceso. */
    public void registrarAccesoExitoso(int idUsuario) throws SQLException {
        ejecutar("UPDATE Usuario_sistema SET intentos_fallidos = 0, bloqueado_hasta = NULL, "
               + "ultimo_acceso = NOW() WHERE id_usuario = ?", idUsuario);
    }

    /** @param columna solo "usuario", "numero_documento" o "correo_institucional" */
    public boolean existe(String columna, String valor) throws SQLException {
        if (!"usuario".equals(columna) && !"numero_documento".equals(columna)
                && !"correo_institucional".equals(columna)) {
            throw new IllegalArgumentException("Columna no permitida: " + columna);
        }
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT 1 FROM Usuario_sistema WHERE LOWER(" + columna + ") = LOWER(?)")) {
                ps.setString(1, valor);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } finally {
            DaoUtil.cerrar(con);
        }
    }

    public int contarAdministradoresActivos() throws SQLException {
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM Usuario_sistema u JOIN Rol r ON r.id_rol = u.id_rol "
                  + "WHERE r.nombre = ? AND u.estado = 'Activo'")) {
                ps.setString(1, Rol.ADMINISTRADOR);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            }
        } finally {
            DaoUtil.cerrar(con);
        }
    }

    private void ejecutar(String sql, Object... parametros) throws SQLException {
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (int i = 0; i < parametros.length; i++) {
                    ps.setObject(i + 1, parametros[i]);
                }
                ps.executeUpdate();
            }
        } finally {
            DaoUtil.cerrar(con);
        }
    }
}
