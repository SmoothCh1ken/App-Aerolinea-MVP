package modelo.modulo_seguridad;

/** Una fila de la auditoría de seguridad. */
public class RegistroAuditoria {

    private final String fecha;
    private final String usuario;
    private final String evento;
    private final String resultado;
    private final String detalle;

    public RegistroAuditoria(String fecha, String usuario, String evento, String resultado, String detalle) {
        this.fecha = fecha;
        this.usuario = usuario;
        this.evento = evento;
        this.resultado = resultado;
        this.detalle = detalle;
    }

    public String getFecha() { return fecha; }
    public String getUsuario() { return usuario; }
    public String getEvento() { return evento; }
    public String getResultado() { return resultado; }
    public String getDetalle() { return detalle; }
}
