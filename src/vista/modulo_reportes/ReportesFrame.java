package vista.modulo_reportes;

import com.toedter.calendar.JDateChooser;
import modelo.modulo_reportes.OpcionFiltro;
import modelo.modulo_reportes.Periodo;
import modelo.modulo_reportes.TipoReporte;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Pantalla del módulo de Reportes Gerenciales y Analítica.
 * Filtros arriba (RF-33) y dos pestañas: tablero ejecutivo (RF-34) y reportes (RF-32).
 */
public class ReportesFrame extends JFrame {

    private static final String[] TITULOS_TARJETAS = {
        "Ventas confirmadas", "Tiquetes vendidos", "Ingresos por servicios",
        "Ocupación promedio", "Reservas confirmadas", "Pendientes de pago",
        "Vuelos reprogramados / cancelados", "Aprobación de pagos", "Reembolsos"
    };

    // --- Filtros ---
    private final JDateChooser dcDesde = new JDateChooser();
    private final JDateChooser dcHasta = new JDateChooser();
    private final JComboBox<OpcionFiltro> cmbOrigen = new JComboBox<>();
    private final JComboBox<OpcionFiltro> cmbDestino = new JComboBox<>();
    private final JComboBox<OpcionFiltro> cmbRuta = new JComboBox<>();
    private final JComboBox<Periodo> cmbPeriodo = new JComboBox<>(Periodo.values());
    private final JButton btnAplicar = new JButton("Aplicar filtros");
    private final JButton btnLimpiar = new JButton("Limpiar");

    // --- Pestañas ---
    private final JTabbedPane pestanas = new JTabbedPane();

    // --- Tablero ---
    private final TarjetaIndicadorPanel[] tarjetas = new TarjetaIndicadorPanel[TITULOS_TARJETAS.length];
    private final JButton btnActualizar = new JButton("Actualizar ahora");
    private final JCheckBox chkAutomatico = new JCheckBox("Actualizar cada 30 segundos", true);
    private final JLabel lblActualizado = new JLabel("Sin datos todavía");

    // --- Reportes ---
    private final JComboBox<TipoReporte> cmbTipo = new JComboBox<>(TipoReporte.values());
    private final JButton btnGenerar = new JButton("Generar reporte");
    private final JButton btnExportar = new JButton("Exportar reporte..."); // Actualizado para reflejar múltiples formatos
    private final JLabel lblNota = new JLabel(" ");
    private final DefaultTableModel modeloTabla = new DefaultTableModel() {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);

    private final JButton btnVolver = new JButton("Volver al menú");

    public ReportesFrame() {
        super("Reportes Gerenciales y Analítica");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(950, 560));

        JPanel principal = new JPanel(new BorderLayout(8, 8));
        principal.setBorder(new EmptyBorder(10, 12, 10, 12));
        setContentPane(principal);

        principal.add(construirFiltros(), BorderLayout.NORTH);

