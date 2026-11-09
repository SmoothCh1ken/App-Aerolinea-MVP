package vista.modulo_seguridad;

import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modelo.modulo_seguridad.EventoSeguridad;
import modelo.modulo_seguridad.Incidente;
import modelo.modulo_seguridad.RegistroAuditoria;

/**
 * Pestaña "Auditoría" (RF-31) y pestaña "Incidentes" (RF-29).
 * Cada pestaña solo se muestra si el rol del funcionario tiene el permiso correspondiente.
 */
public class AuditoriaFrame extends JFrame {

    private static final String TODOS = "Todos";

    // --- auditoría ---
    private final JDateChooser dcDesde = new JDateChooser();
    private final JDateChooser dcHasta = new JDateChooser();
    private final JTextField txtUsuario = new JTextField(12);
    private final JComboBox<String> cmbEvento = new JComboBox<>();
    private final JComboBox<String> cmbResultado = new JComboBox<>(
            new String[] {TODOS, EventoSeguridad.EXITOSO, EventoSeguridad.FALLIDO});
    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnLimpiar = new JButton("Limpiar filtros");
    private final DefaultTableModel modeloAuditoria = new DefaultTableModel(
            new String[] {"Fecha y hora", "Usuario", "Evento", "Resultado", "Detalle"}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };

    // --- incidentes ---
    private final JCheckBox chkSoloAbiertos = new JCheckBox("Solo abiertos", true);
    private final JButton btnActualizarIncidentes = new JButton("Actualizar");
    private final JButton btnAtender = new JButton("Marcar como atendido");
    private final JButton btnDesbloquear = new JButton("Desbloquear cuenta");
    private final DefaultTableModel modeloIncidentes = new DefaultTableModel(
            new String[] {"N.º", "Fecha y hora", "Usuario", "Tipo", "Detalle", "Estado"}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tablaIncidentes = new JTable(modeloIncidentes);

    private final JTabbedPane pestanas = new JTabbedPane();
    private final JButton btnCerrar = new JButton("Cerrar");

    public AuditoriaFrame(boolean verAuditoria, boolean verIncidentes) {
        super("Auditoría e incidentes de seguridad");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1250, 600);
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout(8, 8));
        principal.setBorder(new EmptyBorder(10, 12, 10, 12));
        setContentPane(principal);

        cmbEvento.addItem(TODOS);
        for (String e : EventoSeguridad.TODOS) cmbEvento.addItem(e);

        if (verAuditoria) pestanas.addTab("Auditoría", construirAuditoria());
        if (verIncidentes) pestanas.addTab("Incidentes de seguridad", construirIncidentes());
        principal.add(pestanas, BorderLayout.CENTER);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pie.add(btnCerrar);
        principal.add(pie, BorderLayout.SOUTH);
        btnCerrar.addActionListener(e -> dispose());
    }

    private JPanel construirAuditoria() {
        dcDesde.setDateFormatString("yyyy-MM-dd");
        dcHasta.setDateFormatString("yyyy-MM-dd");
        dcDesde.setPreferredSize(new Dimension(130, 26));
        dcHasta.setPreferredSize(new Dimension(130, 26));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        filtros.add(new JLabel("Desde:")); filtros.add(dcDesde);
        filtros.add(new JLabel("Hasta:")); filtros.add(dcHasta);
        filtros.add(new JLabel("Usuario:")); filtros.add(txtUsuario);
        filtros.add(new JLabel("Evento:")); filtros.add(cmbEvento);
        filtros.add(new JLabel("Resultado:")); filtros.add(cmbResultado);
        filtros.add(btnBuscar); filtros.add(btnLimpiar);

        JTable tabla = new JTable(modeloAuditoria);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setReorderingAllowed(false);
        int[] anchos = {140, 130, 170, 80, 520};
        for (int i = 0; i < anchos.length; i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.add(filtros, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirIncidentes() {
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        acciones.add(chkSoloAbiertos);
        acciones.add(btnActualizarIncidentes);
        acciones.add(btnAtender);
        acciones.add(btnDesbloquear);

        tablaIncidentes.setRowHeight(22);
        tablaIncidentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaIncidentes.getTableHeader().setReorderingAllowed(false);
        int[] anchos = {50, 140, 130, 120, 520, 80};
        for (int i = 0; i < anchos.length; i++) tablaIncidentes.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.add(acciones, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaIncidentes), BorderLayout.CENTER);
        return panel;
    }

    // ---------- API para el controlador: auditoría ----------

    public Date getDesde() { return dcDesde.getDate(); }
    public Date getHasta() { return dcHasta.getDate(); }
    public String getUsuarioFiltro() { return txtUsuario.getText(); }

    /** @return el evento elegido, o null si es "Todos". */
    public String getEventoFiltro() {
        String e = (String) cmbEvento.getSelectedItem();
        return TODOS.equals(e) ? null : e;
    }

    public String getResultadoFiltro() {
        String r = (String) cmbResultado.getSelectedItem();
        return TODOS.equals(r) ? null : r;
    }

    public void limpiarFiltros() {
        dcDesde.setDate(null);
        dcHasta.setDate(null);
        txtUsuario.setText("");
        cmbEvento.setSelectedIndex(0);
        cmbResultado.setSelectedIndex(0);
    }

    public void mostrarAuditoria(List<RegistroAuditoria> registros) {
        modeloAuditoria.setRowCount(0);
        for (RegistroAuditoria r : registros) {
            modeloAuditoria.addRow(new Object[] {r.getFecha(), r.getUsuario(), r.getEvento(),
                r.getResultado(), r.getDetalle()});
        }
    }

    public void addBuscarListener(ActionListener l) { btnBuscar.addActionListener(l); }
    public void addLimpiarListener(ActionListener l) { btnLimpiar.addActionListener(l); }

    // ---------- API para el controlador: incidentes ----------

    public boolean isSoloAbiertos() { return chkSoloAbiertos.isSelected(); }

    public int getIncidenteSeleccionado() { return tablaIncidentes.getSelectedRow(); }

    public void mostrarIncidentes(List<Incidente> incidentes) {
        modeloIncidentes.setRowCount(0);
        for (Incidente i : incidentes) {
            modeloIncidentes.addRow(new Object[] {i.getId(), i.getFecha(), i.getUsuario(), i.getTipo(),
                i.getDetalle(), i.getEstado()});
        }
    }

    public void addActualizarIncidentesListener(ActionListener l) {
        btnActualizarIncidentes.addActionListener(l);
        chkSoloAbiertos.addActionListener(l);
    }
    public void addAtenderListener(ActionListener l) { btnAtender.addActionListener(l); }
    public void addDesbloquearListener(ActionListener l) { btnDesbloquear.addActionListener(l); }

    // ---------- mensajes ----------

    public void mostrarInfo(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarAdvertencia(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
