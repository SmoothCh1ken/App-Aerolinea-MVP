package modelo.modulo_seguridad;

/** Tipos de evento que se guardan en la auditoría (RF-31). */
public final class EventoSeguridad {

    public static final String LOGIN_CLAVE_OK = "LOGIN_CLAVE_OK";
    public static final String LOGIN_FALLIDO = "LOGIN_FALLIDO";
    public static final String MFA_OK = "MFA_OK";
    public static final String MFA_FALLIDO = "MFA_FALLIDO";
    public static final String CUENTA_BLOQUEADA = "CUENTA_BLOQUEADA";
    public static final String CUENTA_DESBLOQUEADA = "CUENTA_DESBLOQUEADA";
    public static final String ACCESO_MODULO = "ACCESO_MODULO";
    public static final String ACCESO_DENEGADO = "ACCESO_DENEGADO";
    public static final String CIERRE_SESION = "CIERRE_SESION";
    public static final String SESION_EXPIRADA = "SESION_EXPIRADA";
    public static final String CAMBIO_CLAVE = "CAMBIO_CLAVE";
    public static final String CLAVE_RESTABLECIDA = "CLAVE_RESTABLECIDA";
    public static final String MFA_RESTABLECIDO = "MFA_RESTABLECIDO";
    public static final String USUARIO_CREADO = "USUARIO_CREADO";
    public static final String USUARIO_MODIFICADO = "USUARIO_MODIFICADO";
    public static final String ROL_CAMBIADO = "ROL_CAMBIADO";
    public static final String USUARIO_ACTIVADO = "USUARIO_ACTIVADO";
    public static final String USUARIO_DESACTIVADO = "USUARIO_DESACTIVADO";
    public static final String INCIDENTE_ATENDIDO = "INCIDENTE_ATENDIDO";

    public static final String EXITOSO = "Exitoso";
    public static final String FALLIDO = "Fallido";

    /** Para el filtro de eventos de la pantalla de auditoría. */
    public static final String[] TODOS = {
        LOGIN_CLAVE_OK, LOGIN_FALLIDO, MFA_OK, MFA_FALLIDO, CUENTA_BLOQUEADA, CUENTA_DESBLOQUEADA,
        ACCESO_MODULO, ACCESO_DENEGADO, CIERRE_SESION, SESION_EXPIRADA, CAMBIO_CLAVE,
        CLAVE_RESTABLECIDA, MFA_RESTABLECIDO, USUARIO_CREADO, USUARIO_MODIFICADO, ROL_CAMBIADO,
        USUARIO_ACTIVADO, USUARIO_DESACTIVADO, INCIDENTE_ATENDIDO
    };

    private EventoSeguridad() { }
}
