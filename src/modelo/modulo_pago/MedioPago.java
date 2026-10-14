package modelo.modulo_pago;

/**
 * Medio de pago habilitado (tabla Medio_pago).
 * RF-18: el comprador solo puede pagar con medios autorizados por la aerolínea.
 */
public class MedioPago {

    /** Tipos de medio que el módulo sabe procesar. */
    public enum Tipo { PSE, TARJETA, TRANSFERENCIA, BONO, MILLAS, OTRO }

    private final int id;
    private final String nombre;

    public MedioPago(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }

    public String getNombre() { return nombre; }

    /**
     * Deduce el tipo a partir del nombre guardado en la BD
     * (se compara sin tildes para no depender de la codificación del archivo).
     */
    public Tipo getTipo() {
        String n = nombre == null ? "" : nombre.toLowerCase();
        if (n.contains("pse")) return Tipo.PSE;
        if (n.contains("tarjeta")) return Tipo.TARJETA;
        if (n.contains("transferencia")) return Tipo.TRANSFERENCIA;
        if (n.contains("bono")) return Tipo.BONO;
        if (n.contains("milla")) return Tipo.MILLAS;
        return Tipo.OTRO;
    }

    /** Lo usa el JComboBox para mostrar el texto. */
    @Override
    public String toString() { return nombre; }
}
