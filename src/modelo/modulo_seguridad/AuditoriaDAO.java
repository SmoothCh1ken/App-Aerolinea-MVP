package modelo.modulo_seguridad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** Escribe y consulta la auditoría de seguridad (RF-31). Los registros nunca se modifican ni se borran. */
public class AuditoriaDAO {

    /**
     * @param idUsuario    id de la cuenta involucrada, o null si no existe (usuario inexistente en un login)
     * @param usuarioTexto nombre de usuario tal como se escribió
     */
    public void registrar(Integer idUsuario, String usuarioTexto, String evento, String resultado,
                          String detalle) throws SQLException {
        String sql = "INSERT INTO Auditoria_seguridad (id_usuario, usuario_texto, evento, resultado, detalle) "
                   + "VALUES (?,?,?,?,?)";
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                if (idUsuario == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, idUsuario);
                ps.setString(2, recortar(usuarioTexto, 50));
                ps.setString(3, evento);
                ps.setString(4, resultado);
                ps.setString(5, recortar(detalle, 255));
                ps.executeUpdate();
            }
        } finally {
            DaoUtil.cerrar(con);
        }
    }

    public List<RegistroAuditoria> listar(FiltroAuditoria f) throws SQLException {
        StringBuilder condiciones = new StringBuilder();
        List<Object> parametros = new ArrayList<>();
        if (f.getDesde() != null) {
            condiciones.append(" AND a.fecha_hora >= ?");
            parametros.add(f.getDesde() + " 00:00:00");
        }
        if (f.getHasta() != null) {
            condiciones.append(" AND a.fecha_hora < ?");
            parametros.add(f.getHasta().plusDays(1) + " 00:00:00");
        }
        if (f.getUsuario() != null && !f.getUsuario().trim().isEmpty()) {
            condiciones.append(" AND a.usuario_texto LIKE ?");
            parametros.add("%" + f.getUsuario().trim() + "%");
        }
        if (f.getEvento() != null && !f.getEvento().isEmpty()) {
            condiciones.append(" AND a.evento = ?");
            parametros.add(f.getEvento());
        }
        if (f.getResultado() != null && !f.getResultado().isEmpty()) {
            condiciones.append(" AND a.resultado = ?");
            parametros.add(f.getResultado());
        }
        String sql = "SELECT a.fecha_hora, COALESCE(a.usuario_texto, '') AS usuario, a.evento, a.resultado, "
                   + "COALESCE(a.detalle, '') AS detalle FROM Auditoria_seguridad a WHERE 1 = 1"
                   + condiciones + " ORDER BY a.id_auditoria DESC LIMIT 500";

        List<RegistroAuditoria> lista = new ArrayList<>();
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (int i = 0; i < parametros.size(); i++) {
                    ps.setObject(i + 1, parametros.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new RegistroAuditoria(DaoUtil.corto(rs.getString("fecha_hora")),
                                rs.getString("usuario"), rs.getString("evento"),
                                rs.getString("resultado"), rs.getString("detalle")));
                    }
                }
            }
        } finally {
            DaoUtil.cerrar(con);
        }
        return lista;
    }

    private String recortar(String texto, int max) {
        if (texto == null) return null;
        return texto.length() <= max ? texto : texto.substring(0, max);
    }
}
