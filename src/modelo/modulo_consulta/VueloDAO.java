package modelo.modulo_consulta;

import modelo.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO encargada de las operaciones de lectura en la base de datos
 * para el módulo de consulta de vuelos (RF-01).
 */
public class VueloDAO {

    /**
     * Consulta la lista completa de ciudades registradas para poblar los ComboBox.
     * @return List<Ciudad> lista de objetos Ciudad ordenados alfabéticamente.
     */
    public List<Ciudad> obtenerTodasLasCiudades() {
        List<Ciudad> ciudades = new ArrayList<>();
        String sql = "SELECT id_ciudad, nombre, pais FROM Ciudad ORDER BY nombre ASC";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ciudad ciudad = new Ciudad();
                ciudad.setIdCiudad(rs.getInt("id_ciudad"));
                ciudad.setNombre(rs.getString("nombre"));
                ciudad.setPais(rs.getString("pais"));
                ciudades.add(ciudad);
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar las ciudades: " + e.getMessage());
            e.printStackTrace();
        }

        return ciudades;
    }

    /**
     * Consulta la oferta de vuelos disponibles según la ciudad de origen, destino y fecha.
     * Cumple con los Criterios de Aceptación (CA-01) del RF-01.
     * 
     * @@param idCiudadOrigen  ID de la ciudad de salida
     * @param idCiudadDestino ID de la ciudad de llegada
     * @param fechaSalida     Fecha en formato YYYY-MM-DD
     * @return List<VueloDTO> lista de vuelos coincidentes con precios y tarifas
     */
    public List<VueloDTO> buscarVuelos(int idCiudadOrigen, int idCiudadDestino, String fechaSalida) {
        List<VueloDTO> listaVuelos = new ArrayList<>();

        // Consulta SQL con JOINs para unir la infraestructura de la ruta y la oferta comercial
        String sql = "SELECT v.id_vuelo, v.numero_vuelo, "
                   + "CONCAT(ao.codigo_iata, ' - ', ao.nombre) AS aeropuerto_origen, "
                   + "CONCAT(ad.codigo_iata, ' - ', ad.nombre) AS aeropuerto_destino, "
                   + "v.fecha_hora_salida, v.fecha_hora_llegada, "
                   + "r.escalas, r.detalle_escala, vt.precio, t.nombre AS nombre_tarifa "
                   + "FROM Vuelo v "
                   + "INNER JOIN Ruta r ON v.id_ruta = r.id_ruta "
                   + "INNER JOIN Aeropuerto ao ON r.id_aeropuerto_origen = ao.id_aeropuerto "
                   + "INNER JOIN Aeropuerto ad ON r.id_aeropuerto_destino = ad.id_aeropuerto "
                   + "INNER JOIN Vuelo_tarifa vt ON v.id_vuelo = vt.id_vuelo "
                   + "INNER JOIN Tarifa t ON vt.id_tarifa = t.id_tarifa "
                   + "WHERE ao.id_ciudad = ? "
                   + "AND ad.id_ciudad = ? "
                   + "AND DATE(v.fecha_hora_salida) = ? "
                   + "AND v.estado = 'Programado' "
                   + "ORDER BY v.fecha_hora_salida ASC, vt.precio ASC";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCiudadOrigen);
            ps.setInt(2, idCiudadDestino);
            ps.setString(3, fechaSalida); // Se compara directamente con la parte DATE del DATETIME

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VueloDTO vuelo = new VueloDTO();
                    vuelo.setIdVuelo(rs.getInt("id_vuelo"));
                    vuelo.setNumeroVuelo(rs.getString("numero_vuelo"));
                    vuelo.setAeropuertoOrigen(rs.getString("aeropuerto_origen"));
                    vuelo.setAeropuertoDestino(rs.getString("aeropuerto_destino"));
                    vuelo.setFechaHoraSalida(rs.getTimestamp("fecha_hora_salida"));
                    vuelo.setFechaHoraLlegada(rs.getTimestamp("fecha_hora_llegada"));
                    vuelo.setEscalas(rs.getInt("escalas"));
                    vuelo.setDetalleEscala(rs.getString("detalle_escala"));
                    vuelo.setPrecioBase(rs.getDouble("precio"));
                    vuelo.setNombreTarifa(rs.getString("nombre_tarifa"));

                    listaVuelos.add(vuelo);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al ejecutar la consulta de vuelos: " + e.getMessage());
            e.printStackTrace();
        }

        return listaVuelos;
    }
}