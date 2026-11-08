package modelo.modulo_seguridad;

/** Rol de un funcionario (tabla Rol). */
public class Rol {

    /** Nombre del rol que administra las cuentas; siempre debe quedar al menos una cuenta activa con él. */
    public static final String ADMINISTRADOR = "Administrador general";

    private final int id;
    private final String nombre;

    public Rol(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }

    public String getNombre() { return nombre; }

    /** Lo usa el JComboBox para mostrar el texto. */
    @Override
    public String toString() { return nombre; }
}
