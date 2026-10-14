package modelo.modulo_pago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla Medio_pago. */
public class MedioPagoDAO {

    /** RF-18: devuelve solo los medios de pago habilitados. */
    public List<MedioPago> listarActivos() throws SQLException {
        List<MedioPago> medios = new ArrayList<>();
        String sql = "SELECT id_medio_pago, nombre FROM Medio_pago WHERE activo = TRUE ORDER BY id_medio_pago";
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medios.add(new MedioPago(rs.getInt("id_medio_pago"), rs.getString("nombre")));
                }
            }
        } finally {
            DaoUtil.cerrar(con);
        }
        return medios;
    }
}
