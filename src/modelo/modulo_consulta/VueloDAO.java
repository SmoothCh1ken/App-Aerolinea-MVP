package modelo.modulo_consulta;

import modelo.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO encargada de las operaciones de lectura en la base de datos
 * para el módulo de consulta de vuelos. 
 * Implementa validación avanzada de viajes redondos para UX (EXISTS).
 */
public class VueloDAO {

    /**
     * Consulta la lista completa de ciudades (RF-01).
     */
    public List<Ciudad> obtenerTodasLasCiudades() {
        List<Ciudad> ciudades = new ArrayList<>();
        String sql = "SELECT id_ciudad, nombre, pais FROM Ciudad ORDER BY nombre ASC";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ciudades.add(new Ciudad(rs.getInt("id_ciudad"), rs.getString("nombre"), rs.getString("pais")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ciudades;
    }

    /**
     * Consulta la lista de clases de servicio (RF-02).
     */
    public List<ClaseServicio> obtenerClasesServicio() {
        List<ClaseServicio> clases = new ArrayList<>();
        String sql = "SELECT id_clase, nombre FROM Clase_servicio ORDER BY id_clase ASC";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clases.add(new ClaseServicio(rs.getInt("id_clase"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clases;
    }

    /**
     * Consulta principal dinámica que integra Búsqueda, Filtros (RF-04), 
     * Ordenamiento (RF-04), Restricción de Pasajeros (RF-02), Compatibilidad (RF-05)
     * y Evita callejones sin salida garantizando el regreso.
     */
    public List<VueloDTO> buscarVuelosAvanzado(
            int idOrigen, int idDestino, String fechaSalida, String fechaRegreso, // <-- NUEVO PARÁMETRO
            int idClase, int totalPasajeros, 
            Timestamp fechaMinimaSalida, 
            String criterioOrdenamiento, 
            boolean soloDirectos, double precioMaximo, boolean conEquipaje
    ) {
        
        List<VueloDTO> listaVuelos = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT v.id_vuelo, vt.id_tarifa, v.numero_vuelo, ");
        sql.append("CONCAT(ao.codigo_iata, ' - ', ao.nombre) AS aeropuerto_origen, ");
        sql.append("CONCAT(ad.codigo_iata, ' - ', ad.nombre) AS aeropuerto_destino, ");
        sql.append("v.fecha_hora_salida, v.fecha_hora_llegada, ");
        sql.append("TIMESTAMPDIFF(MINUTE, v.fecha_hora_salida, v.fecha_hora_llegada) AS minutos_vuelo, ");
        sql.append("r.escalas, r.detalle_escala, ");
        sql.append("CONCAT(ae.modelo, ' (', ae.matricula, ')') AS aeronave, ");
        sql.append("cs.nombre AS clase_servicio, t.nombre AS nombre_tarifa, ");
        sql.append("vt.precio, vt.cupos_totales, t.equipaje_incluido_kg, ");
        sql.append("CONCAT('Cambios: ', IF(t.permite_cambios, 'Sí', 'No'), ' | Reembolso: ', IF(t.permite_reembolso, 'Sí', 'No')) AS restricciones ");
        
        sql.append("FROM Vuelo v ");
        sql.append("INNER JOIN Ruta r ON v.id_ruta = r.id_ruta ");
        sql.append("INNER JOIN Aeropuerto ao ON r.id_aeropuerto_origen = ao.id_aeropuerto ");
        sql.append("INNER JOIN Aeropuerto ad ON r.id_aeropuerto_destino = ad.id_aeropuerto ");
        sql.append("INNER JOIN Aeronave ae ON v.id_aeronave = ae.id_aeronave ");
        sql.append("INNER JOIN Vuelo_tarifa vt ON v.id_vuelo = vt.id_vuelo ");
        sql.append("INNER JOIN Tarifa t ON vt.id_tarifa = t.id_tarifa ");
        sql.append("INNER JOIN Clase_servicio cs ON t.id_clase = cs.id_clase ");
        
        // Condiciones obligatorias de la ida
        sql.append("WHERE ao.id_ciudad = ? AND ad.id_ciudad = ? ");
        sql.append("AND DATE(v.fecha_hora_salida) = ? ");
        sql.append("AND v.estado = 'Programado' ");
        sql.append("AND cs.id_clase = ? ");
        sql.append("AND vt.cupos_totales >= ? ");

        // RF-05: Para cuando consultamos la vuelta específicamente
        if (fechaMinimaSalida != null) {
            sql.append("AND v.fecha_hora_salida > ? ");
        }

        // NUEVO: Validación de Existencia de Regreso (Evita mostrar Idas si no hay Vuelta)
        if (fechaRegreso != null) {
            sql.append("AND EXISTS ( ");
            sql.append("  SELECT 1 FROM Vuelo v2 ");
            sql.append("  INNER JOIN Ruta r2 ON v2.id_ruta = r2.id_ruta ");
            sql.append("  INNER JOIN Aeropuerto ao2 ON r2.id_aeropuerto_origen = ao2.id_aeropuerto ");
            sql.append("  INNER JOIN Aeropuerto ad2 ON r2.id_aeropuerto_destino = ad2.id_aeropuerto ");
            sql.append("  INNER JOIN Vuelo_tarifa vt2 ON v2.id_vuelo = vt2.id_vuelo ");
            sql.append("  INNER JOIN Tarifa t2 ON vt2.id_tarifa = t2.id_tarifa ");
            sql.append("  WHERE ao2.id_ciudad = ? AND ad2.id_ciudad = ? "); // Origen y Destino invertidos
            sql.append("  AND DATE(v2.fecha_hora_salida) = ? "); // La fecha de regreso
            sql.append("  AND v2.fecha_hora_salida > v.fecha_hora_llegada "); // Cruce clave: Regreso sale después que llega la Ida
            sql.append("  AND t2.id_clase = ? "); // Misma clase
            sql.append("  AND vt2.cupos_totales >= ? "); // Mismos cupos
            sql.append("  AND v2.estado = 'Programado' ");
            sql.append(") ");
        }

        // Aplicación de Filtros (RF-04)
        if (soloDirectos) {
            sql.append("AND r.escalas = 0 ");
        }
        if (precioMaximo > 0) {
            sql.append("AND vt.precio <= ? ");
        }
        if (conEquipaje) {
            sql.append("AND t.equipaje_incluido_kg > 0 ");
        }

        // Aplicación de Ordenamiento (RF-04)
        switch (criterioOrdenamiento) {
            case "Duración del vuelo":
                sql.append("ORDER BY minutos_vuelo ASC, vt.precio ASC");
                break;
            case "Horario de salida":
                sql.append("ORDER BY v.fecha_hora_salida ASC, vt.precio ASC");
                break;
            case "Cantidad de escalas":
                sql.append("ORDER BY r.escalas ASC, vt.precio ASC");
                break;
            case "Precio":
            default:
                sql.append("ORDER BY vt.precio ASC, v.fecha_hora_salida ASC");
                break;
        }

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            
            // Parámetros obligatorios de Ida
            ps.setInt(paramIndex++, idOrigen);
            ps.setInt(paramIndex++, idDestino);
            ps.setString(paramIndex++, fechaSalida);
            ps.setInt(paramIndex++, idClase);
            ps.setInt(paramIndex++, totalPasajeros);

            if (fechaMinimaSalida != null) {
                ps.setTimestamp(paramIndex++, fechaMinimaSalida);
            }

            // Parámetros inyectados a la subconsulta EXISTS si se solicita regreso
            if (fechaRegreso != null) {
                ps.setInt(paramIndex++, idDestino); // El origen del regreso es el destino de ida
                ps.setInt(paramIndex++, idOrigen);  // El destino del regreso es el origen de ida
                ps.setString(paramIndex++, fechaRegreso);
                ps.setInt(paramIndex++, idClase);
                ps.setInt(paramIndex++, totalPasajeros);
            }

            if (precioMaximo > 0) {
                ps.setDouble(paramIndex++, precioMaximo);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VueloDTO vuelo = new VueloDTO();
                    vuelo.setIdVuelo(rs.getInt("id_vuelo"));
                    vuelo.setIdTarifa(rs.getInt("id_tarifa"));
                    vuelo.setNumeroVuelo(rs.getString("numero_vuelo"));
                    vuelo.setAeropuertoOrigen(rs.getString("aeropuerto_origen"));
                    vuelo.setAeropuertoDestino(rs.getString("aeropuerto_destino"));
                    vuelo.setFechaHoraSalida(rs.getTimestamp("fecha_hora_salida"));
                    vuelo.setFechaHoraLlegada(rs.getTimestamp("fecha_hora_llegada"));
                    vuelo.setEscalas(rs.getInt("escalas"));
                    vuelo.setDetalleEscala(rs.getString("detalle_escala"));
                    vuelo.setAeronave(rs.getString("aeronave"));
                    vuelo.setClaseServicio(rs.getString("clase_servicio"));
                    vuelo.setNombreTarifa(rs.getString("nombre_tarifa"));
                    vuelo.setPrecioBase(rs.getDouble("precio"));
                    vuelo.setCuposDisponibles(rs.getInt("cupos_totales"));
                    vuelo.setEquipajeIncluido(rs.getInt("equipaje_incluido_kg"));
                    vuelo.setRestricciones(rs.getString("restricciones"));

                    int minutosTotales = rs.getInt("minutos_vuelo");
                    int horas = minutosTotales / 60;
                    int minutos = minutosTotales % 60;
                    vuelo.setDuracion(horas + "h " + minutos + "m");

                    listaVuelos.add(vuelo);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al ejecutar búsqueda avanzada: " + e.getMessage());
            e.printStackTrace();
        }

        return listaVuelos;
    }
}