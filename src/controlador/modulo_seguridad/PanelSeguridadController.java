package controlador.modulo_seguridad;

import java.sql.SQLException;
import java.util.Arrays;
import modelo.modulo_seguridad.Modulo;
import modelo.modulo_seguridad.ReglaSeguridadException;
import modelo.modulo_seguridad.SesionActual;
import modelo.modulo_seguridad.Usuario;
import modelo.modulo_seguridad.UsuarioService;
import vista.modulo_seguridad.CambioClaveDialog;
import vista.modulo_seguridad.PanelSeguridadFrame;

/**
 * Panel del funcionario: muestra su sesión, habilita los accesos que su rol permite,
 * y permite cambiar la contraseña y cerrar la sesión.
 *
 * Desde el menú principal (después de GuardiaAcceso.autenticar) se abre con:
 *     PanelSeguridadController.iniciar(this::irAlMenu);
 */
public class PanelSeguridadController {

    private final PanelSeguridadFrame vista;
    private final Runnable alSalir; // vuelve al menú principal
    private final UsuarioService usuarioService = new UsuarioService();

    public PanelSeguridadController(PanelSeguridadFrame vista, Runnable alSalir) {
        this.vista = vista;
        this.alSalir = alSalir;

        Usuario u = SesionActual.getUsuario();
        vista.setSesion(u.getNombreCompleto(), u.getRolNombre());
        vista.setAccesos(SesionActual.tienePermiso(Modulo.USUARIOS),
                SesionActual.tienePermiso(Modulo.AUDITORIA) || SesionActual.tienePermiso(Modulo.INCIDENTES));

        vista.addUsuariosListener(e -> abrirUsuarios());
        vista.addAuditoriaListener(e -> abrirAuditoria());
        vista.addClaveListener(e -> cambiarClave());
        vista.addCerrarSesionListener(e -> cerrarSesion());
        vista.addVolverListener(e -> volver());
        vista.addCierreListener(this::volver);
    }

    public static PanelSeguridadController iniciar(Runnable alSalir) {
        PanelSeguridadFrame frame = new PanelSeguridadFrame();
        PanelSeguridadController controlador = new PanelSeguridadController(frame, alSalir);
        GuardiaAcceso.registrar(frame);
        frame.setVisible(true);
        return controlador;
    }

    private void abrirUsuarios() {
        if (GuardiaAcceso.autorizar(Modulo.USUARIOS, vista)) {
            GestionUsuariosController.iniciar();
        }
    }

    private void abrirAuditoria() {
        Modulo modulo = SesionActual.tienePermiso(Modulo.AUDITORIA) ? Modulo.AUDITORIA : Modulo.INCIDENTES;
        if (GuardiaAcceso.autorizar(modulo, vista)) {
            AuditoriaController.iniciar(SesionActual.tienePermiso(Modulo.AUDITORIA),
                    SesionActual.tienePermiso(Modulo.INCIDENTES));
        }
    }

    private void cambiarClave() {
        CambioClaveDialog dialogo = new CambioClaveDialog(vista);
        while (dialogo.pedir()) {
            char[] actual = dialogo.getActual();
            char[] nueva = dialogo.getNueva();
            char[] confirmacion = dialogo.getConfirmacion();
            try {
                if (!Arrays.equals(nueva, confirmacion)) {
                    dialogo.mostrarError("La confirmación no coincide con la contraseña nueva.");
                    continue;
                }
                usuarioService.cambiarClavePropia(SesionActual.getUsuario(), actual, nueva);
                vista.mostrarInfo("Su contraseña fue actualizada.");
                break;
            } catch (ReglaSeguridadException ex) {
                dialogo.mostrarError(ex.getMessage());
            } catch (SQLException ex) {
                vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
                break;
            } finally {
                Arrays.fill(actual, '\0');
                Arrays.fill(nueva, '\0');
                Arrays.fill(confirmacion, '\0');
            }
        }
        dialogo.limpiar();
        dialogo.dispose();
    }

    private void cerrarSesion() {
        if (!vista.confirmar("¿Desea cerrar su sesión? Se cerrarán las ventanas internas abiertas.")) {
            return;
        }
        GuardiaAcceso.cerrarSesion(); // cierra también este panel
        alSalir.run();
    }

    private void volver() {
        vista.dispose();
        alSalir.run();
    }
}
