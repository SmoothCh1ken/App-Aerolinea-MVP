package modelo.modulo_reportes;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Va armando las condiciones AND de un reporte junto con sus parámetros,
 * para no concatenar valores del usuario dentro del SQL (evita inyección SQL).
 */
final class ConsultaBuilder {

    private final StringBuilder condiciones = new StringBuilder();
    private final List<Object> parametros = new ArrayList<>();

    ConsultaBuilder y(String condicion, Object... valores) {
        condiciones.append(" AND ").append(condicion);
        for (Object v : valores) {
            parametros.add(v);
        }
        return this;
    }

    /**
     * Filtro de fechas sobre una columna DATETIME. Se envían como texto para que el
     * driver no les aplique conversiones de zona horaria (hasta es inclusivo).
     */
    ConsultaBuilder fechas(FiltroReporte f, String columna) {
        if (f.getDesde() != null) {
            y(columna + " >= ?", f.getDesde() + " 00:00:00");
        }
        if (f.getHasta() != null) {
            y(columna + " < ?", f.getHasta().plusDays(1) + " 00:00:00");
        }
        return this;
    }

    /** Ciudad de origen/destino y ruta. Requiere los alias ru, ao y ad en la consulta. */
    ConsultaBuilder geografia(FiltroReporte f) {
        if (f.getIdCiudadOrigen() != null) {
            y("ao.id_ciudad = ?", f.getIdCiudadOrigen());
        }
        if (f.getIdCiudadDestino() != null) {
            y("ad.id_ciudad = ?", f.getIdCiudadDestino());
        }
        if (f.getIdRuta() != null) {
            y("ru.id_ruta = ?", f.getIdRuta());
        }
        return this;
    }

    /** Igual que geografia(), pero para consultas sobre Reserva: exige algún tramo en esa ruta o ciudad. */
    ConsultaBuilder geografiaDeReserva(FiltroReporte f, String columnaIdReserva) {
        if (!f.tieneGeografia()) {
            return this;
        }
        ConsultaBuilder g = new ConsultaBuilder().geografia(f);
        return y("EXISTS (SELECT 1 FROM Tramo_pasajero tpg "
               + "JOIN Reserva_pasajero rpg ON rpg.id_reserva_pasajero = tpg.id_reserva_pasajero "
               + "JOIN Vuelo vg ON vg.id_vuelo = tpg.id_vuelo "
               + "JOIN Ruta ru ON ru.id_ruta = vg.id_ruta "
               + "JOIN Aeropuerto ao ON ao.id_aeropuerto = ru.id_aeropuerto_origen "
               + "JOIN Aeropuerto ad ON ad.id_aeropuerto = ru.id_aeropuerto_destino "
               + "WHERE rpg.id_reserva = " + columnaIdReserva + g.condiciones() + ")",
                g.parametros.toArray());
    }

    String condiciones() { return condiciones.toString(); }

    void asignar(PreparedStatement ps) throws SQLException {
        for (int i = 0; i < parametros.size(); i++) {
            ps.setObject(i + 1, parametros.get(i));
        }
    }
}
