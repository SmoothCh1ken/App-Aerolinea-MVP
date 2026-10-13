package modelo.modulo_consulta;

/**
 * Entidad que representa una Clase de Servicio en el sistema.
 * Utilizada para poblar el campo desplegable de clases (RF-02).
 */
public class ClaseServicio {

    // Atributos privados en lowerCamelCase
    private int idClase;
    private String nombre;

    // Constructor vacío
    public ClaseServicio() {
    }

    // Constructor completo
    public ClaseServicio(int idClase, String nombre) {
        this.idClase = idClase;
        this.nombre = nombre;
    }

    // --- GETTERS Y SETTERS ---

    public int getIdClase() {
        return idClase;
    }

    public void setIdClase(int idClase) {
        this.idClase = idClase;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Sobreescritura para Swing:
     * Retorna el nombre de la clase para que se visualice correctamente en el JComboBox.
     */
    @Override
    public String toString() {
        return nombre;
    }
}