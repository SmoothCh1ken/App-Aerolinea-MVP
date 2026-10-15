package controlador.modulo_pago;

import java.sql.SQLException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import controlador.menu.Navegador;
import modelo.modulo_pago.PagoDAO;
import modelo.modulo_pago.ResumenCompra;
import modelo.modulo_pago.ResumenCompraDAO;
import vista.modulo_pago.ResumenCompraFrame;
import vista.modulo_pago.SeleccionMedioPagoFrame;

/**
 * Punto de entrada del módulo de pago.
 *
 * Desde otro módulo (p. ej. ConfirmacionReservaFrame) se abre con:
 *     ResumenCompraController.iniciar(codigoReserva);
 */
public class ResumenCompraController {

    private final ResumenCompraFrame vista;
    private final ResumenCompraDAO resumenDAO = new ResumenCompraDAO();
    private final PagoDAO pagoDAO = new PagoDAO();
    private final Navegador navegador; // null = ejecución independiente del módulo
    private ResumenCompra resumenActual;

    public ResumenCompraController(ResumenCompraFrame vista, Navegador navegador) {
        this.vista = vista;
        this.navegador = navegador;
        vista.addCerrarListener(e -> salir());
        vista.addCierreListener(this::salir);
        vista.addBuscarListener(e -> buscar(false)); // Búsqueda manual muestra la alerta
        vista.addContinuarListener(e -> continuarAlPago());
    }

    /** Crea la ventana y su controlador; si llega un código, lo consulta de una vez. */
    public static void iniciar(String codigoReserva, Navegador navegador) {
        ResumenCompraFrame frame = new ResumenCompraFrame();
        ResumenCompraController controlador = new ResumenCompraController(frame, navegador);
        frame.setVisible(true);
        if (codigoReserva != null && !codigoReserva.trim().isEmpty()) {
            frame.setCodigoIngresado(codigoReserva);
            controlador.buscar(false);
        }
    }

    /** Solo para probar el módulo de forma independiente (Run File). */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorada) { }
            iniciar(args.length > 0 ? args[0] : null, null);
        });
    }

    /** Cierra el módulo y regresa al menú principal (si fue lanzado desde él). */
    private void salir() {
        vista.dispose();
        if (navegador != null) {
            navegador.irAlMenu();
        }
    }

    private void buscar(boolean retornoSilencioso) {
        String codigo = vista.getCodigoIngresado();
        if (codigo.isEmpty()) {
            vista.mostrarError("Ingrese el código de la reserva.");
            return;
        }
        try {
            pagoDAO.liberarReservasExpiradas(); // CA-24: libera cupos vencidos
            resumenActual = resumenDAO.buscarPorCodigo(codigo);
            if (resumenActual == null) {
                vista.limpiar();
                vista.mostrarError("No existe una reserva con el código " + codigo + ".");
                return;
            }
            vista.mostrarResumen(resumenActual);
            
            // Lógica trasladada desde la Vista para respetar el patrón MVC
            if (!retornoSilencioso && !resumenActual.isPagable()) {
                vista.mostrarMensaje("La reserva está \"" + resumenActual.getEstadoReserva() + "\" y no admite pago.");
            }
            
        } catch (SQLException ex) {
            vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
        }
    }
    private void continuarAlPago() {
        if (resumenActual == null || !resumenActual.isPagable()) {
            vista.mostrarError("La reserva no está disponible para pago.");
            return;
        }
        SeleccionMedioPagoFrame seleccion = new SeleccionMedioPagoFrame();
        new SeleccionMedioPagoController(seleccion, resumenActual, this::regresarDelPago);
        vista.setVisible(false);
        seleccion.setVisible(true);
    }

    /** Se ejecuta al terminar o abandonar el pago: refresca el resumen y lo vuelve a mostrar. */
    /** Se ejecuta al terminar o abandonar el pago: refresca el resumen y lo vuelve a mostrar. */
    private void regresarDelPago() {
        buscar(true); // Retorno silencioso: NO dispara el pop-up
        vista.setVisible(true);
    }
}
