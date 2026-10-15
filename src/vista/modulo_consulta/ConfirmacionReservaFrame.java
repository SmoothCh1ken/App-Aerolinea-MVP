package vista.modulo_consulta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Vista 3: Pantalla de éxito tras confirmar la reserva (RF-08).
 * Muestra el código de reserva (PNR) y los detalles del pago pendiente.
 */
public class ConfirmacionReservaFrame extends JFrame {

    // --- PANELES Y ETIQUETAS PRINCIPALES ---
    private JLabel lblMensajeExito;
    private JLabel lblCodigoReserva;
    
    private JLabel lblEstadoReserva;
    private JLabel lblFechaExpiracion;
    
    private JLabel lblDatosComprador;
    private JLabel lblTotalPagar;

    // --- BOTONES ---
    private JButton btnVolverInicio;
    private JButton btnProcederPago;

    public ConfirmacionReservaFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Confirmación de Reserva");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(new EmptyBorder(20, 30, 20, 30));
        setContentPane(panelPrincipal);

        // --- 1. ZONA NORTE: MENSAJE DE ÉXITO Y CÓDIGO ---
        JPanel panelExito = new JPanel(new BorderLayout(0, 10));
        panelExito.setBackground(new Color(240, 255, 240)); // Fondo verde muy claro
        panelExito.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 160, 70), 2),
                new EmptyBorder(15, 15, 15, 15)
        ));

        lblMensajeExito = new JLabel("¡Reserva generada con éxito!", SwingConstants.CENTER);
        lblMensajeExito.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMensajeExito.setForeground(new Color(40, 160, 70));
        panelExito.add(lblMensajeExito, BorderLayout.NORTH);

        lblCodigoReserva = new JLabel("CÓDIGO PNR: ------", SwingConstants.CENTER);
        lblCodigoReserva.setFont(new Font("Monospaced", Font.BOLD, 36));
        lblCodigoReserva.setForeground(Color.BLACK);
        panelExito.add(lblCodigoReserva, BorderLayout.CENTER);

        panelPrincipal.add(panelExito, BorderLayout.NORTH);

        // --- 2. ZONA CENTRAL: DETALLES DE LA RESERVA ---
        JPanel panelDetalles = new JPanel(new GridLayout(2, 1, 10, 10));

        // 2.1 Subpanel de Fechas y Estado
        JPanel panelEstado = new JPanel(new GridLayout(2, 1, 5, 5));
        panelEstado.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), " Estado Actual ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), Color.DARK_GRAY
        ));
        
        lblEstadoReserva = new JLabel("Estado: Pendiente de Pago");
        lblEstadoReserva.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEstadoReserva.setForeground(new Color(200, 100, 0)); // Color Naranja
        
        lblFechaExpiracion = new JLabel("Expiración (Límite de pago): ---");
        lblFechaExpiracion.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panelEstado.add(lblEstadoReserva);
        panelEstado.add(lblFechaExpiracion);
        panelDetalles.add(panelEstado);

        // 2.2 Subpanel del Comprador
        JPanel panelComprador = new JPanel(new GridLayout(2, 1, 5, 5));
        panelComprador.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), " Datos de Facturación ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), Color.DARK_GRAY
        ));

        lblDatosComprador = new JLabel("Titular: ---");
        lblDatosComprador.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        lblTotalPagar = new JLabel("Total a Pagar: $ 0.00");
        lblTotalPagar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalPagar.setForeground(new Color(0, 102, 204)); // Azul oscuro

        panelComprador.add(lblDatosComprador);
        panelComprador.add(lblTotalPagar);
        panelDetalles.add(panelComprador);

        panelPrincipal.add(panelDetalles, BorderLayout.CENTER);

        // --- 3. ZONA SUR: BOTONES DE ACCIÓN ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        btnVolverInicio = new JButton("Finalizar y Volver al Inicio");
        btnVolverInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnProcederPago = new JButton("Proceder al Pago >>");
        btnProcederPago.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProcederPago.setBackground(new Color(25, 115, 210));
        btnProcederPago.setForeground(Color.WHITE);

        panelBotones.add(btnVolverInicio);
        panelBotones.add(btnProcederPago);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
    }

    // --- GETTERS PARA EL CONTROLADOR ---
    public JLabel getLblCodigoReserva() { return lblCodigoReserva; }
    public JLabel getLblEstadoReserva() { return lblEstadoReserva; }
    public JLabel getLblFechaExpiracion() { return lblFechaExpiracion; }
    public JLabel getLblDatosComprador() { return lblDatosComprador; }
    public JLabel getLblTotalPagar() { return lblTotalPagar; }
    public JButton getBtnVolverInicio() { return btnVolverInicio; }
    public JButton getBtnProcederPago() { return btnProcederPago; }

    /** Para que la X de la ventana ejecute la acción indicada (ej. volver al menú). */
    public void addCierreListener(Runnable accion) {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { accion.run(); }
        });
    }
}
