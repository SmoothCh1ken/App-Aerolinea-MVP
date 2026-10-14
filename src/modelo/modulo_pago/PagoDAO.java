package modelo.modulo_pago;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Operaciones sobre Pago, Reserva (estado), Bono_corporativo y Cuenta_millas.
 * Los métodos que reciben Connection forman parte de una transacción manejada
 * por ProcesadorPago (todo se confirma o todo se revierte).
 */
public class PagoDAO {

    /** Estado de la reserva leído con bloqueo de fila. */
    public static class EstadoReserva {
        public final String estado;
        public final boolean vencida;
        EstadoReserva(String estado, boolean vencida) {
            this.estado = estado;
            this.vencida = vencida;
        }
    }

    /** Datos básicos de un bono corporativo. */
    public static class Bono {
        public final int id;
        public final BigDecimal saldo;
        public final Date vencimiento;
        public final boolean activo;
        Bono(int id, BigDecimal saldo, Date vencimiento, boolean activo) {
            this.id = id;
            this.saldo = saldo;
            this.vencimiento = vencimiento;
            this.activo = activo;
        }
    }

    /** Lee y bloquea la reserva para que dos pagos simultáneos no se crucen. */
    public EstadoReserva bloquearReserva(Connection con, int idReserva) throws SQLException {
        String sql = "SELECT estado, (fecha_expiracion < NOW()) AS vencida "
                   + "FROM Reserva WHERE id_reserva = ? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new EstadoReserva(rs.getString("estado"), rs.getBoolean("vencida"));
            }
        }
        return null;
    }

    /** Inserta el registro del pago y devuelve su id. */
    public int insertar(Connection con, Pago p) throws SQLException {
        String sql = "INSERT INTO Pago (id_reserva, id_medio_pago, valor, estado, "
                   + "referencia_externa, id_bono, millas_usadas) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getIdReserva());
            ps.setInt(2, p.getIdMedioPago());
            ps.setBigDecimal(3, p.getValor());
            ps.setString(4, p.getEstado());
            ps.setString(5, p.getReferenciaExterna());
            if (p.getIdBono() == null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, p.getIdBono());
            if (p.getMillasUsadas() == null) ps.setNull(7, Types.INTEGER); else ps.setInt(7, p.getMillasUsadas());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return 0;
    }

    /** Pasa la reserva a "Confirmada" (pago aprobado). */
    public void confirmarReserva(Connection con, int idReserva) throws SQLException {
        String sql = "UPDATE Reserva SET estado = 'Confirmada' "
                   + "WHERE id_reserva = ? AND estado = 'Pendiente de pago'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.executeUpdate();
        }
    }

    /**
     * RF-19 / CA-24: libera los cupos de una reserva cuyo bloqueo venció.
     * Quita los asientos asignados, marca la reserva como "Expirada" y los pagos
     * que seguían "Pendiente" como "Expirado". (Los cupos de Vuelo_tarifa se calculan
     * contando solo reservas que NO estén Expirada ni Cancelada.)
     */
    public void liberarReserva(Connection con, int idReserva) throws SQLException {
        String sqlAsientos = "UPDATE Tramo_pasajero tp "
                + "JOIN Reserva_pasajero rp ON rp.id_reserva_pasajero = tp.id_reserva_pasajero "
                + "SET tp.id_asiento = NULL WHERE rp.id_reserva = ?";
        String sqlPagos = "UPDATE Pago SET estado = 'Expirado' WHERE id_reserva = ? AND estado = 'Pendiente'";
        String sqlReserva = "UPDATE Reserva SET estado = 'Expirada' "
                + "WHERE id_reserva = ? AND estado = 'Pendiente de pago'";
        for (String sql : new String[] { sqlAsientos, sqlPagos, sqlReserva }) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idReserva);
                ps.executeUpdate();
            }
        }
    }

    /** Libera todas las reservas pendientes cuyo plazo ya venció. @return cuántas liberó. */
    public int liberarReservasExpiradas() throws SQLException {
        Connection con = null;
        try {
            con = DaoUtil.abrir();
            con.setAutoCommit(false);
            List<Integer> ids = new ArrayList<>();
            String sql = "SELECT id_reserva FROM Reserva "
                       + "WHERE estado = 'Pendiente de pago' AND fecha_expiracion < NOW() FOR UPDATE";
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
            for (int id : ids) liberarReserva(con, id);
            con.commit();
            return ids.size();
        } catch (SQLException e) {
            if (con != null) { try { con.rollback(); } catch (SQLException ignorada) { } }
            throw e;
        } finally {
            DaoUtil.cerrar(con);
        }
    }

    // ---------- Bono corporativo ----------

    public Bono obtenerBono(Connection con, String codigo) throws SQLException {
        String sql = "SELECT id_bono, saldo, fecha_vencimiento, activo "
                   + "FROM Bono_corporativo WHERE codigo = ? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Bono(rs.getInt("id_bono"), rs.getBigDecimal("saldo"),
                            rs.getDate("fecha_vencimiento"), rs.getBoolean("activo"));
                }
            }
        }
        return null;
    }

    public boolean descontarBono(Connection con, int idBono, BigDecimal valor) throws SQLException {
        String sql = "UPDATE Bono_corporativo SET saldo = saldo - ? WHERE id_bono = ? AND saldo >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, valor);
            ps.setInt(2, idBono);
            ps.setBigDecimal(3, valor);
            return ps.executeUpdate() == 1;
        }
    }

    // ---------- Millas ----------

    /** @return saldo de millas del comprador, o -1 si no tiene cuenta de millas. */
    public int obtenerSaldoMillas(Connection con, int idPersona) throws SQLException {
        String sql = "SELECT saldo_millas FROM Cuenta_millas WHERE id_persona = ? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean descontarMillas(Connection con, int idPersona, int millas) throws SQLException {
        String sql = "UPDATE Cuenta_millas SET saldo_millas = saldo_millas - ? "
                   + "WHERE id_persona = ? AND saldo_millas >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, millas);
            ps.setInt(2, idPersona);
            ps.setInt(3, millas);
            return ps.executeUpdate() == 1;
        }
    }
}
