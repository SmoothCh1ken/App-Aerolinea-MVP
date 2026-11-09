package modelo.modulo_seguridad;

/** Incidente reportado al área de Tecnología (RF-29). */
public class Incidente {

    private final int id;
    private final String fecha;
    private final int idUsuario;
    private final String usuario;
    private final String tipo;
    private final String detalle;
    private final String estado;

    public Incidente(int id, String fecha, int idUsuario, String usuario, String tipo, String detalle, String estado) {
        this.id = id;
        this.fecha = fecha;
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.tipo = tipo;
        this.detalle = detalle;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getFecha() { return fecha; }
    public int getIdUsuario() { return idUsuario; }
    public String getUsuario() { return usuario; }
    public String getTipo() { return tipo; }
    public String getDetalle() { return detalle; }
    public String getEstado() { return estado; }
}
