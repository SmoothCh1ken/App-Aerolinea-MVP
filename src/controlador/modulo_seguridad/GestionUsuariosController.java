package controlador.modulo_seguridad;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import modelo.modulo_seguridad.ReglaSeguridadException;
import modelo.modulo_seguridad.Rol;
import modelo.modulo_seguridad.RolDAO;
import modelo.modulo_seguridad.SesionActual;
import modelo.modulo_seguridad.Usuario;
import modelo.modulo_seguridad.UsuarioDAO;
import modelo.modulo_seguridad.UsuarioService;
import vista.modulo_seguridad.GestionUsuariosFrame;
import vista.modulo_seguridad.UsuarioFormDialog;

/** Administración de cuentas institucionales, roles y segundo factor (RF-26 y RF-27). */
public class GestionUsuariosController {

    private final GestionUsuariosFrame vista;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RolDAO rolDAO = new RolDAO();
    private final UsuarioService servicio = new UsuarioService();
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Rol> roles = new ArrayList<>();

    public GestionUsuariosController(GestionUsuariosFrame vista) {
        this.vista = vista;
        vista.addNuevoListener(e -> nuevo());
        vista.addEditarListener(e -> editar());
        vista.addEstadoListener(e -> cambiarEstado());
        vista.addClaveListener(e -> restablecerClave());
        vista.addMfaListener(e -> restablecerSegundoFactor());
        vista.addDesbloquearListener(e -> desbloquear());
        vista.addActualizarListener(e -> cargar());
        try {
            roles = rolDAO.listar();
        } catch (SQLException ex) {
            vista.mostrarError("No se pudieron cargar los roles: " + ex.getMessage());
        }
        cargar();
    }

    public static GestionUsuariosController iniciar() {
        GestionUsuariosFrame frame = new GestionUsuariosFrame();
        GestionUsuariosController controlador = new GestionUsuariosController(frame);
        GuardiaAcceso.registrar(frame);
        frame.setVisible(true);
        return controlador;
    }

    private void cargar() {
        try {
            usuarios = usuarioDAO.listar();
            vista.mostrarUsuarios(usuarios);
        } catch (SQLException ex) {
            vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
        }
    }

    /** @return el funcionario elegido en la tabla, o null (y avisa) si no hay ninguno. */
    private Usuario seleccionado() {
        int fila = vista.getFilaSeleccionada();
        if (fila < 0 || fila >= usuarios.size()) {
            vista.mostrarAdvertencia("Seleccione un funcionario de la tabla.");
            return null;
        }
        return usuarios.get(fila);
    }

    private void nuevo() {
        UsuarioFormDialog dialogo = new UsuarioFormDialog(vista, true, roles);
        while (dialogo.pedir()) {
            char[] clave = dialogo.getClave();
            char[] confirmacion = dialogo.getConfirmacion();
            try {
                if (!Arrays.equals(clave, confirmacion)) {
                    dialogo.mostrarError("Las contraseñas no coinciden.");
                    continue;
                }
                Usuario datos = new Usuario();
                datos.setUsuario(dialogo.getUsuario());
                datos.setNombres(dialogo.getNombres());
                datos.setApellidos(dialogo.getApellidos());
                datos.setDocumento(dialogo.getDocumento());
                datos.setCorreo(dialogo.getCorreo());
                datos.setCargo(dialogo.getCargo());
                Rol rol = dialogo.getRol();
                datos.setIdRol(rol == null ? 0 : rol.getId());
                datos.setRolNombre(rol == null ? "" : rol.getNombre());

                String secreto = servicio.crear(datos, clave, SesionActual.getUsuario());
                cargar();
                vista.mostrarSecreto("Cuenta creada",
                        "La cuenta <b>" + datos.getUsuario() + "</b> fue creada.<br>"
                      + "Para el segundo factor, agregue esta clave en la aplicación autenticadora "
                      + "(opción \"ingresar clave manualmente\").<br><b>Se muestra una sola vez.</b>", secreto);
                break;
            } catch (ReglaSeguridadException ex) {
                dialogo.mostrarError(ex.getMessage());
            } catch (SQLException ex) {
                vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
                break;
            } finally {
                Arrays.fill(clave, '\0');
                Arrays.fill(confirmacion, '\0');
            }
        }
        dialogo.limpiarClaves();
        dialogo.dispose();
    }

    private void editar() {
        Usuario u = seleccionado();
        if (u == null) return;
        UsuarioFormDialog dialogo = new UsuarioFormDialog(vista, false, roles);
        dialogo.cargar(u);
        while (dialogo.pedir()) {
            try {
                Rol rol = dialogo.getRol();
                servicio.actualizarPerfil(u, dialogo.getCargo(), rol.getId(), rol.getNombre(),
                        SesionActual.getUsuario());
                cargar();
                break;
            } catch (ReglaSeguridadException ex) {
                dialogo.mostrarError(ex.getMessage());
            } catch (SQLException ex) {
                vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
                break;
            }
        }
        dialogo.dispose();
    }

    private void cambiarEstado() {
        Usuario u = seleccionado();
        if (u == null) return;
        try {
            if (Usuario.ACTIVO.equals(u.getEstado())) {
                if (!vista.confirmar("¿Desactivar la cuenta \"" + u.getUsuario() + "\"? No podrá iniciar sesión.")) return;
                servicio.desactivar(u, SesionActual.getUsuario());
            } else {
                servicio.activar(u, SesionActual.getUsuario());
            }
            cargar();
        } catch (ReglaSeguridadException ex) {
            vista.mostrarAdvertencia(ex.getMessage());
        } catch (SQLException ex) {
            vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
        }
    }

    private void desbloquear() {
        Usuario u = seleccionado();
        if (u == null) return;
        if (!Usuario.BLOQUEADO.equals(u.getEstado())) {
            vista.mostrarInfo("La cuenta \"" + u.getUsuario() + "\" no está bloqueada.");
            return;
        }
        try {
            servicio.desbloquear(u, SesionActual.getUsuario());
            cargar();
        } catch (SQLException ex) {
            vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
        }
    }

    private void restablecerClave() {
        Usuario u = seleccionado();
        if (u == null) return;
        char[] nueva = vista.pedirClaveNueva("Restablecer contraseña de " + u.getUsuario());
        if (nueva == null) return;
        try {
            servicio.restablecerClave(u, nueva, SesionActual.getUsuario());
            vista.mostrarInfo("La contraseña de \"" + u.getUsuario() + "\" fue restablecida.");
        } catch (ReglaSeguridadException ex) {
            vista.mostrarAdvertencia(ex.getMessage());
        } catch (SQLException ex) {
            vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
        } finally {
            Arrays.fill(nueva, '\0');
        }
    }

    private void restablecerSegundoFactor() {
        Usuario u = seleccionado();
        if (u == null) return;
        if (!vista.confirmar("¿Generar un segundo factor nuevo para \"" + u.getUsuario()
                + "\"? El anterior dejará de funcionar.")) return;
        try {
            String secreto = servicio.restablecerSegundoFactor(u, SesionActual.getUsuario());
            vista.mostrarSecreto("Segundo factor restablecido",
                    "Nueva clave para <b>" + u.getUsuario() + "</b>. Agréguela en la aplicación autenticadora.<br>"
                  + "<b>Se muestra una sola vez.</b>", secreto);
        } catch (SQLException ex) {
            vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
        }
    }
}
