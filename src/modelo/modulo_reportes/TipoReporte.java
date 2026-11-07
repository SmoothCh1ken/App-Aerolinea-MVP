package modelo.modulo_reportes;

/**
 * Reportes que se pueden generar con los datos actuales de la base (RF-32).
 * No se incluyen rentabilidad, puntualidad, equipajes ni reclamaciones porque
 * el esquema aerolinea_app_db no guarda esa información.
 */
public enum TipoReporte {
    VENTAS("Ventas", "la fecha de la compra", true),
    OCUPACION("Ocupación de vuelos", "la fecha de salida del vuelo", false),
    CANCELACIONES("Cancelaciones de reservas", "la fecha de cancelación de la reserva", true),
    REPROGRAMACIONES("Vuelos reprogramados y cancelados", "la fecha de salida del vuelo", false),
    AERONAVES("Utilización de aeronaves", "la fecha de salida del vuelo", false),
    SERVICIOS("Ingresos por servicios adicionales", "la fecha de la compra", true);

    private final String titulo;
    private final String fechaFiltrada;
    private final boolean usaPeriodo;

    TipoReporte(String titulo, String fechaFiltrada, boolean usaPeriodo) {
        this.titulo = titulo;
        this.fechaFiltrada = fechaFiltrada;
        this.usaPeriodo = usaPeriodo;
    }

    public String getTitulo() { return titulo; }

    /** Texto de ayuda: qué fecha filtra este reporte y si agrupa por período. */
    public String getNota() {
        return "Las fechas filtran por " + fechaFiltrada + "."
                + (usaPeriodo ? " Los resultados se agrupan por el período elegido." : "");
    }

    @Override
    public String toString() { return titulo; }
}
