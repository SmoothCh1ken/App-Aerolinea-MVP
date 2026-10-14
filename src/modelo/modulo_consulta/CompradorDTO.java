package modelo.modulo_consulta;

/**
 * Data Transfer Object (DTO) que almacena la información del Comprador.
 * Actualizado para reflejar que el Comprador también se insertará en la tabla Persona,
 * requiriendo nacionalidad y un objeto Ciudad.
 */
public class CompradorDTO {

    private String nombres;
    private String apellidos;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nacionalidad; // Nuevo: Obligatorio en tabla Persona
    private Ciudad ciudadResidencia; // Nuevo: Obligatorio en tabla Persona
    private String direccion;
    private String telefono;
    private String correo;

    public CompradorDTO() {
    }

    // --- GETTERS Y SETTERS ---

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public Ciudad getCiudadResidencia() { return ciudadResidencia; }
    public void setCiudadResidencia(Ciudad ciudadResidencia) { this.ciudadResidencia = ciudadResidencia; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}