        pestanas.addTab("Tablero ejecutivo", construirTablero());
        pestanas.addTab("Reportes", construirReportes());
        principal.add(pestanas, BorderLayout.CENTER);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(btnVolver);
        principal.add(barra, BorderLayout.SOUTH);
    }

    // ---------- construcción de la pantalla ----------

    private JPanel construirFiltros() {
        dcDesde.setDateFormatString("yyyy-MM-dd");
        dcHasta.setDateFormatString("yyyy-MM-dd");
        dcDesde.setPreferredSize(new Dimension(130, 26));
        dcHasta.setPreferredSize(new Dimension(130, 26));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        fila1.add(new JLabel("Desde:")); fila1.add(dcDesde);
        fila1.add(new JLabel("Hasta:")); fila1.add(dcHasta);
        fila1.add(new JLabel("Agrupar por:")); fila1.add(cmbPeriodo);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        fila2.add(new JLabel("Origen:")); fila2.add(cmbOrigen);
        fila2.add(new JLabel("Destino:")); fila2.add(cmbDestino);
        fila2.add(new JLabel("Ruta:")); fila2.add(cmbRuta);
        btnAplicar.setBackground(new Color(25, 115, 210));
        btnAplicar.setForeground(Color.WHITE);
        fila2.add(btnAplicar); fila2.add(btnLimpiar);

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Filtros (fechas, ciudades, ruta y período)"));
        panel.add(fila1);
        panel.add(fila2);
        return panel;
    }

    private JPanel construirTablero() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel rejilla = new JPanel(new GridLayout(3, 3, 12, 12));
        for (int i = 0; i < TITULOS_TARJETAS.length; i++) {
            tarjetas[i] = new TarjetaIndicadorPanel(TITULOS_TARJETAS[i]);
            rejilla.add(tarjetas[i]);
        }
        panel.add(rejilla, BorderLayout.CENTER);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        pie.add(btnActualizar);
        pie.add(chkAutomatico);
        pie.add(lblActualizado);
        panel.add(pie, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirReportes() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel opciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        opciones.add(new JLabel("Reporte:"));
        opciones.add(cmbTipo);
        btnGenerar.setBackground(new Color(40, 160, 70));
        btnGenerar.setForeground(Color.WHITE);
        opciones.add(btnGenerar);
        opciones.add(btnExportar);

        lblNota.setFont(lblNota.getFont().deriveFont(Font.ITALIC));
        lblNota.setBorder(new EmptyBorder(0, 14, 0, 0));

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(opciones, BorderLayout.NORTH);
        norte.add(lblNota, BorderLayout.SOUTH);
        panel.add(norte, BorderLayout.NORTH);

        tabla.setRowHeight(24);
        tabla.getTableHeader().setReorderingAllowed(false);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    // ---------- API para el controlador: filtros ----------

    public void cargarOrigenes(List<OpcionFiltro> opciones) { cargar(cmbOrigen, opciones); }
    public void cargarDestinos(List<OpcionFiltro> opciones) { cargar(cmbDestino, opciones); }
    public void cargarRutas(List<OpcionFiltro> opciones) { cargar(cmbRuta, opciones); }

    private void cargar(JComboBox<OpcionFiltro> combo, List<OpcionFiltro> opciones) {
        combo.removeAllItems();
        combo.addItem(OpcionFiltro.TODAS);
        for (OpcionFiltro o : opciones) combo.addItem(o);
    }

    public Date getDesde() { return dcDesde.getDate(); }
    public Date getHasta() { return dcHasta.getDate(); }
    public OpcionFiltro getOrigen() { return (OpcionFiltro) cmbOrigen.getSelectedItem(); }
    public OpcionFiltro getDestino() { return (OpcionFiltro) cmbDestino.getSelectedItem(); }
    public OpcionFiltro getRuta() { return (OpcionFiltro) cmbRuta.getSelectedItem(); }
    public Periodo getPeriodo() { return (Periodo) cmbPeriodo.getSelectedItem(); }
    public TipoReporte getTipoReporte() { return (TipoReporte) cmbTipo.getSelectedItem(); }

    public void limpiarFiltros() {
        dcDesde.setDate(null);
        dcHasta.setDate(null);
        if (cmbOrigen.getItemCount() > 0) cmbOrigen.setSelectedIndex(0);
        if (cmbDestino.getItemCount() > 0) cmbDestino.setSelectedIndex(0);
        if (cmbRuta.getItemCount() > 0) cmbRuta.setSelectedIndex(0);
        cmbPeriodo.setSelectedItem(Periodo.MES);
    }

    // ---------- API para el controlador: tablero y reportes ----------

    public void setIndicadores(String[] valores) {
        for (int i = 0; i < tarjetas.length && i < valores.length; i++) {
            tarjetas[i].setValor(valores[i]);
        }
    }

    public void setEstadoTablero(String texto) { lblActualizado.setText(texto); }

    public boolean isTableroActivo() { return pestanas.getSelectedIndex() == 0; }

    public boolean isActualizacionAutomatica() { return chkAutomatico.isSelected(); }

    public void setNota(String texto) { lblNota.setText(texto); }

    public void mostrarReporte(String[] columnas, List<String[]> filas) {
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);
        for (String[] fila : filas) modeloTabla.addRow(fila);
    }

    // ---------- API para el controlador: eventos ----------

    public void addAplicarListener(ActionListener l) { btnAplicar.addActionListener(l); }
    public void addLimpiarListener(ActionListener l) { btnLimpiar.addActionListener(l); }
    public void addActualizarListener(ActionListener l) { btnActualizar.addActionListener(l); }
    public void addGenerarListener(ActionListener l) { btnGenerar.addActionListener(l); }
    public void addExportarListener(ActionListener l) { btnExportar.addActionListener(l); }
    public void addTipoListener(ActionListener l) { cmbTipo.addActionListener(l); }
    public void addVolverListener(ActionListener l) { btnVolver.addActionListener(l); }

    /** Para que la X de la ventana se comporte igual que "Volver al menú". */
    public void addCierreListener(Runnable accion) {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { accion.run(); }
        });
    }

    // ---------- mensajes y diálogos ----------

    public void mostrarInfo(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarAdvertencia(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** 
     * Selector de archivos unificado para exportar en múltiples formatos (CSV, Excel, PDF). 
     * Permite al usuario elegir la extensión y ajusta el nombre automáticamente.
     */
    public File elegirArchivoExportacion(String nombreBase) {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar reporte exportado");
        
        // Filtros para los tres formatos soportados
        FileNameExtensionFilter filtroExcel = new FileNameExtensionFilter("Libro de Excel (*.xlsx)", "xlsx");
        FileNameExtensionFilter filtroPdf = new FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf");
        FileNameExtensionFilter filtroCsv = new FileNameExtensionFilter("Archivo CSV (*.csv)", "csv");
        
        selector.addChoosableFileFilter(filtroExcel);
        selector.addChoosableFileFilter(filtroPdf);
        selector.addChoosableFileFilter(filtroCsv);
        selector.setFileFilter(filtroExcel); // Por defecto sugerimos Excel moderno o CSV
        
        selector.setSelectedFile(new File(nombreBase + ".xlsx"));

        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File archivo = selector.getSelectedFile();
        javax.swing.filechooser.FileFilter filtroActual = selector.getFileFilter();

        // Asegurar la extensión correcta según el filtro seleccionado por el usuario
        String nombre = archivo.getName().toLowerCase();
        if (filtroActual == filtroPdf && !nombre.endsWith(".pdf")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".pdf");
        } else if (filtroActual == filtroExcel && !nombre.endsWith(".xlsx")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".xlsx");
        } else if (filtroActual == filtroCsv && !nombre.endsWith(".csv")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".csv");
        } else if (!nombre.endsWith(".xlsx") && !nombre.endsWith(".pdf") && !nombre.endsWith(".csv")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".xlsx"); // Predeterminado
        }

        return archivo;
    }
}