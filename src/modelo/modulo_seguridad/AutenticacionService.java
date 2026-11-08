package modelo.modulo_seguridad;

import java.sql.SQLException;

/**
 * Reglas de autenticación (RF-28, RF-29, RF-30 y RF-31).
 *
 * Primer factor: usuario y contraseña. Segundo factor: código TOTP de 6 dígitos.
 * Los fallos de ambos factores suman al mismo contador; al llegar al máximo de la política
 * la cuenta se bloquea temporalmente y se reporta un incidente al área de Tecnología.
 * Cada paso queda en la auditoría.
 */
public class AutenticacionService {

    /** Resultado de un paso de autenticación. */
    public static class Resultado {
        private final boolean ok;
        private final String mensaje;
        private final Usuario usuario;
        private final boolean cuentaBloqueada;

        private Resultado(boolean ok, String mensaje, Usuario usuario, boolean cuentaBloqueada) {
            this.ok = ok;
            this.mensaje = mensaje;
            this.usuario = usuario;
            this.cuentaBloqueada = cuentaBloqueada;
        }

        static Resultado exito(Usuario u) { return new Resultado(true, null, u, false); }
        static Resultado fallo(String mensaje, boolean bloqueada) { return new Resultado(false, mensaje, null, bloqueada); }

        public boolean isOk() { return ok; }
        public String getMensaje() { return mensaje; }
        public Usuario getUsuario() { return usuario; }
        /** true si la cuenta quedó bloqueada: la pantalla debe volver al primer paso. */
        public boolean isCuentaBloqueada() { return cuentaBloqueada; }
    }

    private static final String MENSAJE_GENERICO = "Usuario o contraseña incorrectos.";

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RolDAO rolDAO = new RolDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();
    private final IncidenteDAO incidenteDAO = new IncidenteDAO();
    private final PoliticaDAO politicaDAO = new PoliticaDAO();

    // ---------- primer factor ----------

    public Resultado primerFactor(String nombreUsuario, char[] clave) throws SQLException {
        Usuario u = usuarioDAO.buscarPorUsuario(nombreUsuario);
        if (u == null) {
            // mismo mensaje que una clave incorrecta, para no revelar qué usuarios existen
            auditoriaDAO.registrar(null, nombreUsuario, EventoSeguridad.LOGIN_FALLIDO,
                    EventoSeguridad.FALLIDO, "Usuario inexistente");
            return Resultado.fallo(MENSAJE_GENERICO, false);
        }
        if (Usuario.INACTIVO.equals(u.getEstado())) {
            auditoriaDAO.registrar(u.getIdUsuario(), u.getUsuario(), EventoSeguridad.LOGIN_FALLIDO,
                    EventoSeguridad.FALLIDO, "Cuenta inactiva");
            return Resultado.fallo("La cuenta está inactiva. Comuníquese con el administrador.", false);
        }
        if (Usuario.BLOQUEADO.equals(u.getEstado())) {
            if (u.isBloqueoVigente()) {
                auditoriaDAO.registrar(u.getIdUsuario(), u.getUsuario(), EventoSeguridad.LOGIN_FALLIDO,
                        EventoSeguridad.FALLIDO, "Intento de ingreso con la cuenta bloqueada");
                return Resultado.fallo("La cuenta está bloqueada temporalmente. Intente de nuevo en "
                        + Math.max(1, u.getMinutosRestantes()) + " minuto(s).", true);
            }
            // el plazo del bloqueo ya venció: se desbloquea sola
            usuarioDAO.establecerEstado(u.getIdUsuario(), Usuario.ACTIVO);
            u.setIntentosFallidos(0);
            auditoriaDAO.registrar(u.getIdUsuario(), u.getUsuario(), EventoSeguridad.CUENTA_DESBLOQUEADA,
                    EventoSeguridad.EXITOSO, "Desbloqueo automático al vencer el plazo");
        }
        if (!ServicioClave.verificar(clave, u.getSalClave(), u.getHashClave())) {
            return registrarFallo(u, EventoSeguridad.LOGIN_FALLIDO, "Contraseña incorrecta", MENSAJE_GENERICO);
        }
        auditoriaDAO.registrar(u.getIdUsuario(), u.getUsuario(), EventoSeguridad.LOGIN_CLAVE_OK,
                EventoSeguridad.EXITOSO, "Primer factor validado");
        return Resultado.exito(u);
    }

    // ---------- segundo factor ----------

    public Resultado segundoFactor(Usuario u, String codigo) throws SQLException {
        if (!ServicioTotp.verificar(u.getSecretoMfa(), codigo)) {
            return registrarFallo(u, EventoSeguridad.MFA_FALLIDO, "Código del segundo factor inválido",
                    "El código de verificación es incorrecto o venció.");
        }
        usuarioDAO.registrarAccesoExitoso(u.getIdUsuario());
        auditoriaDAO.registrar(u.getIdUsuario(), u.getUsuario(), EventoSeguridad.MFA_OK,
                EventoSeguridad.EXITOSO, "Autenticación multifactor completada");
        return Resultado.exito(u);
    }

    /** Abre la sesión con los permisos del rol y el tiempo de inactividad de la política. */
    public void completarSesion(Usuario u) throws SQLException {
        PoliticaSeguridad politica = politicaDAO.cargar();
        SesionActual.iniciar(u, rolDAO.modulosDeRol(u.getIdRol()), politica.getMinutosInactividad());
    }

    // ---------- cierre de sesión ----------

    public void cerrarSesion(boolean porInactividad) throws SQLException {
        Usuario u = SesionActual.getUsuario();
        if (u == null) {
            return;
        }
        SesionActual.cerrar();
        auditoriaDAO.registrar(u.getIdUsuario(), u.getUsuario(),
                porInactividad ? EventoSeguridad.SESION_EXPIRADA : EventoSeguridad.CIERRE_SESION,
                EventoSeguridad.EXITOSO,
                porInactividad ? "Sesión cerrada por inactividad" : "El usuario cerró la sesión");
    }

    // ---------- apoyo ----------

    private Resultado registrarFallo(Usuario u, String evento, String detalle, String mensaje) throws SQLException {
        PoliticaSeguridad politica = politicaDAO.cargar();
        int intentos = usuarioDAO.incrementarIntentos(u.getIdUsuario());
        auditoriaDAO.registrar(u.getIdUsuario(), u.getUsuario(), evento, EventoSeguridad.FALLIDO,
                detalle + " (intento " + intentos + " de " + politica.getMaxIntentos() + ")");

        if (intentos >= politica.getMaxIntentos()) {
            usuarioDAO.bloquear(u.getIdUsuario(), politica.getMinutosBloqueo());
            String resumen = intentos + " intentos fallidos consecutivos; cuenta bloqueada "
                    + politica.getMinutosBloqueo() + " minuto(s).";
            auditoriaDAO.registrar(u.getIdUsuario(), u.getUsuario(), EventoSeguridad.CUENTA_BLOQUEADA,
                    EventoSeguridad.EXITOSO, resumen);
            incidenteDAO.crear(u.getIdUsuario(), "Cuenta bloqueada", resumen);        // RF-29: reporte a Tecnología
            ServicioReporteTi.enviar("Cuenta bloqueada: " + u.getUsuario(), resumen);
            return Resultado.fallo("La cuenta fue bloqueada por " + politica.getMinutosBloqueo()
                    + " minuto(s) al superar los intentos permitidos. El incidente se reportó al área de Tecnología.",
                    true);
        }
        return Resultado.fallo(mensaje, false);
    }
}
