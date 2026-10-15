package vista.modulo_consulta;

import com.toedter.calendar.JDateChooser;
import modelo.modulo_consulta.Ciudad;
import modelo.modulo_consulta.ClaseServicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Vista unificada para Búsqueda, Filtrado y Selección de Vuelos.
 * Cubre los requerimientos RF-01, RF-02, RF-03, RF-04 y RF-05.
 */
public class BuscarYSeleccionarVueloFrame extends JFrame {

    // --- COMPONENTES BÁSICOS DE VIAJE ---
    private JRadioButton rbtnSoloIda, rbtnIdaYVuelta;
    private JComboBox<Ciudad> cmbCiudadOrigen, cmbCiudadDestino;
    private JDateChooser dcFechaIda, dcFechaRegreso;

    // --- COMPONENTES DE PASAJEROS Y CLASE (RF-02) ---
    private JSpinner spnAdultos, spnNinos, spnBebes;
    private JComboBox<ClaseServicio> cmbClaseServicio;

    // --- COMPONENTES DE FILTROS Y ORDENAMIENTO (RF-04) ---
    private JComboBox<String> cmbOrdenamiento;
    private JCheckBox chkSoloDirectos, chkConEquipaje;
    private JSpinner spnPrecioMaximo;

    // --- BOTONES Y TABLA (RF-03, RF-05) ---
    private JButton btnConsultar, btnSeleccionarVuelo, btnVolverMenu;
    private JTable tblVuelos;
    private DefaultTableModel modeloTabla;
    private JLabel lblEstadoBusqueda;

    public BuscarYSeleccionarVueloFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Buscar y Seleccionar Vuelo");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1200, 750); 
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(10, 15, 10, 15));
        setContentPane(panelPrincipal);

        // --- CONTENEDOR SUPERIOR (BÚSQUEDA + PASAJEROS + FILTROS) ---
        JPanel panelControlesSuperior = new JPanel();
        panelControlesSuperior.setLayout(new BoxLayout(panelControlesSuperior, BoxLayout.Y_AXIS));

        // 1. Panel de Ruta y Fechas (MODIFICADO: Dividido en 2 filas para que nada se oculte)
        JPanel panelRuta = new JPanel();
        panelRuta.setLayout(new BoxLayout(panelRuta, BoxLayout.Y_AXIS));
        panelRuta.setBorder(BorderFactory.createTitledBorder("1. Ruta y Fechas"));
        
        // Fila 1 de Ruta: RadioButtons
        JPanel fila1Ruta = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        rbtnSoloIda = new JRadioButton("Solo Ida", true);
        rbtnIdaYVuelta = new JRadioButton("Ida y Regreso");
        ButtonGroup bgTipo = new ButtonGroup();
        bgTipo.add(rbtnSoloIda); bgTipo.add(rbtnIdaYVuelta);
        fila1Ruta.add(rbtnSoloIda); 
        fila1Ruta.add(rbtnIdaYVuelta);

        // Fila 2 de Ruta: ComboBoxes y Calendarios
        JPanel fila2Ruta = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        cmbCiudadOrigen = new JComboBox<>();
        cmbCiudadDestino = new JComboBox<>();
        dcFechaIda = new JDateChooser(); dcFechaIda.setDateFormatString("yyyy-MM-dd");
        dcFechaRegreso = new JDateChooser(); dcFechaRegreso.setDateFormatString("yyyy-MM-dd");
        dcFechaRegreso.setEnabled(false);

        fila2Ruta.add(new JLabel("Origen:")); fila2Ruta.add(cmbCiudadOrigen);
        fila2Ruta.add(new JLabel("  Destino:")); fila2Ruta.add(cmbCiudadDestino);
        fila2Ruta.add(new JLabel("  Fecha Ida:")); fila2Ruta.add(dcFechaIda);
        fila2Ruta.add(new JLabel("  Fecha Regreso:")); fila2Ruta.add(dcFechaRegreso);

        panelRuta.add(fila1Ruta);
        panelRuta.add(fila2Ruta);

        // 2. Panel de Pasajeros y Clase (RF-02) (MODIFICADO: Límite de pasajeros subido a 100)
        JPanel panelPasajeros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelPasajeros.setBorder(BorderFactory.createTitledBorder("2. Pasajeros y Clase"));
        
        spnAdultos = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)); // Máx 100
        spnNinos = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));   // Máx 100
        spnBebes = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));   // Máx 100
        cmbClaseServicio = new JComboBox<>();

        panelPasajeros.add(new JLabel("Adultos (>12):")); panelPasajeros.add(spnAdultos);
        panelPasajeros.add(new JLabel("  Niños (2-12):")); panelPasajeros.add(spnNinos);
        panelPasajeros.add(new JLabel("  Bebés (<2):")); panelPasajeros.add(spnBebes);
        panelPasajeros.add(new JLabel("    Clase:")); panelPasajeros.add(cmbClaseServicio);

        // 3. Panel de Filtros y Ordenamiento (RF-04)
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("3. Filtros y Ordenamiento (Opcional)"));
        
        cmbOrdenamiento = new JComboBox<>(new String[]{"Precio", "Duración del vuelo", "Horario de salida", "Cantidad de escalas"});
        chkSoloDirectos = new JCheckBox("Solo vuelos directos");
        chkConEquipaje = new JCheckBox("Equipaje incluido");
        spnPrecioMaximo = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000000.0, 50000.0)); 

        panelFiltros.add(new JLabel("Ordenar por:")); panelFiltros.add(cmbOrdenamiento);
        panelFiltros.add(new JLabel("  |  "));
        panelFiltros.add(chkSoloDirectos);
        panelFiltros.add(chkConEquipaje);
        panelFiltros.add(new JLabel("  Precio Máx ($):")); panelFiltros.add(spnPrecioMaximo);
        
        btnConsultar = new JButton("Consultar Vuelos");
        btnConsultar.setBackground(new Color(25, 115, 210));
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelFiltros.add(Box.createHorizontalStrut(20)); 
        panelFiltros.add(btnConsultar);

        // Ensamblar panel superior
        panelControlesSuperior.add(panelRuta);
        panelControlesSuperior.add(panelPasajeros);
        panelControlesSuperior.add(panelFiltros);
        panelPrincipal.add(panelControlesSuperior, BorderLayout.NORTH);

        // --- CONTENEDOR CENTRAL (TABLA DE RESULTADOS - RF-03) ---
        JPanel panelTabla = new JPanel(new BorderLayout(0, 5));
        
        lblEstadoBusqueda = new JLabel("Esperando consulta...");
        lblEstadoBusqueda.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstadoBusqueda.setForeground(new Color(150, 50, 50));
        panelTabla.add(lblEstadoBusqueda, BorderLayout.NORTH);

        String[] columnas = {
            "N° Vuelo", "Salida", "Llegada", "Duración", "Origen", "Destino", 
            "Escalas", "Aeronave", "Clase/Tarifa", "Cupos", "Precio Base", "Restricciones"
        };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblVuelos = new JTable(modeloTabla);
        tblVuelos.getTableHeader().setReorderingAllowed(false);
        tblVuelos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblVuelos.getColumnModel().getColumn(11).setPreferredWidth(200); 

        panelTabla.add(new JScrollPane(tblVuelos), BorderLayout.CENTER);
        panelPrincipal.add(panelTabla, BorderLayout.CENTER);

        // --- CONTENEDOR INFERIOR (BOTÓN SELECCIONAR - RF-05) ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSeleccionarVuelo = new JButton("Seleccionar Vuelo");
        btnSeleccionarVuelo.setBackground(new Color(40, 160, 70));
        btnSeleccionarVuelo.setForeground(Color.WHITE);
        btnSeleccionarVuelo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSeleccionarVuelo.setEnabled(false); 
        btnVolverMenu = new JButton("Volver al menú");
        btnVolverMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelInferior.add(btnVolverMenu);
        panelInferior.add(btnSeleccionarVuelo);
        
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
    }

    // --- GETTERS PARA EL CONTROLADOR ---

    public JRadioButton getRbtnSoloIda() { return rbtnSoloIda; }
    public JRadioButton getRbtnIdaYVuelta() { return rbtnIdaYVuelta; }
    public JComboBox<Ciudad> getCmbCiudadOrigen() { return cmbCiudadOrigen; }
    public JComboBox<Ciudad> getCmbCiudadDestino() { return cmbCiudadDestino; }
    public JDateChooser getDcFechaIda() { return dcFechaIda; }
    public JDateChooser getDcFechaRegreso() { return dcFechaRegreso; }
    
    public JSpinner getSpnAdultos() { return spnAdultos; }
    public JSpinner getSpnNinos() { return spnNinos; }
    public JSpinner getSpnBebes() { return spnBebes; }
    public JComboBox<ClaseServicio> getCmbClaseServicio() { return cmbClaseServicio; }
    
    public JComboBox<String> getCmbOrdenamiento() { return cmbOrdenamiento; }
    public JCheckBox getChkSoloDirectos() { return chkSoloDirectos; }
    public JCheckBox getChkConEquipaje() { return chkConEquipaje; }
    public JSpinner getSpnPrecioMaximo() { return spnPrecioMaximo; }
    
    public JButton getBtnConsultar() { return btnConsultar; }
    public JButton getBtnSeleccionarVuelo() { return btnSeleccionarVuelo; }
    public JButton getBtnVolverMenu() { return btnVolverMenu; }
    public JTable getTblVuelos() { return tblVuelos; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public JLabel getLblEstadoBusqueda() { return lblEstadoBusqueda; }

    /** Para que la X de la ventana ejecute la acción indicada (ej. volver al menú). */
    public void addCierreListener(Runnable accion) {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { accion.run(); }
        });
    }
}
