package modelo.modulo_seguridad;

import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * Administración de cuentas institucionales y perfiles (RF-26, RF-27 y RF-31).
 * Cada operación deja su registro de auditoría. El "actor" es quien la realiza.
 */
public class UsuarioService {

    private static final Pattern FORMATO_USUARIO = Pattern.compile("[a-z0-9._]{3,30}");
    private static final Pattern FORMATO_CORREO = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();
    private final IncidenteDAO incidenteDAO = new IncidenteDAO();

    // ---------- RF-26: cuentas ----------

    /**
     * Crea la cuenta institucional de un funcionario. Cada funcionario tiene una sola cuenta:
     * no se repiten el usuario, el documento ni el correo institucional.
     * @return el secreto del segundo factor, que se muestra una única vez
     */
    public String crear(Usuario datos, char[] clave, Usuario actor) throws SQLException, ReglaSeguridadException {
        datos.setUsuario(texto(datos.getUsuario()).toLowerCase());
        datos.setNombres(texto(datos.getNombres()));
        datos.setApellidos(texto(datos.getApellidos()));
        datos.setDocumento(texto(datos.getDocumento()));
        datos.setCorreo(texto(datos.getCorreo()).toLowerCase());
        datos.setCargo(texto(datos.getCargo()));

        if (datos.getNombres().isEmpty() || datos.getApellidos().isEmpty() || datos.getDocumento().isEmpty()
                || datos.getCorreo().isEmpty() || datos.getCargo().isEmpty()) {
            throw new ReglaSeguridadException("Complete nombres, apellidos, documento, correo y cargo.");
        }
        if (!FORMATO_USUARIO.matcher(datos.getUsuario()).matches()) {
            throw new ReglaSeguridadException(
                    "El usuario debe tener de 3 a 30 caracteres: letras minúsculas, números, punto o guion bajo.");
        }
        if (!FORMATO_CORREO.matcher(datos.getCorreo()).matches()) {
            throw new ReglaSeguridadException("El correo institucional no tiene un formato válido.");
        }
        if (datos.getIdRol() <= 0) {
            throw new ReglaSeguridadException("Seleccione el rol del funcionario.");
        }
        String errorClave = ServicioClave.validarPolitica(clave, datos.getUsuario());
        if (errorClave != null) {
            throw new ReglaSeguridadException(errorClave);
        }
        if (usuarioDAO.existe("usuario", datos.getUsuario())) {
            throw new ReglaSeguridadException("Ya existe una cuenta con el usuario \"" + datos.getUsuario() + "\".");
        }
        if (usuarioDAO.existe("numero_documento", datos.getDocumento())) {
            throw new ReglaSeguridadException(
                    "Ese funcionario ya tiene una cuenta: cada documento tiene una única cuenta institucional.");
        }
        if (usuarioDAO.existe("correo_institucional", datos.getCorreo())) {
            throw new ReglaSeguridadException("Ya existe una cuenta con ese correo institucional.");
        }

        String sal = ServicioClave.nuevaSal();
        String secreto = ServicioTotp.generarSecreto();
        datos.setSecretoMfa(secreto);
        try {
            usuarioDAO.insertar(datos, sal, ServicioClave.hash(clave, sal));
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // otro administrador la creó justo antes
                throw new ReglaSeguridadException("La cuenta ya existe (usuario, documento o correo repetido).");
            }
            throw e;
        }
        auditar(actor, EventoSeguridad.USUARIO_CREADO, EventoSeguridad.EXITOSO,
                "Cuenta \"" + datos.getUsuario() + "\" creada con el rol \"" + datos.getRolNombre() + "\"");
        return secreto;
    }

    // ---------- RF-27: roles y perfil ----------

    public void actualizarPerfil(Usuario objetivo, String cargo, int idRol, String nuevoRolNombre, Usuario actor)
            throws SQLException, ReglaSeguridadException {
        if (texto(cargo).isEmpty()) {
            throw new ReglaSeguridadException("Escriba el cargo del funcionario.");
        }
        boolean cambiaRol = idRol != objetivo.getIdRol();
        if (cambiaRol && Rol.ADMINISTRADOR.equals(objetivo.getRolNombre())
                && Usuario.ACTIVO.equals(objetivo.getEstado())) {
            exigirOtroAdministrador("quitarle el rol de administrador");
        }
        usuarioDAO.actualizarPerfil(objetivo.getIdUsuario(), texto(cargo), idRol);
        if (cambiaRol) {
            auditar(actor, EventoSeguridad.ROL_CAMBIADO, EventoSeguridad.EXITOSO,
                    "Rol de \"" + objetivo.getUsuario() + "\": " + objetivo.getRolNombre() + " -> " + nuevoRolNombre);
        }
        if (!texto(cargo).equals(objetivo.getCargo())) {
            auditar(actor, EventoSeguridad.USUARIO_MODIFICADO, EventoSeguridad.EXITOSO,
                    "Cargo de \"" + objetivo.getUsuario() + "\" actualizado a \"" + texto(cargo) + "\"");
        }
    }

    public void activar(Usuario objetivo, Usuario actor) throws SQLException {
        usuarioDAO.establecerEstado(objetivo.getIdUsuario(), Usuario.ACTIVO);
        auditar(actor, EventoSeguridad.USUARIO_ACTIVADO, EventoSeguridad.EXITOSO,
                "Cuenta \"" + objetivo.getUsuario() + "\" activada");
    }

    public void desactivar(Usuario objetivo, Usuario actor) throws SQLException, ReglaSeguridadException {
        if (objetivo.getIdUsuario() == actor.getIdUsuario()) {
            throw new ReglaSeguridadException("No puede desactivar su propia cuenta.");
        }
        if (Rol.ADMINISTRADOR.equals(objetivo.getRolNombre()) && Usuario.ACTIVO.equals(objetivo.getEstado())) {
            exigirOtroAdministrador("desactivar esta cuenta");
        }
        usuarioDAO.establecerEstado(objetivo.getIdUsuario(), Usuario.INACTIVO);
        auditar(actor, EventoSeguridad.USUARIO_DESACTIVADO, EventoSeguridad.EXITOSO,
                "Cuenta \"" + objetivo.getUsuario() + "\" desactivada");
    }

    /** Quita el bloqueo y los intentos fallidos de una cuenta (acción del área de Tecnología). */
    public void desbloquear(Usuario objetivo, Usuario actor) throws SQLException {
        usuarioDAO.establecerEstado(objetivo.getIdUsuario(), Usuario.ACTIVO);
        auditar(actor, EventoSeguridad.CUENTA_DESBLOQUEADA, EventoSeguridad.EXITOSO,
                "Desbloqueo manual de \"" + objetivo.getUsuario() + "\"");
    }

    /** Área de Tecnología: marca un incidente como atendido y, si se pide, desbloquea la cuenta afectada. */
    public void atenderIncidente(Incidente incidente, boolean desbloquear, Usuario actor) throws SQLException {
        if (desbloquear) {
            Usuario afectado = usuarioDAO.buscarPorId(incidente.getIdUsuario());
            if (afectado != null) {
                desbloquear(afectado, actor);
            }
        }
        incidenteDAO.marcarAtendido(incidente.getId(), actor.getIdUsuario());
        auditar(actor, EventoSeguridad.INCIDENTE_ATENDIDO, EventoSeguridad.EXITOSO,
                "Incidente #" + incidente.getId() + " atendido" + (desbloquear ? " (cuenta desbloqueada)" : ""));
    }

    // ---------- contraseñas y segundo factor ----------

    public void restablecerClave(Usuario objetivo, char[] nueva, Usuario actor)
            throws SQLException, ReglaSeguridadException {
        String error = ServicioClave.validarPolitica(nueva, objetivo.getUsuario());
        if (error != null) {
            throw new ReglaSeguridadException(error);
        }
        String sal = ServicioClave.nuevaSal();
        usuarioDAO.actualizarClave(objetivo.getIdUsuario(), sal, ServicioClave.hash(nueva, sal));
        auditar(actor, EventoSeguridad.CLAVE_RESTABLECIDA, EventoSeguridad.EXITOSO,
                "Contraseña de \"" + objetivo.getUsuario() + "\" restablecida");
    }

    /** Genera un secreto nuevo (por ejemplo, si el funcionario perdió el celular). Se muestra una sola vez. */
    public String restablecerSegundoFactor(Usuario objetivo, Usuario actor) throws SQLException {
        String secreto = ServicioTotp.generarSecreto();
        usuarioDAO.actualizarSecretoMfa(objetivo.getIdUsuario(), secreto);
        auditar(actor, EventoSeguridad.MFA_RESTABLECIDO, EventoSeguridad.EXITOSO,
                "Segundo factor de \"" + objetivo.getUsuario() + "\" restablecido");
        return secreto;
    }

    /** El funcionario cambia su propia contraseña. */
    public void cambiarClavePropia(Usuario actor, char[] actual, char[] nueva)
            throws SQLException, ReglaSeguridadException {
        Usuario guardado = usuarioDAO.buscarPorId(actor.getIdUsuario());
        if (guardado == null || !ServicioClave.verificar(actual, guardado.getSalClave(), guardado.getHashClave())) {
            auditar(actor, EventoSeguridad.CAMBIO_CLAVE, EventoSeguridad.FALLIDO, "Contraseña actual incorrecta");
            throw new ReglaSeguridadException("La contraseña actual no es correcta.");
        }
        String error = ServicioClave.validarPolitica(nueva, actor.getUsuario());
        if (error != null) {
            throw new ReglaSeguridadException(error);
        }
        if (ServicioClave.verificar(nueva, guardado.getSalClave(), guardado.getHashClave())) {
            throw new ReglaSeguridadException("La contraseña nueva debe ser distinta de la actual.");
        }
        String sal = ServicioClave.nuevaSal();
        usuarioDAO.actualizarClave(actor.getIdUsuario(), sal, ServicioClave.hash(nueva, sal));
        auditar(actor, EventoSeguridad.CAMBIO_CLAVE, EventoSeguridad.EXITOSO, "El usuario cambió su contraseña");
    }

    // ---------- apoyo ----------

    private void exigirOtroAdministrador(String accion) throws SQLException, ReglaSeguridadException {
        if (usuarioDAO.contarAdministradoresActivos() <= 1) {
            throw new ReglaSeguridadException("No se puede " + accion
                    + ": debe quedar al menos un administrador general activo.");
        }
    }

    private void auditar(Usuario actor, String evento, String resultado, String detalle) throws SQLException {
        auditoriaDAO.registrar(actor.getIdUsuario(), actor.getUsuario(), evento, resultado, detalle);
    }

    private String texto(String s) {
        return s == null ? "" : s.trim();
    }
}
