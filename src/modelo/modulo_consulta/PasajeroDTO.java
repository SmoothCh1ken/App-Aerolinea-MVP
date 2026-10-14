package modelo.modulo_consulta;

import java.util.Date;

/**
 * Data Transfer Object (DTO) que almacena la información de un pasajero.
 * Actualizado para cumplir estrictamente con el esquema de la tabla Persona
 * (incluyendo nacionalidad y dirección).
 */
public class PasajeroDTO {

    private String nombres;
    private String apellidos;
    private String tipoDocumento; // Ahora almacenará abreviaturas: 'CC', 'TI', 'RC', etc.
    private String numeroDocumento;
    private Date fechaNacimiento;
    private String categoria; 
    
    // Nuevos campos obligatorios según base de datos
    private String nacionalidad;
    private String direccion; 
    
    private String telefono; 
    private String correo; 
    private Ciudad ciudadResidencia; 

    public PasajeroDTO() {
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

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Ciudad getCiudadResidencia() { return ciudadResidencia; }
    public void setCiudadResidencia(Ciudad ciudadResidencia) { this.ciudadResidencia = ciudadResidencia; }
}