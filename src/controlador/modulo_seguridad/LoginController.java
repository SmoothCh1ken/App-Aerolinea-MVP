package controlador.modulo_seguridad;

import java.sql.SQLException;
import java.util.Arrays;
import modelo.modulo_seguridad.AutenticacionService;
import modelo.modulo_seguridad.AutenticacionService.Resultado;
import modelo.modulo_seguridad.Usuario;
import vista.modulo_seguridad.LoginDialog;

/** 
 * Conduce el ingreso en tres pasos (RF-28): 
 * 1. Contraseña
 * 2. Elección de método MFA (App o Correo)
 * 3. Validación del código de 6 dígitos.
 */
public class LoginController {

    private final LoginDialog vista;
    private final AutenticacionService servicio = new AutenticacionService();
    
    private Usuario pendiente; // usuario que ya validó el primer factor
    private boolean eligioCorreo; // indica qué método eligió el usuario

    public LoginController(LoginDialog vista) {
        this.vista = vista;
        
        // --- Eventos expuestos desde la vista ---
        vista.addIngresarListener(e -> primerFactor());
        vista.addElegirAppListener(e -> manejarSeleccionApp());
        vista.addElegirCorreoListener(e -> manejarSeleccionCorreo());
        vista.addVerificarListener(e -> segundoFactor());
        vista.addVolverListener(e -> volverAlPasoSeleccion());
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
                // En vez de pedir el código, mostramos la pantalla de elección
                vista.mostrarPasoSeleccionMfa(pendiente.getNombres());
            } else {
                vista.mostrarError(r.getMensaje());
            }
        } catch (SQLException ex) {
            vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
        } finally {
            Arrays.fill(clave, '\0'); // no dejar la contraseña en memoria
        }
    }

    private void manejarSeleccionApp() {
        eligioCorreo = false;
        vista.limpiarMensaje();
        vista.mostrarPasoMfaCodigo(false); // false = es por App
    }

    private void manejarSeleccionCorreo() {
        eligioCorreo = true;
        vista.limpiarMensaje();
        
        // Pide al servicio que genere y envíe el código
        Resultado r = servicio.generarYEnviarCodigoCorreo(pendiente);
        
        if (r.isOk()) {
            vista.mostrarInfo("Código enviado exitosamente a su correo.");
            vista.mostrarPasoMfaCodigo(true); // true = es por correo
        } else {
            vista.mostrarError(r.getMensaje());
        }
    }

    private void segundoFactor() {
        if (pendiente == null) {
            volverAlPasoSeleccion();
            return;
        }
        
        String codigoIngresado = vista.getCodigo();
        if (codigoIngresado.isEmpty()) {
            vista.mostrarError("Debe ingresar el código de 6 dígitos.");
            return;
        }

        try {
            Resultado r;
            // Evaluamos qué método eligió el usuario para invocar el validador correcto
            if (eligioCorreo) {
                r = servicio.segundoFactorCorreo(pendiente, codigoIngresado);
            } else {
                r = servicio.segundoFactorTotp(pendiente, codigoIngresado);
            }

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

    private void volverAlPasoSeleccion() {
        if (pendiente == null) {
            vista.limpiarMensaje();
            vista.mostrarPasoUsuario();
        } else {
            vista.limpiarMensaje();
            vista.mostrarPasoSeleccionMfa(pendiente.getNombres());
        }
    }

    private void cancelar() {
        pendiente = null;
        vista.dispose();
    }
}