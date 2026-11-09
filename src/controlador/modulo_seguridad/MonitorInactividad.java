package controlador.modulo_seguridad;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import javax.swing.Timer;
import modelo.modulo_seguridad.SesionActual;

/**
 * Vigila la inactividad de la sesión (RF-30). Cualquier movimiento del ratón o tecla pulsada
 * en cualquier ventana de la aplicación cuenta como actividad. Cada pocos segundos se compara
 * el tiempo sin actividad con el límite de la política.
 */
final class MonitorInactividad {

    private static final int REVISION_MS = 5_000;

    private boolean instalado;
    private Timer temporizador;

    void instalar() {
        if (instalado) {
            return;
        }
        Toolkit.getDefaultToolkit().addAWTEventListener(evento -> SesionActual.registrarActividad(),
                AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK
              | AWTEvent.MOUSE_WHEEL_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
        temporizador = new Timer(REVISION_MS, e -> revisar());
        instalado = true;
    }

    void iniciar() {
        if (temporizador != null) {
            temporizador.start();
        }
    }

    void detener() {
        if (temporizador != null) {
            temporizador.stop();
        }
    }

    private void revisar() {
        if (!SesionActual.activa()) {
            temporizador.stop();
            return;
        }
        if (SesionActual.milisegundosInactivo() >= SesionActual.getLimiteInactividadMs()) {
            GuardiaAcceso.expirarPorInactividad();
        }
    }
}
