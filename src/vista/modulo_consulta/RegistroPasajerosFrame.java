package vista.modulo_consulta;

import modelo.modulo_consulta.Ciudad;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Vista 2: Formulario dinámico para el registro de datos personales (RF-06)
 * y definición del comprador de la reserva (RF-07).
 * Corregido: GridBagConstraints (gbc) aplicados correctamente a todos los JLabel.
 */
public class RegistroPasajerosFrame extends JFrame {

    // --- PANEL SUPERIOR: RESUMEN ---
    private JLabel lblResumenItinerario;

    // --- PANEL CENTRAL: CONTENEDOR DINÁMICO ---
    private JPanel panelContenedorPasajeros;

    // --- PANEL INFERIOR: COMPRADOR (RF-07) ---
    private JCheckBox chkCompradorEsPasajero;
    private JComboBox<String> cmbPasajeroComprador;
    private JTextField txtCompNombres;
    private JTextField txtCompApellidos;
    private JComboBox<String> cmbCompTipoDoc;
    private JTextField txtCompNumDoc;
    private JComboBox<String> cmbCompNacionalidad; 
    private JComboBox<Ciudad> cmbCompCiudadResidencia; 
    private JTextField txtCompTelefono;
    private JTextField txtCompCorreo;
    private JTextField txtCompDireccion;

    // --- BOTONES DE NAVEGACIÓN ---
    private JButton btnAtras;
    private JButton btnContinuar;

