package modelo.modulo_consulta;

/**
 * Entidad que representa una Ciudad en el sistema.
 * Utilizada para poblar los campos desplegables de origen y destino.
 */
public class Ciudad {

    // Atributos privados en lowerCamelCase
    private int idCiudad;
    private String nombre;
    private String pais;

    // Constructor vacío (necesario para inicializaciones dinámicas)
    public Ciudad() {
    }

    // Constructor completo
    public Ciudad(int idCiudad, String nombre, String pais) {
        this.idCiudad = idCiudad;
        this.nombre = nombre;
        this.pais = pais;
    }

    // --- GETTERS Y SETTERS ---

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    /**
     * Sobreescritura clave para Swing:
     * Al agregar un objeto Ciudad a un JComboBox, la interfaz gráfica llamará
     * automáticamente a este método para renderizar el texto que ve el usuario.
     */
    @Override
    public String toString() {
        if (pais != null && !pais.trim().isEmpty()) {
            return nombre + " (" + pais + ")";
        }
        return nombre;
    }
}