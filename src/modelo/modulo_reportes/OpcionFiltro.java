package modelo.modulo_reportes;

/** Opción de un desplegable de filtros (ciudad o ruta). id null = "Todas". */
public class OpcionFiltro {

    public static final OpcionFiltro TODAS = new OpcionFiltro(null, "Todas");

    private final Integer id;
    private final String etiqueta;

    public OpcionFiltro(Integer id, String etiqueta) {
        this.id = id;
        this.etiqueta = etiqueta;
    }

    public Integer getId() { return id; }

    @Override
    public String toString() { return etiqueta; }
}
