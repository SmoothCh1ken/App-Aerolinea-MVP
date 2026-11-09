package vista.menu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Menú Principal rediseñado con interfaz moderna estilo Web.
 * Incluye barra superior con botón de estado de sesión (Login / Perfil)
 * y separación de accesos públicos y de administración.
 */
public class MenuPrincipalFrame extends JFrame {

    // Botón superior de sesión / perfil
    private final JButton btnUsuario = new JButton("Login");

    // Botones públicos (All Users)
    private final JButton btnReservar = new JButton("Reservar Nuevo Vuelo");
    private final JButton btnPagar = new JButton("Pagar Reserva Existente");
    private final JButton btnGestionar = new JButton("Gestionar mi Reserva");

    // Panel y botones de administración / módulos internos (Admin)
    private final JPanel pnlAdmin = new JPanel(new GridLayout(3, 1, 8, 8));
    private final JLabel lblTituloAdmin = new JLabel("Módulos Internos", SwingConstants.LEFT);
    private final JButton btnOperaciones = new JButton("Operaciones de Aerolínea");
    private final JButton btnReportes = new JButton("Reportes Gerenciales");
    private final JButton btnSeguridad = new JButton("Seguridad y Usuarios");

    public MenuPrincipalFrame() {
        super("Aerolínea MVP - Menú Principal");
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480, 580);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- 1. BARRA SUPERIOR (HEADER) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(25, 115, 210)); // Azul corporativo
        pnlHeader.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lblLogo = new JLabel("AirColombia");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(Color.WHITE);

        // Estilar botón de usuario/login en la esquina superior derecha
        btnUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUsuario.setFocusPainted(false);
        btnUsuario.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUsuario.setBackground(Color.WHITE);
        btnUsuario.setForeground(new Color(25, 115, 210));

        pnlHeader.add(lblLogo, BorderLayout.WEST);
        pnlHeader.add(btnUsuario, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. PANEL CENTRAL DE OPCIONES ---
        JPanel pnlCentral = new JPanel();
        pnlCentral.setLayout(new BoxLayout(pnlCentral, BoxLayout.Y_AXIS));
        pnlCentral.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Subsección 1: Accesos Públicos
        JLabel lblTituloPublico = new JLabel("Servicios al Pasajero");
        lblTituloPublico.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloPublico.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCentral.add(lblTituloPublico);
        pnlCentral.add(Box.createVerticalStrut(10));

        JPanel pnlPublico = new JPanel(new GridLayout(3, 1, 8, 8));
        pnlPublico.setAlignmentX(Component.LEFT_ALIGNMENT);

        estilarBotonPublico(btnReservar);
        estilarBotonPublico(btnPagar);
        estilarBotonPublico(btnGestionar);

        pnlPublico.add(btnReservar);
        pnlPublico.add(btnPagar);
        pnlPublico.add(btnGestionar);

        pnlCentral.add(pnlPublico);
        pnlCentral.add(Box.createVerticalStrut(25));

        // Subsección 2: Módulos Internos (Admin)
        lblTituloAdmin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloAdmin.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCentral.add(lblTituloAdmin);
        pnlCentral.add(Box.createVerticalStrut(10));

        pnlAdmin.setAlignmentX(Component.LEFT_ALIGNMENT);

        estilarBotonAdmin(btnOperaciones);
        estilarBotonAdmin(btnReportes);
        estilarBotonAdmin(btnSeguridad);

        pnlAdmin.add(btnOperaciones);
        pnlAdmin.add(btnReportes);
        pnlAdmin.add(btnSeguridad);

        pnlCentral.add(pnlAdmin);

        add(pnlCentral, BorderLayout.CENTER);

        // Por defecto, iniciamos en estado no autenticado
        setEstadoSesion(false, null);
    }

    private void estilarBotonPublico(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 40));
    }

    private void estilarBotonAdmin(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(180, 40, 40));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 40));
    }

    // ---------- MÉTODOS DE ESTADO Y CONTROL DE INTERFAZ ----------

    /**
     * Actualiza la visibilidad de los componentes según el estado de la sesión.
     * @param logueado true si hay un funcionario autenticado.
     * @param usuarioNombre Nombre del usuario para mostrar en el botón (o null si no hay sesión).
     */
    public void setEstadoSesion(boolean logueado, String usuarioNombre) {
        if (logueado && usuarioNombre != null && !usuarioNombre.trim().isEmpty()) {
            btnUsuario.setText("👤 " + usuarioNombre);
            lblTituloAdmin.setVisible(true);
            pnlAdmin.setVisible(true);
        } else {
            btnUsuario.setText("👤 Login");
            lblTituloAdmin.setVisible(false);
            pnlAdmin.setVisible(false);
        }
        revalidate();
        repaint();
    }

    /** Permite obtener la posición del botón de usuario para anclar el Popup */
    public JButton getBtnUsuario() {
        return btnUsuario;
    }

    // ---------- LISTENERS PARA EL CONTROLADOR ----------

    public void addUsuarioListener(ActionListener l) { btnUsuario.addActionListener(l); }
    public void addReservarListener(ActionListener l) { btnReservar.addActionListener(l); }
    public void addPagarListener(ActionListener l) { btnPagar.addActionListener(l); }
    public void addGestionarListener(ActionListener l) { btnGestionar.addActionListener(l); }
    public void addOperacionesListener(ActionListener l) { btnOperaciones.addActionListener(l); }
    public void addReportesListener(ActionListener l) { btnReportes.addActionListener(l); }
    public void addSeguridadListener(ActionListener l) { btnSeguridad.addActionListener(l); }
}