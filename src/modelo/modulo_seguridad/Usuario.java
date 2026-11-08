package modelo.modulo_seguridad;

/** Cuenta institucional de un funcionario (tabla Usuario_sistema). */
public class Usuario {

    public static final String ACTIVO = "Activo";
    public static final String INACTIVO = "Inactivo";
    public static final String BLOQUEADO = "Bloqueado";

    private int idUsuario;
    private String usuario;
    private String nombres;
    private String apellidos;
    private String documento;
    private String correo;
    private String cargo;
    private int idRol;
    private String rolNombre;
    private String estado;
    private int intentosFallidos;
    private boolean bloqueoVigente;      // bloqueada y el plazo aún no vence
    private int minutosRestantes;        // minutos que faltan para el desbloqueo automático
    private String ultimoAcceso;
    private String secretoMfa;           // clave del segundo factor (TOTP)
    private String salClave;
    private String hashClave;

    public String getNombreCompleto() { return nombres + " " + apellidos; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int v) { this.idUsuario = v; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String v) { this.usuario = v; }
    public String getNombres() { return nombres; }
    public void setNombres(String v) { this.nombres = v; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String v) { this.apellidos = v; }
    public String getDocumento() { return documento; }
    public void setDocumento(String v) { this.documento = v; }
    public String getCorreo() { return correo; }
    public void setCorreo(String v) { this.correo = v; }
    public String getCargo() { return cargo; }
    public void setCargo(String v) { this.cargo = v; }
    public int getIdRol() { return idRol; }
    public void setIdRol(int v) { this.idRol = v; }
    public String getRolNombre() { return rolNombre; }
    public void setRolNombre(String v) { this.rolNombre = v; }
    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }
    public int getIntentosFallidos() { return intentosFallidos; }
    public void setIntentosFallidos(int v) { this.intentosFallidos = v; }
    public boolean isBloqueoVigente() { return bloqueoVigente; }
    public void setBloqueoVigente(boolean v) { this.bloqueoVigente = v; }
    public int getMinutosRestantes() { return minutosRestantes; }
    public void setMinutosRestantes(int v) { this.minutosRestantes = v; }
    public String getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(String v) { this.ultimoAcceso = v; }
    public String getSecretoMfa() { return secretoMfa; }
    public void setSecretoMfa(String v) { this.secretoMfa = v; }
    public String getSalClave() { return salClave; }
    public void setSalClave(String v) { this.salClave = v; }
    public String getHashClave() { return hashClave; }
    public void setHashClave(String v) { this.hashClave = v; }
}
