package controlador.modulo_seguridad;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import modelo.modulo_seguridad.AuditoriaDAO;
import modelo.modulo_seguridad.AutenticacionService;
import modelo.modulo_seguridad.EventoSeguridad;
import modelo.modulo_seguridad.Modulo;
import modelo.modulo_seguridad.SesionActual;
import modelo.modulo_seguridad.Usuario;
import vista.modulo_seguridad.LoginDialog;

/**
 * Punto de entrada del módulo de seguridad para el resto de la aplicación.
 *
 * En el menú principal:
 *     GuardiaAcceso.instalar(this::irAlMenu);                    // una vez, al crear el menú
 *     if (!GuardiaAcceso.autorizar(Modulo.OPERACIONES, vista)) return;   // antes de abrir un módulo interno
 *     GuardiaAcceso.registrar(frameInterno);                     // para cerrarlo si la sesión expira
 */
public final class GuardiaAcceso {

    private static final AutenticacionService AUTENTICACION = new AutenticacionService();
    private static final AuditoriaDAO AUDITORIA = new AuditoriaDAO();
    private static final MonitorInactividad MONITOR = new MonitorInactividad();
    private static final List<Window> VENTANAS = new ArrayList<>();
    private static Runnable volverAlMenu;

    private GuardiaAcceso() { }

    /** @param alVolverAlMenu lo que se ejecuta cuando la sesión expira (debe mostrar el menú principal) */
    public static void instalar(Runnable alVolverAlMenu) {
        volverAlMenu = alVolverAlMenu;
        MONITOR.instalar();
    }

    /**
     * RF-28: pide usuario, contraseña y segundo factor si no hay una sesión abierta.
     * @return true si hay una sesión válida
     */
    public static boolean autenticar(Component padre) {
        if (SesionActual.activa()) {
            return true;
        }
        Window propietario = padre instanceof Window ? (Window) padre
                : (padre == null ? null : SwingUtilities.getWindowAncestor(padre));
        LoginDialog dialogo = new LoginDialog(propietario);
        new LoginController(dialogo);
        dialogo.setVisible(true); // es modal: espera aquí hasta que termine el ingreso
        if (SesionActual.activa()) {
            MONITOR.iniciar();
            return true;
        }
        return false;
    }

    /**
     * RF-27: además de autenticar, verifica que el rol del funcionario tenga acceso al módulo.
     * Toda decisión (permitida o denegada) queda en la auditoría.
     */
    public static boolean autorizar(Modulo modulo, Component padre) {
        if (!autenticar(padre)) {
            return false;
        }
        Usuario u = SesionActual.getUsuario();
        boolean permitido = SesionActual.tienePermiso(modulo);
        try {
            AUDITORIA.registrar(u.getIdUsuario(), u.getUsuario(),
                    permitido ? EventoSeguridad.ACCESO_MODULO : EventoSeguridad.ACCESO_DENEGADO,
                    permitido ? EventoSeguridad.EXITOSO : EventoSeguridad.FALLIDO,
                    (permitido ? "Acceso a " : "Acceso denegado a ") + modulo.getTitulo()
                            + " (rol " + u.getRolNombre() + ")");
        } catch (SQLException ex) {
            // si no se puede dejar el registro de auditoría, no se concede el acceso
            JOptionPane.showMessageDialog(padre, "No se pudo registrar la auditoría: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!permitido) {
            JOptionPane.showMessageDialog(padre, "Su rol (" + u.getRolNombre() + ") no tiene acceso a: "
                    + modulo.getTitulo() + ".", "Acceso denegado", JOptionPane.WARNING_MESSAGE);
        }
        return permitido;
    }

    /** Anota una ventana interna para cerrarla automáticamente si la sesión termina. */
    public static void registrar(Window ventana) {
        synchronized (VENTANAS) {
            VENTANAS.add(ventana);
        }
        ventana.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                synchronized (VENTANAS) {
                    VENTANAS.remove(ventana);
                }
            }
        });
    }

    /** Cierre voluntario de la sesión: cierra las ventanas internas y deja el registro. */
    public static void cerrarSesion() {
        try {
            AUTENTICACION.cerrarSesion(false);
        } catch (SQLException ex) {
            System.err.println("No se pudo registrar el cierre de sesión: " + ex.getMessage());
        }
        MONITOR.detener();
        cerrarVentanas();
    }

    /** RF-30: cierre automático por inactividad. */
    static void expirarPorInactividad() {
        try {
            AUTENTICACION.cerrarSesion(true);
        } catch (SQLException ex) {
            System.err.println("No se pudo registrar la expiración de la sesión: " + ex.getMessage());
        }
        MONITOR.detener();
        cerrarVentanas();
        if (volverAlMenu != null) {
            volverAlMenu.run();
        }
        JOptionPane.showMessageDialog(null, "Su sesión se cerró por inactividad. Vuelva a iniciar sesión para continuar.",
                "Sesión finalizada", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void cerrarVentanas() {
        List<Window> copia;
        synchronized (VENTANAS) {
            copia = new ArrayList<>(VENTANAS);
            VENTANAS.clear();
        }
        for (Window w : copia) {
            w.dispose();
        }
    }
}
