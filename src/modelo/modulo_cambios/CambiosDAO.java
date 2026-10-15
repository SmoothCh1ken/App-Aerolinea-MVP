package modelo.modulo_cambios;

import modelo.ConexionDB;
import modelo.comun.Cupos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

/** Acceso a la base de datos del módulo 4. Aquí solo hay SQL, ninguna regla de negocio. */
public class CambiosDAO {

    /** Lee una fecha tal como esta en la base de datos, sin conversiones de zona horaria. */
    private LocalDateTime fecha(ResultSet rs, String columna) throws SQLException {
        String texto = rs.getString(columna);
        return LocalDateTime.parse(texto.substring(0, 19).replace(' ', 'T'));
    }

    // =====================================================================
    // CONSULTAR RESERVA
    // =====================================================================

    /** Busca la reserva por código y apellido del titular. Devuelve null si no existe. */
    public ReservaInfoDTO buscarReserva(String codigo, String apellido) throws SQLException {
        String sql = "SELECT r.id_reserva, r.codigo_reserva, r.estado, c.nombres, c.apellidos, c.correo "
                   + "FROM Reserva r "
                   + "JOIN Persona c ON c.id_persona = r.id_comprador "
                   + "WHERE r.codigo_reserva = ? AND LOWER(c.apellidos) = LOWER(?)";

        ReservaInfoDTO reserva = null;
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setString(2, apellido);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                reserva = new ReservaInfoDTO();
                reserva.idReserva = rs.getInt("id_reserva");
                reserva.codigo = rs.getString("codigo_reserva");
                reserva.estado = rs.getString("estado");
                reserva.correo = rs.getString("correo");
                reserva.apellido = rs.getString("apellidos");
                reserva.titular = rs.getString("nombres") + " " + reserva.apellido;
            }
        }

        if (reserva != null) {
            reserva.tramos = buscarTramos(reserva.idReserva);
        }
        return reserva;
    }

    /** Todos los tramos (ida/regreso) de todos los pasajeros de una reserva. */
    private ArrayList<TramoInfoDTO> buscarTramos(int idReserva) throws SQLException {
        String sql = "SELECT t.id_tramo_pasajero, t.sentido, t.id_vuelo, t.id_tarifa, "
                   + "       t.precio_base, t.descuento, t.impuestos, "
                   + "       p.nombres, p.apellidos, "
                   + "       cp.nombre AS categoria, cp.factor_tarifa, cp.ocupa_cupo, "
                   + "       v.numero_vuelo, v.id_ruta, v.fecha_hora_salida, v.fecha_hora_llegada, v.estado AS estado_vuelo, "
                   + "       ao.codigo_iata AS origen, ad.codigo_iata AS destino, "
                   + "       ta.nombre AS tarifa, ta.permite_cambios, ta.cargo_cambio, ta.permite_reembolso, "
                   + "       ta.porcentaje_penalidad_reembolso, ta.horas_minimas_antelacion "
                   + "FROM Tramo_pasajero t "
                   + "JOIN Reserva_pasajero rp ON rp.id_reserva_pasajero = t.id_reserva_pasajero "
                   + "JOIN Persona p ON p.id_persona = rp.id_persona "
                   + "JOIN Categoria_pasajero cp ON cp.id_categoria = rp.id_categoria "
                   + "JOIN Vuelo v ON v.id_vuelo = t.id_vuelo "
                   + "JOIN Ruta ru ON ru.id_ruta = v.id_ruta "
                   + "JOIN Aeropuerto ao ON ao.id_aeropuerto = ru.id_aeropuerto_origen "
                   + "JOIN Aeropuerto ad ON ad.id_aeropuerto = ru.id_aeropuerto_destino "
                   + "JOIN Tarifa ta ON ta.id_tarifa = t.id_tarifa "
                   + "WHERE rp.id_reserva = ? "
                   + "ORDER BY t.sentido, t.id_tramo_pasajero";

        ArrayList<TramoInfoDTO> lista = new ArrayList<>();
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TramoInfoDTO t = new TramoInfoDTO();
                t.idTramo = rs.getInt("id_tramo_pasajero");
                t.sentido = rs.getString("sentido");
                t.idVuelo = rs.getInt("id_vuelo");
                t.idTarifa = rs.getInt("id_tarifa");
                t.precioBase = rs.getBigDecimal("precio_base");
                t.descuento = rs.getBigDecimal("descuento");
                t.impuestos = rs.getBigDecimal("impuestos");
                t.pasajero = rs.getString("nombres") + " " + rs.getString("apellidos");
                t.categoria = rs.getString("categoria");
                t.factor = rs.getBigDecimal("factor_tarifa");
                t.ocupaCupo = rs.getBoolean("ocupa_cupo");
                t.numeroVuelo = rs.getString("numero_vuelo");
                t.idRuta = rs.getInt("id_ruta");
                t.salida = fecha(rs, "fecha_hora_salida");
                t.llegada = fecha(rs, "fecha_hora_llegada");
                t.estadoVuelo = rs.getString("estado_vuelo");
                t.origen = rs.getString("origen");
                t.destino = rs.getString("destino");
                t.tarifa = rs.getString("tarifa");
                t.permiteCambios = rs.getBoolean("permite_cambios");
                t.cargoCambio = rs.getBigDecimal("cargo_cambio");
                t.permiteReembolso = rs.getBoolean("permite_reembolso");
                t.penalidad = rs.getBigDecimal("porcentaje_penalidad_reembolso");
                t.horasMinimas = rs.getInt("horas_minimas_antelacion");
                lista.add(t);
            }
        }
        return lista;
    }

    // =====================================================================
    // VUELOS ALTERNOS PARA UN CAMBIO
    // =====================================================================

    /** Vuelos de la misma ruta y tarifa que tengan al menos "plazas" cupos libres. */
    public ArrayList<OpcionCambioDTO> buscarOpciones(int idRuta, int idVueloActual, int idTarifa, int plazas)
            throws SQLException {
        String sql = "SELECT v.id_vuelo, v.numero_vuelo, v.fecha_hora_salida, v.fecha_hora_llegada, "
                   + "       vt.id_tarifa, vt.precio, vt.cupos_totales, ru.porcentaje_impuesto "
                   + "FROM Vuelo v "
                   + "JOIN Vuelo_tarifa vt ON vt.id_vuelo = v.id_vuelo "
                   + "JOIN Ruta ru ON ru.id_ruta = v.id_ruta "
                   + "WHERE v.id_ruta = ? AND v.id_vuelo <> ? AND vt.id_tarifa = ? "
                   + "AND v.estado <> 'Cancelado' AND v.fecha_hora_salida > NOW() "
                   + "ORDER BY v.fecha_hora_salida";

        ArrayList<OpcionCambioDTO> todas = new ArrayList<>();
        ArrayList<Integer> cuposTotales = new ArrayList<>();
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRuta);
            ps.setInt(2, idVueloActual);
            ps.setInt(3, idTarifa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OpcionCambioDTO o = new OpcionCambioDTO();
                o.idVuelo = rs.getInt("id_vuelo");
                o.numeroVuelo = rs.getString("numero_vuelo");
                o.salida = fecha(rs, "fecha_hora_salida");
                o.llegada = fecha(rs, "fecha_hora_llegada");
                o.idTarifa = rs.getInt("id_tarifa");
                o.precio = rs.getBigDecimal("precio");
                o.porcImpuesto = rs.getBigDecimal("porcentaje_impuesto");
                todas.add(o);
                cuposTotales.add(rs.getInt("cupos_totales"));
            }
        }

        // Nos quedamos solo con los vuelos que tengan cupos suficientes
        ArrayList<OpcionCambioDTO> disponibles = new ArrayList<>();
        for (int i = 0; i < todas.size(); i++) {
            OpcionCambioDTO o = todas.get(i);
            o.cupos = cuposTotales.get(i) - contarOcupados(o.idVuelo, o.idTarifa);
            if (o.cupos >= plazas) {
                disponibles.add(o);
            }
        }
        return disponibles;
    }

    /** Cuántos cupos ya están ocupados en un vuelo y tarifa (regla compartida con Consulta y Reserva). */
    private int contarOcupados(int idVuelo, int idTarifa) throws SQLException {
        try (Connection con = ConexionDB.conectar()) {
            return Cupos.ocupados(con, idVuelo, idTarifa);
        }
    }

    // =====================================================================
    // REGISTRAR UN CAMBIO DE VUELO
    // =====================================================================

    /**
     
     */
    public int registrarCambio(ReservaInfoDTO r, String sentido, TramoInfoDTO ref,
                               OpcionCambioDTO op, CotizacionDTO q) throws SQLException {
        boolean aplicar = q.neto.signum() <= 0;
        int idCambio = 0;

        Connection con = null;
        try {
            con = ConexionDB.conectar();
            con.setAutoCommit(false);                       // inicia la transacción

            // 1. Guardar el cambio
            String sqlCambio = "INSERT INTO Cambio_reserva (id_reserva, sentido, id_vuelo_anterior, id_tarifa_anterior, "
                             + "id_vuelo_nuevo, id_tarifa_nuevo, motivo, cargo_cambio, diferencia_valor, estado) "
                             + "VALUES (?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sqlCambio, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, r.idReserva);
                ps.setString(2, sentido);
                ps.setInt(3, ref.idVuelo);
                ps.setInt(4, ref.idTarifa);
                ps.setInt(5, op.idVuelo);
                ps.setInt(6, op.idTarifa);
                ps.setString(7, q.motivo);
                ps.setBigDecimal(8, q.cargo);
                ps.setBigDecimal(9, q.diferencia);
                ps.setString(10, aplicar ? "Aplicado" : "Pendiente de pago");
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    idCambio = keys.getInt(1);
                }
            }

            if (aplicar) {
                // 2. Mover cada tramo al nuevo vuelo (el asiento queda NULL: era del vuelo anterior)
                String sqlTramo = "UPDATE Tramo_pasajero SET id_vuelo = ?, id_tarifa = ?, id_asiento = NULL, "
                                + "precio_base = ?, impuestos = ? WHERE id_tramo_pasajero = ?";
                for (TramoInfoDTO t : r.tramos(sentido)) {
                    try (PreparedStatement ps = con.prepareStatement(sqlTramo)) {
                        ps.setInt(1, op.idVuelo);
                        ps.setInt(2, op.idTarifa);
                        ps.setBigDecimal(3, t.nuevoPrecioBase);
                        ps.setBigDecimal(4, t.nuevoImpuestos);
                        ps.setInt(5, t.idTramo);
                        ps.executeUpdate();
                    }
                }

                // 3. Si queda saldo a favor, se crea el reembolso
                if (q.neto.signum() < 0) {
                    String sqlReembolso = "INSERT INTO Reembolso (id_reserva, id_cambio, motivo, valor_penalidad, valor_reembolso) "
                                        + "VALUES (?, ?, 'Saldo a favor', 0, ?)";
                    try (PreparedStatement ps = con.prepareStatement(sqlReembolso)) {
                        ps.setInt(1, r.idReserva);
                        ps.setInt(2, idCambio);
                        ps.setBigDecimal(3, q.neto.negate());
                        ps.executeUpdate();
                    }
                }
            }

            con.commit();                                   // todo salió bien: se guarda
        } catch (SQLException e) {
            if (con != null) {
                con.rollback();                             // algo falló: se deshace todo
            }
            throw e;
        } finally {
            if (con != null) {
                con.close();
            }
        }
        return idCambio;
    }

    // =====================================================================
    // CANCELAR RESERVA
    // =====================================================================

    public void cancelarReserva(ReservaInfoDTO r, CotizacionDTO q) throws SQLException {
        Connection con = null;
        try {
            con = ConexionDB.conectar();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE Reserva SET estado = 'Cancelada', fecha_cancelacion = NOW() WHERE id_reserva = ?")) {
                ps.setInt(1, r.idReserva);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE Tiquete t JOIN Reserva_pasajero rp ON rp.id_reserva_pasajero = t.id_reserva_pasajero "
                  + "SET t.estado = 'Anulado' WHERE rp.id_reserva = ?")) {
                ps.setInt(1, r.idReserva);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Reembolso (id_reserva, motivo, valor_penalidad, valor_reembolso) VALUES (?,?,?,?)")) {
                ps.setInt(1, r.idReserva);
                ps.setString(2, q.motivo);
                ps.setBigDecimal(3, q.penalidad);
                ps.setBigDecimal(4, q.reembolso);
                ps.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            if (con != null) {
                con.rollback();
            }
            throw e;
        } finally {
            if (con != null) {
                con.close();
            }
        }
    }

    // =====================================================================
    // HISTORIAL DE LA RESERVA
    // =====================================================================

    public ArrayList<String> historial(int idReserva) throws SQLException {
        ArrayList<String> lista = new ArrayList<>();

        try (Connection con = ConexionDB.conectar()) {

            PreparedStatement ps = con.prepareStatement(
                "SELECT id_cambio, sentido, motivo, estado, cargo_cambio, diferencia_valor "
              + "FROM Cambio_reserva WHERE id_reserva = ? ORDER BY id_cambio");
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add("CAMBIO #" + rs.getInt("id_cambio") + " (" + rs.getString("sentido") + ", "
                        + rs.getString("motivo") + ") " + rs.getString("estado")
                        + " - cargo " + rs.getBigDecimal("cargo_cambio")
                        + ", diferencia " + rs.getBigDecimal("diferencia_valor"));
            }

            ps = con.prepareStatement(
                "SELECT id_reembolso, motivo, valor_penalidad, valor_reembolso, estado "
              + "FROM Reembolso WHERE id_reserva = ? ORDER BY id_reembolso");
            ps.setInt(1, idReserva);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add("REEMBOLSO #" + rs.getInt("id_reembolso") + " (" + rs.getString("motivo") + ") "
                        + rs.getString("estado") + " - penalidad " + rs.getBigDecimal("valor_penalidad")
                        + ", a reembolsar " + rs.getBigDecimal("valor_reembolso"));
            }

            ps = con.prepareStatement(
                "SELECT tipo, asunto, mensaje, fecha_hora FROM Notificacion WHERE id_reserva = ? ORDER BY id_notificacion");
            ps.setInt(1, idReserva);
            rs = ps.executeQuery();
            while (rs.next()) {
                String fecha = fecha(rs, "fecha_hora").toString().replace('T', ' ');
                lista.add("NOTIFICACIÓN [" + rs.getString("tipo") + "] " + fecha
                        + " - " + rs.getString("asunto") + ": " + rs.getString("mensaje"));
            }
        }
        return lista;
    }

    // =====================================================================
    // OPERACIONES SOBRE VUELOS
    // =====================================================================

    private static final String SQL_VUELOS =
            "SELECT v.id_vuelo, v.numero_vuelo, CONCAT(ao.codigo_iata, ' -> ', ad.codigo_iata) AS ruta, "
          + "       v.fecha_hora_salida, v.fecha_hora_llegada, v.estado "
          + "FROM Vuelo v "
          + "JOIN Ruta ru ON ru.id_ruta = v.id_ruta "
          + "JOIN Aeropuerto ao ON ao.id_aeropuerto = ru.id_aeropuerto_origen "
          + "JOIN Aeropuerto ad ON ad.id_aeropuerto = ru.id_aeropuerto_destino ";

    private VueloInfoDTO leerVuelo(ResultSet rs) throws SQLException {
        VueloInfoDTO v = new VueloInfoDTO();
        v.id = rs.getInt("id_vuelo");
        v.numero = rs.getString("numero_vuelo");
        v.ruta = rs.getString("ruta");
        v.salida = fecha(rs, "fecha_hora_salida");
        v.llegada = fecha(rs, "fecha_hora_llegada");
        v.estado = rs.getString("estado");
        return v;
    }

    public ArrayList<VueloInfoDTO> listarVuelos() throws SQLException {
        ArrayList<VueloInfoDTO> lista = new ArrayList<>();
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_VUELOS + "ORDER BY v.fecha_hora_salida")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(leerVuelo(rs));
            }
        }
        return lista;
    }

    public VueloInfoDTO buscarVuelo(int idVuelo) throws SQLException {
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_VUELOS + "WHERE v.id_vuelo = ?")) {
            ps.setInt(1, idVuelo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return leerVuelo(rs);
            }
            return null;
        }
    }

    /**
     * Cambia el estado del vuelo (y su horario si "salida" no es null) y crea una notificación
     * para cada pasajero con reserva confirmada. Devuelve cuántos pasajeros se notificaron.
     */
    public int operarVuelo(int idVuelo, String estado, LocalDateTime salida, LocalDateTime llegada,
                           String tipo, String asunto, String mensaje) throws SQLException {
        int notificados = 0;
        Connection con = null;
        try {
            con = ConexionDB.conectar();
            con.setAutoCommit(false);

            // 1. Actualizar el vuelo
            if (salida == null) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE Vuelo SET estado = ? WHERE id_vuelo = ?")) {
                    ps.setString(1, estado);
                    ps.setInt(2, idVuelo);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE Vuelo SET estado = ?, fecha_hora_salida = ?, fecha_hora_llegada = ? WHERE id_vuelo = ?")) {
                    ps.setString(1, estado);
                    ps.setString(2, salida.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    ps.setString(3, llegada.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    ps.setInt(4, idVuelo);
                    ps.executeUpdate();
                }
            }

            // 2. Notificar a los pasajeros afectados
            String sqlNotif = "INSERT INTO Notificacion (id_reserva, id_persona, id_vuelo, tipo, asunto, mensaje, correo_destino) "
                            + "SELECT r.id_reserva, p.id_persona, ?, ?, ?, ?, COALESCE(p.correo, cb.correo) "
                            + "FROM Tramo_pasajero t "
                            + "JOIN Reserva_pasajero rp ON rp.id_reserva_pasajero = t.id_reserva_pasajero "
                            + "JOIN Reserva r ON r.id_reserva = rp.id_reserva "
                            + "JOIN Persona p ON p.id_persona = rp.id_persona "
                            + "JOIN Persona cb ON cb.id_persona = r.id_comprador "
                            + "WHERE t.id_vuelo = ? AND r.estado = 'Confirmada' "
                            + "AND COALESCE(p.correo, cb.correo) IS NOT NULL";
            try (PreparedStatement ps = con.prepareStatement(sqlNotif)) {
                ps.setInt(1, idVuelo);
                ps.setString(2, tipo);
                ps.setString(3, asunto);
                ps.setString(4, mensaje);
                ps.setInt(5, idVuelo);
                notificados = ps.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            if (con != null) {
                con.rollback();
            }
            throw e;
        } finally {
            if (con != null) {
                con.close();
            }
        }
        return notificados;
    }
}
