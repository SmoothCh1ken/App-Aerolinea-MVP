package modelo.modulo_seguridad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Incidentes de seguridad que se reportan al área de Tecnología (RF-29). */
public class IncidenteDAO {

    public void crear(int idUsuario, String tipo, String detalle) throws SQLException {
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Incidente_seguridad (id_usuario, tipo, detalle) VALUES (?,?,?)")) {
                ps.setInt(1, idUsuario);
                ps.setString(2, tipo);
                ps.setString(3, detalle);
                ps.executeUpdate();
            }
        } finally {
            DaoUtil.cerrar(con);
        }
    }

    public List<Incidente> listar(boolean soloAbiertos) throws SQLException {
        String sql = "SELECT i.id_incidente, i.fecha_hora, i.id_usuario, u.usuario, i.tipo, i.detalle, i.estado "
                   + "FROM Incidente_seguridad i JOIN Usuario_sistema u ON u.id_usuario = i.id_usuario "
                   + (soloAbiertos ? "WHERE i.estado = 'Abierto' " : "")
                   + "ORDER BY i.id_incidente DESC LIMIT 500";
        List<Incidente> lista = new ArrayList<>();
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Incidente(rs.getInt(1), DaoUtil.corto(rs.getString(2)), rs.getInt(3),
                            rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)));
                }
            }
        } finally {
            DaoUtil.cerrar(con);
        }
        return lista;
    }

    public void marcarAtendido(int idIncidente, int idUsuarioQueAtiende) throws SQLException {
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE Incidente_seguridad SET estado = 'Atendido', id_usuario_atiende = ?, "
                  + "fecha_atencion = NOW() WHERE id_incidente = ? AND estado = 'Abierto'")) {
                ps.setInt(1, idUsuarioQueAtiende);
                ps.setInt(2, idIncidente);
                ps.executeUpdate();
            }
        } finally {
            DaoUtil.cerrar(con);
        }
    }
}
