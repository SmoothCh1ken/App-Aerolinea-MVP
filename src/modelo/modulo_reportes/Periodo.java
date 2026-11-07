package modelo.modulo_reportes;

/** Forma de agrupar los reportes en el tiempo (RF-33: períodos de tiempo). */
public enum Periodo {
    DIA("Día", "%Y-%m-%d"),
    MES("Mes", "%Y-%m"),
    ANIO("Año", "%Y");

    private final String etiqueta;
    private final String formatoSql; // formato de DATE_FORMAT en MySQL

    Periodo(String etiqueta, String formatoSql) {
        this.etiqueta = etiqueta;
        this.formatoSql = formatoSql;
    }

    public String getFormatoSql() { return formatoSql; }

    /** Lo usa el JComboBox para mostrar el texto. */
    @Override
    public String toString() { return etiqueta; }
}
