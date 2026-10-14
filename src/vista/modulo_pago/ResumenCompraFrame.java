package vista.modulo_pago;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import modelo.modulo_pago.LineaCompra;
import modelo.modulo_pago.ResumenCompra;

/** Pantalla 1 del módulo: resumen de la compra antes de pagar (RF-17). */
public class ResumenCompraFrame extends JFrame {

    private static final String[] COLUMNAS = {
        "Pasajero", "Categoría", "Tramo", "Vuelo", "Salida", "Tarifa",
        "Precio base", "Descuento", "Impuestos", "Servicios", "Detalle servicios", "Subtotal"
    };

    private final JTextField txtCodigo = new JTextField(12);
    private final JButton btnBuscar = new JButton("Buscar reserva");

    private final JLabel lblReserva = new JLabel("-");
    private final JLabel lblEstado = new JLabel("-");
    private final JLabel lblComprador = new JLabel("-");
    private final JLabel lblExpira = new JLabel("-");

    private final DefaultTableModel modelo = new DefaultTableModel(COLUMNAS, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    private final JLabel lblBase = new JLabel("-");
    private final JLabel lblDescuentos = new JLabel("-");
    private final JLabel lblImpuestos = new JLabel("-");
    private final JLabel lblServicios = new JLabel("-");
    private final JLabel lblTotal = new JLabel("-");

    private final JButton btnContinuar = new JButton("Continuar al pago");
    private final JButton btnCerrar = new JButton("Cerrar");

    public ResumenCompraFrame() {
        super("Pago de reserva - Resumen de compra");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1150, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        // Parte superior: búsqueda + datos de la reserva
        JPanel norte = new JPanel(new BorderLayout());
        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busqueda.add(new JLabel("Código de reserva:"));
        busqueda.add(txtCodigo);
        busqueda.add(btnBuscar);
        JPanel datos = new JPanel(new GridLayout(2, 4, 8, 4));
        datos.setBorder(BorderFactory.createEmptyBorder(0, 10, 6, 10));
        datos.add(negrita("Reserva:")); datos.add(lblReserva);
        datos.add(negrita("Estado:")); datos.add(lblEstado);
        datos.add(negrita("Comprador:")); datos.add(lblComprador);
        datos.add(negrita("Plazo para pagar:")); datos.add(lblExpira);
        norte.add(busqueda, BorderLayout.NORTH);
        norte.add(datos, BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

        // Centro: tabla
        tabla.setRowHeight(24);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] anchos = {160, 80, 70, 60, 120, 110, 95, 85, 90, 90, 220, 105};
        for (int i = 0; i < anchos.length; i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Sur: totales + botones
        JPanel totales = new JPanel(new GridLayout(5, 2, 4, 2));
        totales.setBorder(BorderFactory.createTitledBorder("Totales"));
        totales.add(new JLabel("Tarifas (precio base):")); totales.add(derecha(lblBase));
        totales.add(new JLabel("Descuentos:")); totales.add(derecha(lblDescuentos));
        totales.add(new JLabel("Impuestos:")); totales.add(derecha(lblImpuestos));
        totales.add(new JLabel("Servicios complementarios:")); totales.add(derecha(lblServicios));
        JLabel etiquetaTotal = negrita("TOTAL A PAGAR:");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 16f));
        totales.add(etiquetaTotal); totales.add(derecha(lblTotal));
        totales.setPreferredSize(new Dimension(340, 130));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnContinuar.setEnabled(false);
        botones.add(btnCerrar);
        botones.add(btnContinuar);

        JPanel sur = new JPanel(new BorderLayout());
        JPanel contenedorTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        contenedorTotales.add(totales);
        sur.add(contenedorTotales, BorderLayout.NORTH);
        sur.add(botones, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);

        btnCerrar.addActionListener(e -> dispose());
    }

    private JLabel negrita(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(l.getFont().deriveFont(Font.BOLD));
        return l;
    }

    private JLabel derecha(JLabel l) {
        l.setHorizontalAlignment(JLabel.RIGHT);
        return l;
    }

    // ---------- API para el controlador ----------

    public String getCodigoIngresado() { return txtCodigo.getText().trim(); }

    public void setCodigoIngresado(String codigo) { txtCodigo.setText(codigo == null ? "" : codigo); }

    public void addBuscarListener(ActionListener l) {
        btnBuscar.addActionListener(l);
        txtCodigo.addActionListener(l); // Enter en el campo también busca
    }

    public void addContinuarListener(ActionListener l) { btnContinuar.addActionListener(l); }

    public void mostrarResumen(ResumenCompra r) {
        lblReserva.setText(r.getCodigoReserva());
        lblEstado.setText(r.getEstadoReserva());
        lblComprador.setText(r.getNombreComprador());
        lblExpira.setText(FormatoUtil.fecha(r.getFechaExpiracion()));

        modelo.setRowCount(0);
        for (LineaCompra l : r.getLineas()) {
            modelo.addRow(new Object[] {
                l.getPasajero(), l.getCategoria(), l.getSentido(),
                l.getNumeroVuelo() + " " + l.getOrigen() + "-" + l.getDestino(),
                FormatoUtil.fecha(l.getFechaSalida()), l.getTarifa(),
                FormatoUtil.moneda(l.getPrecioBase()), FormatoUtil.moneda(l.getDescuento()),
                FormatoUtil.moneda(l.getImpuestos()), FormatoUtil.moneda(l.getServicios()),
                l.getDetalleServicios() == null ? "-" : l.getDetalleServicios(),
                FormatoUtil.moneda(l.getSubtotal())
            });
        }
        lblBase.setText(FormatoUtil.moneda(r.getTotalBase()));
        lblDescuentos.setText("- " + FormatoUtil.moneda(r.getTotalDescuentos()));
        lblImpuestos.setText(FormatoUtil.moneda(r.getTotalImpuestos()));
        lblServicios.setText(FormatoUtil.moneda(r.getTotalServicios()));
        lblTotal.setText(FormatoUtil.moneda(r.getTotal()));

        boolean pagable = r.isPagable();
        btnContinuar.setEnabled(pagable);
        lblEstado.setForeground(pagable ? new Color(0, 110, 0) : Color.RED.darker());
        if (!pagable) {
            mostrarMensaje("La reserva está \"" + r.getEstadoReserva() + "\" y no admite pago.");
        }
    }

    public void limpiar() {
        lblReserva.setText("-"); lblEstado.setText("-"); lblComprador.setText("-"); lblExpira.setText("-");
        modelo.setRowCount(0);
        lblBase.setText("-"); lblDescuentos.setText("-"); lblImpuestos.setText("-");
        lblServicios.setText("-"); lblTotal.setText("-");
        btnContinuar.setEnabled(false);
    }

    public void mostrarMensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