    public RegistroPasajerosFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Registro de Pasajeros y Comprador");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(950, 780); 
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 650));

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(panelPrincipal);

        // --- 1. ZONA SUPERIOR: RESUMEN DE VUELOS ---
        JPanel panelResumen = new JPanel(new BorderLayout());
        panelResumen.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), " Resumen de Selección ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), new Color(40, 160, 70)
        ));
        lblResumenItinerario = new JLabel("Cargando información del itinerario...");
        lblResumenItinerario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblResumenItinerario.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelResumen.add(lblResumenItinerario, BorderLayout.CENTER);
        panelPrincipal.add(panelResumen, BorderLayout.NORTH);

        // --- 2. ZONA CENTRAL: SCROLL PARA FORMULARIOS DE PASAJEROS ---
        panelContenedorPasajeros = new JPanel();
        panelContenedorPasajeros.setLayout(new BoxLayout(panelContenedorPasajeros, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPasajeros = new JScrollPane(panelContenedorPasajeros);
        scrollPasajeros.getVerticalScrollBar().setUnitIncrement(16);
        scrollPasajeros.setBorder(BorderFactory.createTitledBorder(" Datos de los Pasajeros (RF-06) "));
        panelPrincipal.add(scrollPasajeros, BorderLayout.CENTER);

        // --- 3. ZONA INFERIOR: COMPRADOR Y BOTONES ---
        JPanel panelInferior = new JPanel(new BorderLayout(0, 10));

        // 3.1 Formulario del Comprador (RF-07)
        JPanel panelComprador = new JPanel(new GridBagLayout());
        panelComprador.setBorder(BorderFactory.createTitledBorder(" Datos del Comprador (RF-07) "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 1: Checkbox y Selector
        chkCompradorEsPasajero = new JCheckBox("El comprador es uno de los pasajeros inscritos", true);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelComprador.add(chkCompradorEsPasajero, gbc);

        cmbPasajeroComprador = new JComboBox<>();
        gbc.gridx = 2; gbc.gridwidth = 2;
        panelComprador.add(cmbPasajeroComprador, gbc);

        // Fila 2: Nombres y Apellidos
        gbc.gridwidth = 1; gbc.gridy = 1;
        gbc.gridx = 0; panelComprador.add(new JLabel("Nombres:"), gbc); // <-- gbc agregado
        txtCompNombres = new JTextField(); txtCompNombres.setEnabled(false);
        gbc.gridx = 1; panelComprador.add(txtCompNombres, gbc);

        gbc.gridx = 2; panelComprador.add(new JLabel("Apellidos:"), gbc); // <-- gbc agregado
        txtCompApellidos = new JTextField(); txtCompApellidos.setEnabled(false);
        gbc.gridx = 3; panelComprador.add(txtCompApellidos, gbc);

        // Fila 3: Documento Normalizado
        gbc.gridy = 2;
        gbc.gridx = 0; panelComprador.add(new JLabel("Tipo Doc:"), gbc); // <-- gbc agregado
        cmbCompTipoDoc = new JComboBox<>(new String[]{"CC", "Pasaporte", "CE", "DNI"});
        cmbCompTipoDoc.setEnabled(false);
        gbc.gridx = 1; panelComprador.add(cmbCompTipoDoc, gbc);

        gbc.gridx = 2; panelComprador.add(new JLabel("N° Documento:"), gbc); // <-- gbc agregado
        txtCompNumDoc = new JTextField(); txtCompNumDoc.setEnabled(false);
        gbc.gridx = 3; panelComprador.add(txtCompNumDoc, gbc);

        // Fila 4: Nacionalidad y Ciudad
        gbc.gridy = 3;
        gbc.gridx = 0; panelComprador.add(new JLabel("Nacionalidad:"), gbc); // <-- gbc agregado
        cmbCompNacionalidad = new JComboBox<>(new String[]{"Colombiana", "Estadounidense", "Española", "Peruana", "Argentina", "Mexicana", "Otra"});
        cmbCompNacionalidad.setEnabled(false);
        gbc.gridx = 1; panelComprador.add(cmbCompNacionalidad, gbc);

        gbc.gridx = 2; panelComprador.add(new JLabel("Ciudad Residencia:"), gbc); // <-- gbc agregado
        cmbCompCiudadResidencia = new JComboBox<>();
        cmbCompCiudadResidencia.setEnabled(false);
        gbc.gridx = 3; panelComprador.add(cmbCompCiudadResidencia, gbc);

        // Fila 5: Contacto
        gbc.gridy = 4;
        gbc.gridx = 0; panelComprador.add(new JLabel("Teléfono:"), gbc); // <-- gbc agregado
        txtCompTelefono = new JTextField(); txtCompTelefono.setEnabled(false);
        gbc.gridx = 1; panelComprador.add(txtCompTelefono, gbc);

        gbc.gridx = 2; panelComprador.add(new JLabel("Correo:"), gbc); // <-- gbc agregado
        txtCompCorreo = new JTextField(); txtCompCorreo.setEnabled(false);
        gbc.gridx = 3; panelComprador.add(txtCompCorreo, gbc);

        // Fila 6: Dirección
        gbc.gridy = 5;
        gbc.gridx = 0; panelComprador.add(new JLabel("Dirección Residencia:"), gbc); // <-- gbc agregado
        txtCompDireccion = new JTextField(); txtCompDireccion.setEnabled(false);
        gbc.gridx = 1; gbc.gridwidth = 3;
        panelComprador.add(txtCompDireccion, gbc);

        panelInferior.add(panelComprador, BorderLayout.CENTER);

        // 3.2 Botones de Acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        btnAtras = new JButton("<< Volver a Selección");
        btnAtras.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        btnContinuar = new JButton("Confirmar Datos y Generar Reserva >>");
        btnContinuar.setBackground(new Color(25, 115, 210));
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panelBotones.add(btnAtras);
        panelBotones.add(btnContinuar);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
    }

    // --- GETTERS PARA EL CONTROLADOR ---
    public JLabel getLblResumenItinerario() { return lblResumenItinerario; }
    public JPanel getPanelContenedorPasajeros() { return panelContenedorPasajeros; }
    public JCheckBox getChkCompradorEsPasajero() { return chkCompradorEsPasajero; }
    public JComboBox<String> getCmbPasajeroComprador() { return cmbPasajeroComprador; }
    public JTextField getTxtCompNombres() { return txtCompNombres; }
    public JTextField getTxtCompApellidos() { return txtCompApellidos; }
    public JComboBox<String> getCmbCompTipoDoc() { return cmbCompTipoDoc; }
    public JTextField getTxtCompNumDoc() { return txtCompNumDoc; }
    public JComboBox<String> getCmbCompNacionalidad() { return cmbCompNacionalidad; }
    public JComboBox<Ciudad> getCmbCompCiudadResidencia() { return cmbCompCiudadResidencia; }
    public JTextField getTxtCompTelefono() { return txtCompTelefono; }
    public JTextField getTxtCompCorreo() { return txtCompCorreo; }
    public JTextField getTxtCompDireccion() { return txtCompDireccion; }
    public JButton getBtnAtras() { return btnAtras; }
    public JButton getBtnContinuar() { return btnContinuar; }

    /** Para que la X de la ventana ejecute la acción indicada (ej. volver al menú). */
    public void addCierreListener(Runnable accion) {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { accion.run(); }
        });
    }
}
