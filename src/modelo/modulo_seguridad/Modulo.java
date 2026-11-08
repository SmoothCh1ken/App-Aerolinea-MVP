package modelo.modulo_seguridad;

/** Módulos internos que se controlan por rol (RF-27). El nombre coincide con Rol_modulo.modulo en la BD. */
public enum Modulo {
    USUARIOS("Gestión de usuarios"),
    OPERACIONES("Operaciones de aerolínea"),
    REPORTES("Reportes gerenciales"),
    AUDITORIA("Auditoría de seguridad"),
    INCIDENTES("Incidentes de seguridad");

    private final String titulo;

    Modulo(String titulo) { this.titulo = titulo; }

    public String getTitulo() { return titulo; }

    @Override
    public String toString() { return titulo; }
}
