package modelo.modulo_consulta;

import modelo.ConexionDB;
import java.math.BigDecimal;
import java.sql.*;

/**
 * Clase DAO encargada de persistir la Reserva completa de manera transaccional.
 * Cubre el requerimiento RF-08.
 */
public class ReservaDAO {

    /**
     * Guarda la reserva, los pasajeros y los tramos en la base de datos de forma segura.
     * Si alguna inserción falla, se revierte todo (Rollback).
     *
     * @param reserva Objeto DTO con toda la información empaquetada.
     * @return true si la transacción fue exitosa, false en caso contrario.
     */
    public boolean registrarReservaTransaccional(ReservaDTO reserva) {
        Connection con = null;

        try {
            con = ConexionDB.conectar();
            // 1. Iniciar Transacción (Desactivar auto-commit)
            con.setAutoCommit(false);

            // 2. Procesar Comprador (Obtener ID o Insertarlo)
            int idComprador = obtenerOInsertarPersona(con, reserva.getComprador());
            if (idComprador == -1) {
                con.rollback();
                return false;
            }

            // 3. Insertar Cabecera de la Reserva
            int idReserva = insertarCabeceraReserva(con, reserva, idComprador);
            if (idReserva == -1) {
                con.rollback();
                return false;
            }

            // 4. Procesar Pasajeros y sus Tramos de Vuelo
            for (PasajeroDTO pasajero : reserva.getListaPasajeros()) {
                
                // 4.1 Insertar o buscar Pasajero
                int idPersonaPasajero = obtenerOInsertarPersona(con, pasajero);
                if (idPersonaPasajero == -1) {
                    con.rollback();
                    return false;
                }

                // 4.2 Traducir categoría de texto a ID y leer su factor de tarifa
                int idCategoria = CalculadoraTarifa.idCategoria(pasajero.getCategoria());
                BigDecimal factor = obtenerFactorCategoria(con, idCategoria);

                // 4.3 Vincular Pasajero a la Reserva
                int idReservaPasajero = insertarReservaPasajero(con, idReserva, idPersonaPasajero, idCategoria);
                if (idReservaPasajero == -1) {
                    con.rollback();
                    return false;
                }

                // 4.4 Insertar Tramo de Ida
                boolean tramoIdaOk = insertarTramoPasajero(con, idReservaPasajero, "Ida", 
                        reserva.getVueloIda(), factor);
                if (!tramoIdaOk) {
                    con.rollback();
                    return false;
                }

                // 4.5 Insertar Tramo de Regreso (Si existe)
                if (reserva.getVueloRegreso() != null) {
                    boolean tramoRegresoOk = insertarTramoPasajero(con, idReservaPasajero, "Regreso", 
                            reserva.getVueloRegreso(), factor);
                    if (!tramoRegresoOk) {
                        con.rollback();
                        return false;
                    }
                }
            }

            // 5. Confirmar Transacción (Commit) si todo salió perfecto
            con.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error Transaccional en ReservaDAO: " + e.getMessage());
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Busca si la persona ya existe por su documento. Si no, la inserta.
     * Este método recibe un Object (puede ser CompradorDTO o PasajeroDTO) por polimorfismo manual.
     */
    private int obtenerOInsertarPersona(Connection con, Object persona) throws SQLException {
        String tipoDoc = "", numDoc = "", nombres = "", apellidos = "", nacionalidad = "", direccion = "", tel = "", correo = "";
        java.util.Date fechaNac = new java.util.Date(); // Por defecto hoy para comprador
        int idCiudad = 1; // Bogotá por defecto en caso extremo

        if (persona instanceof CompradorDTO) {
            CompradorDTO c = (CompradorDTO) persona;
            tipoDoc = c.getTipoDocumento(); numDoc = c.getNumeroDocumento();
            nombres = c.getNombres(); apellidos = c.getApellidos();
            nacionalidad = c.getNacionalidad(); direccion = c.getDireccion();
            tel = c.getTelefono(); correo = c.getCorreo();
            if (c.getCiudadResidencia() != null) idCiudad = c.getCiudadResidencia().getIdCiudad();
            
        } else if (persona instanceof PasajeroDTO) {
            PasajeroDTO p = (PasajeroDTO) persona;
            tipoDoc = p.getTipoDocumento(); numDoc = p.getNumeroDocumento();
            nombres = p.getNombres(); apellidos = p.getApellidos();
            nacionalidad = p.getNacionalidad(); direccion = p.getDireccion();
            tel = p.getTelefono(); correo = p.getCorreo();
            fechaNac = p.getFechaNacimiento();
            if (p.getCiudadResidencia() != null) idCiudad = p.getCiudadResidencia().getIdCiudad();
        }

        // 1. Buscar si ya existe
        String sqlSelect = "SELECT id_persona FROM Persona WHERE tipo_documento = ? AND numero_documento = ?";
        try (PreparedStatement psSel = con.prepareStatement(sqlSelect)) {
            psSel.setString(1, tipoDoc);
            psSel.setString(2, numDoc);
            try (ResultSet rs = psSel.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_persona"); // Retorna el ID existente
                }
            }
        }

        // 2. Si no existe, insertar nueva persona
        String sqlInsert = "INSERT INTO Persona (tipo_documento, numero_documento, nombres, apellidos, fecha_nacimiento, nacionalidad, id_ciudad_residencia, direccion, correo, telefono) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement psIns = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            psIns.setString(1, tipoDoc);
            psIns.setString(2, numDoc);
            psIns.setString(3, nombres);
            psIns.setString(4, apellidos);
            psIns.setDate(5, new java.sql.Date(fechaNac.getTime()));
            psIns.setString(6, nacionalidad);
            psIns.setInt(7, idCiudad);
            psIns.setString(8, direccion);
            psIns.setString(9, correo);
            psIns.setString(10, tel);
            
            int filasAfectadas = psIns.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rsGen = psIns.getGeneratedKeys()) {
                    if (rsGen.next()) return rsGen.getInt(1);
                }
            }
        }
        return -1; // Falla
    }

    private int insertarCabeceraReserva(Connection con, ReservaDTO reserva, int idComprador) throws SQLException {
        // En aerolinea_app_db.sql: id_reserva, codigo_reserva, id_comprador, estado, fecha_creacion, fecha_expiracion
        String sql = "INSERT INTO Reserva (codigo_reserva, id_comprador, estado, fecha_expiracion) VALUES (?, ?, 'Pendiente de pago', DATE_ADD(NOW(), INTERVAL 24 HOUR))";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, reserva.getCodigoReserva());
            ps.setInt(2, idComprador);
            
            if (ps.executeUpdate() > 0) {
                try (ResultSet rsGen = ps.getGeneratedKeys()) {
                    if (rsGen.next()) return rsGen.getInt(1);
                }
            }
        }
        return -1;
    }

    private int insertarReservaPasajero(Connection con, int idReserva, int idPersona, int idCategoria) throws SQLException {
        String sql = "INSERT INTO Reserva_pasajero (id_reserva, id_persona, id_categoria) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idReserva);
            ps.setInt(2, idPersona);
            ps.setInt(3, idCategoria);
            
            if (ps.executeUpdate() > 0) {
                try (ResultSet rsGen = ps.getGeneratedKeys()) {
                    if (rsGen.next()) return rsGen.getInt(1);
                }
            }
        }
        return -1;
    }

    /** Factor de tarifa de la categoría (Adulto 1.00, Niño 0.75, ...). */
    private BigDecimal obtenerFactorCategoria(Connection con, int idCategoria) throws SQLException {
        String sql = "SELECT factor_tarifa FROM Categoria_pasajero WHERE id_categoria = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        }
        throw new SQLException("No existe la categoría de pasajero con id " + idCategoria);
    }

    /** Guarda el tramo ya con factor por categoría e impuestos calculados (misma regla que Cambios). */
    private boolean insertarTramoPasajero(Connection con, int idReservaPasajero, String sentido,
                                          VueloDTO vuelo, BigDecimal factor) throws SQLException {
        BigDecimal base = CalculadoraTarifa.precioBase(vuelo, factor);
        BigDecimal impuestos = CalculadoraTarifa.impuesto(vuelo, base);

        String sql = "INSERT INTO Tramo_pasajero (id_reserva_pasajero, sentido, id_vuelo, id_tarifa, precio_base, impuestos) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReservaPasajero);
            ps.setString(2, sentido);
            ps.setInt(3, vuelo.getIdVuelo());
            ps.setInt(4, vuelo.getIdTarifa());
            ps.setBigDecimal(5, base);
            ps.setBigDecimal(6, impuestos);
            return ps.executeUpdate() > 0;
        }
    }
}
