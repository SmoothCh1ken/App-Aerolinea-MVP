package modelo.modulo_reportes;

/** Fragmentos de SQL que se repiten en varios reportes (los alias son ru, ao y ad para la ruta). */
final class SqlComun {

    private SqlComun() { }

    /** Desde un Tramo_pasajero (alias tp) hasta su Reserva (alias r). */
    static final String JOIN_RESERVA_DE_TRAMO =
            "JOIN Reserva_pasajero rp ON rp.id_reserva_pasajero = tp.id_reserva_pasajero "
          + "JOIN Reserva r ON r.id_reserva = rp.id_reserva ";

    /** Desde un Vuelo (alias v) hasta su ruta y los aeropuertos de origen (ao) y destino (ad). */
    static final String JOIN_RUTA_DE_VUELO =
            "JOIN Ruta ru ON ru.id_ruta = v.id_ruta "
          + "JOIN Aeropuerto ao ON ao.id_aeropuerto = ru.id_aeropuerto_origen "
          + "JOIN Aeropuerto ad ON ad.id_aeropuerto = ru.id_aeropuerto_destino ";

    static final String FROM_VUELO = "FROM Vuelo v " + JOIN_RUTA_DE_VUELO;

    /** Capacidad total del vuelo (suma de cupos de todas sus tarifas). */
    static final String CUPOS_DEL_VUELO =
            "(SELECT COALESCE(SUM(vt.cupos_totales), 0) FROM Vuelo_tarifa vt WHERE vt.id_vuelo = v.id_vuelo)";

    /** Cupos ocupados: misma regla que el módulo de cambios (reservas pendientes o confirmadas). */
    static final String OCUPADOS_DEL_VUELO =
            "(SELECT COUNT(*) FROM Tramo_pasajero tp "
          + "JOIN Reserva_pasajero rp ON rp.id_reserva_pasajero = tp.id_reserva_pasajero "
          + "JOIN Categoria_pasajero cp ON cp.id_categoria = rp.id_categoria "
          + "JOIN Reserva r ON r.id_reserva = rp.id_reserva "
          + "WHERE tp.id_vuelo = v.id_vuelo AND cp.ocupa_cupo = 1 "
          + "AND r.estado IN ('Pendiente de pago', 'Confirmada'))";

    static final String PASAJEROS_CONFIRMADOS_DEL_VUELO =
            "(SELECT COUNT(*) FROM Tramo_pasajero tp "
          + "JOIN Reserva_pasajero rp ON rp.id_reserva_pasajero = tp.id_reserva_pasajero "
          + "JOIN Reserva r ON r.id_reserva = rp.id_reserva "
          + "WHERE tp.id_vuelo = v.id_vuelo AND r.estado = 'Confirmada')";
}
