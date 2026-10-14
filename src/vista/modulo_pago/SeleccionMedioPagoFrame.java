package vista.modulo_pago;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import modelo.modulo_pago.MedioPago;
import modelo.modulo_pago.ResumenCompra;

/** Pantalla 2 del módulo: selección del medio de pago y captura de sus datos (RF-18). */
public class SeleccionMedioPagoFrame extends JFrame {

    private final JLabel lblReserva = new JLabel("-");
    private final JLabel lblTotal = new JLabel("-");
    private final JComboBox<MedioPago> cmbMedios = new JComboBox<>();
    private final MedioPagoFormPanel panelFormulario = new MedioPagoFormPanel();
    private final JButton btnPagar = new JButton("Pagar");
    private final JButton btnVolver = new JButton("Volver al resumen");

    public SeleccionMedioPagoFrame() {
        super("Pago de reserva - Medio de pago");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(520, 470);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JPanel norte = new JPanel(new GridLayout(3, 2, 6, 6));
        norte.setBorder(BorderFactory.createEmptyBorder(10, 12, 0, 12));
        norte.add(negrita("Reserva:")); norte.add(lblReserva);
        norte.add(negrita("Total a pagar:"));
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 15f));
        norte.add(lblTotal);
        norte.add(negrita("Medio de pago:")); norte.add(cmbMedios);
        add(norte, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        centro.add(panelFormulario, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(btnVolver);
        sur.add(btnPagar);
        add(sur, BorderLayout.SOUTH);
    }

    private JLabel negrita(String t) {
        JLabel l = new JLabel(t);
        l.setFont(l.getFont().deriveFont(Font.BOLD));
        return l;
    }

    // ---------- API para el controlador ----------

    public void setResumen(ResumenCompra r) {
        lblReserva.setText(r.getCodigoReserva());
        lblTotal.setText(FormatoUtil.moneda(r.getTotal()));
    }

    public void cargarMedios(List<MedioPago> medios) {
        cmbMedios.removeAllItems();
        for (MedioPago m : medios) cmbMedios.addItem(m);
    }

    public MedioPago getMedioSeleccionado() { return (MedioPago) cmbMedios.getSelectedItem(); }

    public MedioPagoFormPanel getFormPanel() { return panelFormulario; }

    public void addMedioListener(ActionListener l) { cmbMedios.addActionListener(l); }
    public void addPagarListener(ActionListener l) { btnPagar.addActionListener(l); }
    public void addVolverListener(ActionListener l) { btnVolver.addActionListener(l); }

    /** Para que la X de la ventana se comporte igual que "Volver". */
    public void addCierreListener(Runnable accion) {
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { accion.run(); }
        });
    }

    public void setProcesando(boolean procesando) {
        btnPagar.setEnabled(!procesando);
        btnVolver.setEnabled(!procesando);
        btnPagar.setText(procesando ? "Procesando..." : "Pagar");
    }

    public boolean confirmar(String texto) {
        return JOptionPane.showConfirmDialog(this, texto, "Confirmar pago",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public void mostrarAdvertencia(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Datos incompletos", JOptionPane.WARNING_MESSAGE);
    }

    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
