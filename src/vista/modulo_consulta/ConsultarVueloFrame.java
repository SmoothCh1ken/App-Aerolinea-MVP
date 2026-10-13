package vista.modulo_consulta;

import modelo.modulo_consulta.Ciudad;
import com.toedter.calendar.JDateChooser; // <-- Nueva importación de JCalendar

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ConsultarVueloFrame extends JFrame {

    private JRadioButton rbtnSoloIda;
    private JRadioButton rbtnIdaYVuelta;
    private ButtonGroup btnGroupTipoViaje;

    private JComboBox<Ciudad> cmbCiudadOrigen;
    private JComboBox<Ciudad> cmbCiudadDestino;

    // Cambiamos JTextField por JDateChooser
    private JDateChooser dcFechaIda;
    private JDateChooser dcFechaRegreso;

    private JButton btnConsultar;
    private JTable tblVuelos;
    private DefaultTableModel modeloTabla;

    public ConsultarVueloFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Consultar Vuelo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null); 
        setMinimumSize(new Dimension(850, 500));

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(panelPrincipal);

        JPanel panelEncabezado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTituloHeader = new JLabel("Consulta de Vuelos Disponibles");
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloHeader.setForeground(new Color(30, 60, 90));
        panelEncabezado.add(lblTituloHeader);
        panelPrincipal.add(panelEncabezado, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), " Criterios de Búsqueda ",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12), new Color(50, 50, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTipoViaje = new JLabel("Tipo de Viaje:");
        lblTipoViaje.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(lblTipoViaje, gbc);

        JPanel panelRadioButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        rbtnSoloIda = new JRadioButton("Solo Ida", true);
        rbtnIdaYVuelta = new JRadioButton("Ida y Regreso");
        btnGroupTipoViaje = new ButtonGroup();
        btnGroupTipoViaje.add(rbtnSoloIda);
        btnGroupTipoViaje.add(rbtnIdaYVuelta);
        panelRadioButtons.add(rbtnSoloIda);
        panelRadioButtons.add(rbtnIdaYVuelta);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        panelFormulario.add(panelRadioButtons, gbc);

        gbc.gridwidth = 1;

        JLabel lblOrigen = new JLabel("Ciudad de Origen:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(lblOrigen, gbc);

        cmbCiudadOrigen = new JComboBox<>();
        gbc.gridx = 1;
        panelFormulario.add(cmbCiudadOrigen, gbc);

        JLabel lblDestino = new JLabel("Ciudad de Destino:");
        gbc.gridx = 2;
        panelFormulario.add(lblDestino, gbc);

        cmbCiudadDestino = new JComboBox<>();
        gbc.gridx = 3;
        panelFormulario.add(cmbCiudadDestino, gbc);

        // --- AQUI SE INSTANCIA JCALENDAR ---
        JLabel lblFechaIda = new JLabel("Fecha de Viaje:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFormulario.add(lblFechaIda, gbc);

        dcFechaIda = new JDateChooser();
        dcFechaIda.setDateFormatString("yyyy-MM-dd"); // Fija el formato visual
        gbc.gridx = 1;
        panelFormulario.add(dcFechaIda, gbc);

        JLabel lblFechaRegreso = new JLabel("Fecha de Regreso:");
        gbc.gridx = 2;
        panelFormulario.add(lblFechaRegreso, gbc);

        dcFechaRegreso = new JDateChooser();
        dcFechaRegreso.setDateFormatString("yyyy-MM-dd");
        dcFechaRegreso.setEnabled(false); // Deshabilitado por defecto
        gbc.gridx = 3;
        panelFormulario.add(dcFechaRegreso, gbc);

        btnConsultar = new JButton("Consultar");
        btnConsultar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConsultar.setBackground(new Color(25, 115, 210));
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConsultar.setFocusPainted(false);

        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        panelFormulario.add(btnConsultar, gbc);

        panelPrincipal.add(panelFormulario, BorderLayout.NORTH);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), " Oferta de Vuelos Encontrados ",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12), new Color(50, 50, 50)
        ));

        String[] columnas = {
            "N° Vuelo", "Aeropuerto Origen", "Aeropuerto Destino",
            "Salida", "Llegada", "Escalas", "Detalle Escala", "Tarifa", "Precio"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblVuelos = new JTable(modeloTabla);
        tblVuelos.setRowHeight(24);
        tblVuelos.getTableHeader().setReorderingAllowed(false);
        tblVuelos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tblVuelos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollTabla = new JScrollPane(tblVuelos);
        panelTabla.add(scrollTabla, BorderLayout.CENTER);

        panelPrincipal.add(panelTabla, BorderLayout.CENTER);
    }

    // --- GETTERS ACTUALIZADOS ---

    public JRadioButton getRbtnSoloIda() { return rbtnSoloIda; }
    public JRadioButton getRbtnIdaYVuelta() { return rbtnIdaYVuelta; }
    public JComboBox<Ciudad> getCmbCiudadOrigen() { return cmbCiudadOrigen; }
    public JComboBox<Ciudad> getCmbCiudadDestino() { return cmbCiudadDestino; }
    
    // Retornan los componentes JDateChooser
    public JDateChooser getDcFechaIda() { return dcFechaIda; }
    public JDateChooser getDcFechaRegreso() { return dcFechaRegreso; }
    
    public JButton getBtnConsultar() { return btnConsultar; }
    public JTable getTblVuelos() { return tblVuelos; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
}