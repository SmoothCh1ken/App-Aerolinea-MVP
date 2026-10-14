package controlador.modulo_pago;

import java.sql.SQLException;
import java.util.List;
import modelo.modulo_pago.MedioPago;
import modelo.modulo_pago.MedioPagoDAO;
import modelo.modulo_pago.ProcesadorPago;
import modelo.modulo_pago.ResultadoPago;
import modelo.modulo_pago.ResumenCompra;
import modelo.modulo_pago.SolicitudPago;
import vista.modulo_pago.FormatoUtil;
import vista.modulo_pago.ResultadoPagoFrame;
import vista.modulo_pago.SeleccionMedioPagoFrame;

/** Controla la selección del medio de pago, la validación y el procesamiento. */
public class SeleccionMedioPagoController {

    private final SeleccionMedioPagoFrame vista;
    private final ResumenCompra resumen;
    private final Runnable alVolver;
    private final MedioPagoDAO medioDAO = new MedioPagoDAO();
    private final ProcesadorPago procesador = new ProcesadorPago();

    public SeleccionMedioPagoController(SeleccionMedioPagoFrame vista, ResumenCompra resumen, Runnable alVolver) {
        this.vista = vista;
        this.resumen = resumen;
        this.alVolver = alVolver;

        vista.setResumen(resumen);
        cargarMedios();

        vista.addMedioListener(e -> alCambiarMedio());
        vista.addPagarListener(e -> pagar());
        vista.addVolverListener(e -> volver());
        vista.addCierreListener(this::volver);
        alCambiarMedio();
    }

    private void cargarMedios() {
        try {
            List<MedioPago> medios = medioDAO.listarActivos();
            vista.cargarMedios(medios);
        } catch (SQLException ex) {
            vista.mostrarError("No se pudieron cargar los medios de pago: " + ex.getMessage());
        }
    }

    private void alCambiarMedio() {
        MedioPago medio = vista.getMedioSeleccionado();
        if (medio == null) return;
        vista.getFormPanel().mostrarMedio(medio.getTipo());
        int millas = ProcesadorPago.millasNecesarias(resumen.getTotal());
        vista.getFormPanel().setMillasNecesarias(millas,
                "1 milla = " + FormatoUtil.moneda(ProcesadorPago.VALOR_MILLA_COP));
    }

    private void pagar() {
        MedioPago medio = vista.getMedioSeleccionado();
        if (medio == null) {
            vista.mostrarAdvertencia("Seleccione un medio de pago.");
            return;
        }
        String error = vista.getFormPanel().validar(medio.getTipo());
        if (error != null) {
            vista.mostrarAdvertencia(error);
            return;
        }
        if (!vista.confirmar("¿Confirma el pago de " + FormatoUtil.moneda(resumen.getTotal())
                + " con " + medio.getNombre() + "?")) {
            return;
        }

        SolicitudPago solicitud = new SolicitudPago(
                medio,
                vista.getFormPanel().getReferenciaCliente(medio.getTipo()),
                vista.getFormPanel().getCodigoBono(),
                vista.getFormPanel().getResultadoSimulado());

        vista.setProcesando(true);
        ResultadoPago resultado = procesador.procesar(resumen, solicitud);
        vista.setProcesando(false);
        vista.getFormPanel().limpiar(); // no dejar datos de tarjeta en pantalla
        mostrarResultado(resultado);
    }

    private void mostrarResultado(ResultadoPago resultado) {
        ResultadoPagoFrame frameResultado = new ResultadoPagoFrame();
        new ResultadoPagoController(frameResultado, resultado,
                () -> { // Reintentar: volver a esta pantalla
                    frameResultado.dispose();
                    vista.setVisible(true);
                },
                () -> { // Finalizar: cerrar todo y refrescar el resumen
                    frameResultado.dispose();
                    vista.dispose();
                    alVolver.run();
                });
        vista.setVisible(false);
        frameResultado.setVisible(true);
    }

    private void volver() {
        vista.dispose();
        alVolver.run();
    }
}
