package controlador.modulo_consulta;

import modelo.modulo_consulta.ReservaDAO;
import modelo.modulo_consulta.ReservaDTO;
import vista.modulo_consulta.ConfirmacionReservaFrame;
import vista.modulo_consulta.BuscarYSeleccionarVueloFrame;
import controlador.modulo_pago.ResumenCompraController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Controlador para la confirmación de reserva (RF-08).
 * Genera el PNR, interactúa con el DAO transaccional y actualiza la Vista 3.
 */
public class ConfirmacionReservaController implements ActionListener {

    private final ConfirmacionReservaFrame vista;
    private final ReservaDAO modeloDAO;
    private final ReservaDTO reservaFinal;
    
    private final DecimalFormat formatoMoneda;
    private final SimpleDateFormat formatoFecha;

    public ConfirmacionReservaController(ConfirmacionReservaFrame vista, ReservaDAO modeloDAO, ReservaDTO reservaFinal) {
        this.vista = vista;
        this.modeloDAO = modeloDAO;
        this.reservaFinal = reservaFinal;
        
        this.formatoMoneda = new DecimalFormat("$ #,##0.00");
        this.formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        configurarEventos();
        procesarYGuardarReserva();
    }

    private void configurarEventos() {
        vista.getBtnVolverInicio().addActionListener(this);
        vista.getBtnProcederPago().addActionListener(this);
    }

    /**
     * Método principal que ejecuta la lógica de negocio final.
     */
    private void procesarYGuardarReserva() {
        // 1. Generar Código PNR (Ej: A7X9P2)
        String pnrGenerado = generarCodigoPNR();
        reservaFinal.setCodigoReserva(pnrGenerado);

        // 2. Ejecutar transacción en la base de datos
        boolean guardadoExitoso = modeloDAO.registrarReservaTransaccional(reservaFinal);

        if (guardadoExitoso) {
            // 3. Si fue exitoso, calcular la fecha de expiración para la vista (24 horas)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 24);
            Date fechaExpiracion = cal.getTime();

            // 4. Mostrar datos en la interfaz
            vista.getLblCodigoReserva().setText("CÓDIGO PNR: " + pnrGenerado);
            vista.getLblFechaExpiracion().setText("Expiración (Límite de pago): " + formatoFecha.format(fechaExpiracion));
            vista.getLblDatosComprador().setText("Titular: " + reservaFinal.getComprador().getNombres() + " " + reservaFinal.getComprador().getApellidos());
            vista.getLblTotalPagar().setText("Total a Pagar: " + formatoMoneda.format(reservaFinal.getTotalPagar()));
            
            vista.setVisible(true);
        } else {
            // Error en base de datos (Rollback ejecutado)
            JOptionPane.showMessageDialog(null, 
                    "Ocurrió un error crítico al guardar la reserva en la base de datos.\nLa transacción ha sido cancelada para proteger la integridad de los datos.",
                    "Error de Transacción", JOptionPane.ERROR_MESSAGE);
            volverAlInicio();
        }
    }

    /**
     * Genera un código alfanumérico aleatorio de 6 caracteres en mayúsculas.
     */
    private String generarCodigoPNR() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder pnr = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 6; i++) {
            pnr.append(caracteres.charAt(rnd.nextInt(caracteres.length())));
        }
        return pnr.toString();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnVolverInicio()) {
            volverAlInicio();
        } else if (e.getSource() == vista.getBtnProcederPago()) {
            vista.dispose();
            ResumenCompraController.iniciar(reservaFinal.getCodigoReserva());
        }
    }

    private void volverAlInicio() {
        vista.dispose();
        // Reiniciar el ciclo abriendo la Vista 1
        BuscarYSeleccionarVueloFrame frameVuelos = new BuscarYSeleccionarVueloFrame();
        // Nota: Para compilar sin errores necesitas tener VueloDAO en este contexto o pasarlo si es necesario.
        // Asumiendo que VueloDAO tiene un constructor vacío para simplificar el reinicio:
        new BuscarYSeleccionarVueloController(frameVuelos, new modelo.modulo_consulta.VueloDAO());
    }
}