package vista.menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MenuPrincipalFrame extends JFrame {
    
    private final JButton btnReservar = new JButton("Reservar Nuevo Vuelo");
    private final JButton btnPagar = new JButton("Pagar Reserva Existente");
    private final JButton btnGestionar = new JButton("Gestionar mi Reserva");
    private final JButton btnOperaciones = new JButton("Operaciones de Aerolínea (Admin)");
    private final JButton btnReportes = new JButton("Reportes Gerenciales (Admin)");
    private final JButton btnSeguridad = new JButton("Seguridad y Usuarios (Admin)");

    public MenuPrincipalFrame() {
        super("Aerolínea MVP - Menú Principal");
        
        // ¡Esta es la única ventana que debe matar el programa al cerrarse!
        setDefaultCloseOperation(EXIT_ON_CLOSE); 
        setSize(400, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Diseño de la interfaz
        setLayout(new BorderLayout(10, 10));
        JPanel pnlCentro = new JPanel(new GridLayout(6, 1, 10, 10));
        pnlCentro.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel lblTitulo = new JLabel("Bienvenido a Aerolínea MVP", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(lblTitulo, BorderLayout.NORTH);

        pnlCentro.add(btnReservar);
        pnlCentro.add(btnPagar);
        pnlCentro.add(btnGestionar);
        pnlCentro.add(btnOperaciones);
        pnlCentro.add(btnReportes);
        pnlCentro.add(btnSeguridad);

        add(pnlCentro, BorderLayout.CENTER);
        
        // Botón de operaciones visualmente diferente
        btnOperaciones.setBackground(new Color(200, 50, 50));
        btnOperaciones.setForeground(Color.WHITE);
        btnOperaciones.setFocusPainted(false);
        
        btnReportes.setBackground(new Color(200, 50, 50));
        btnReportes.setForeground(Color.WHITE);
        btnReportes.setFocusPainted(false);
        
        btnSeguridad.setBackground(new Color(200, 50, 50));
        btnSeguridad.setForeground(Color.WHITE);
        btnSeguridad.setFocusPainted(false);
    }

    // Métodos para que el controlador escuche los clics
    public void addReservarListener(ActionListener l) { btnReservar.addActionListener(l); }
    public void addPagarListener(ActionListener l) { btnPagar.addActionListener(l); }
    public void addGestionarListener(ActionListener l) { btnGestionar.addActionListener(l); }
    public void addOperacionesListener(ActionListener l) { btnOperaciones.addActionListener(l); }
    public void addReportesListener(ActionListener l) { btnReportes.addActionListener(l); }
    public void addSeguridadListener(ActionListener l) { btnSeguridad.addActionListener(l); }
}