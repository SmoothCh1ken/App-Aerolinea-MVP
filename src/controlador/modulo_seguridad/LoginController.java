package controlador.modulo_seguridad;

import java.sql.SQLException;
import java.util.Arrays;
import modelo.modulo_seguridad.AutenticacionService;
import modelo.modulo_seguridad.AutenticacionService.Resultado;
import modelo.modulo_seguridad.Usuario;
import vista.modulo_seguridad.LoginDialog;

/** Conduce el ingreso en dos pasos: contraseña y segundo factor (RF-28). */
public class LoginController {

    private final LoginDialog vista;
    private final AutenticacionService servicio = new AutenticacionService();
    private Usuario pendiente; // usuario que ya validó el primer factor

    public LoginController(LoginDialog vista) {
        this.vista = vista;
        vista.addIngresarListener(e -> primerFactor());
        vista.addVerificarListener(e -> segundoFactor());
        vista.addVolverListener(e -> volverAlPrimerPaso());
        vista.addCancelarListener(e -> cancelar());
        vista.addCierreListener(this::cancelar);
    }

    private void primerFactor() {
        String usuario = vista.getUsuario();
        char[] clave = vista.getClave();
        try {
            if (usuario.isEmpty() || clave.length == 0) {
                vista.mostrarError("Ingrese su usuario y su contraseña.");
                return;
            }
            Resultado r = servicio.primerFactor(usuario, clave);
            vista.limpiarClave();
            if (r.isOk()) {
                pendiente = r.getUsuario();
                vista.limpiarMensaje();
                vista.mostrarPasoMfa(pendiente.getNombres());
            } else {
                vista.mostrarError(r.getMensaje());
            }
        } catch (SQLException ex) {
            vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
        } finally {
            Arrays.fill(clave, '\0'); // no dejar la contraseña en memoria
        }
    }

    private void segundoFactor() {
        if (pendiente == null) {
            volverAlPrimerPaso();
            return;
        }
        try {
            Resultado r = servicio.segundoFactor(pendiente, vista.getCodigo());
            if (r.isOk()) {
                servicio.completarSesion(pendiente);
                vista.dispose();
            } else {
                vista.mostrarError(r.getMensaje());
                if (r.isCuentaBloqueada()) {
                    pendiente = null;
                    vista.mostrarPasoUsuario();
                }
            }
        } catch (SQLException ex) {
            vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
        }
    }

    private void volverAlPrimerPaso() {
        pendiente = null;
        vista.limpiarMensaje();
        vista.mostrarPasoUsuario();
    }

    private void cancelar() {
        pendiente = null;
        vista.dispose();
    }
}
