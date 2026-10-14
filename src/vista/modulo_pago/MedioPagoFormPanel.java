package vista.modulo_pago;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.YearMonth;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import modelo.modulo_pago.MedioPago;
import modelo.modulo_pago.Pago;

/**
 * Panel con los campos propios de cada medio de pago (usa CardLayout:
 * se muestra solo la "tarjeta" del medio elegido). Incluye la validación
 * de los datos. Los datos de tarjeta NUNCA salen de este panel.
 */
public class MedioPagoFormPanel extends JPanel {

    private static final String[] BANCOS = {
        "Bancolombia", "Davivienda", "BBVA", "Banco de Bogotá", "Banco de Occidente", "Nequi"
    };

    private final CardLayout cards = new CardLayout();
    private final JPanel contenedor = new JPanel(cards);

    private final JComboBox<String> cmbBancoPse = new JComboBox<>(BANCOS);

    private final JTextField txtTitular = new JTextField(22);
    private final JTextField txtNumeroTarjeta = new JTextField(22);
    private final JTextField txtVencimiento = new JTextField(6);
    private final JPasswordField txtCvv = new JPasswordField(4);

    private final JComboBox<String> cmbBancoTransf = new JComboBox<>(BANCOS);
    private final JTextField txtComprobante = new JTextField(18);

    private final JTextField txtCodigoBono = new JTextField(18);

    private final JLabel lblMillas = new JLabel(" ");

    private final JComboBox<String> cmbSimulacion = new JComboBox<>(new String[] {
        Pago.APROBADO, Pago.RECHAZADO, Pago.CANCELADO, Pago.EXPIRADO,
        Pago.ERROR_COMUNICACION, Pago.PENDIENTE
    });
    private final JPanel panelSimulacion = new JPanel(new FlowLayout(FlowLayout.LEFT));

    public MedioPagoFormPanel() {
        super(new BorderLayout(0, 8));
        setBorder(BorderFactory.createTitledBorder("Datos del pago"));

        contenedor.add(construirPse(), MedioPago.Tipo.PSE.name());
        contenedor.add(construirTarjeta(), MedioPago.Tipo.TARJETA.name());
        contenedor.add(construirTransferencia(), MedioPago.Tipo.TRANSFERENCIA.name());
        contenedor.add(construirBono(), MedioPago.Tipo.BONO.name());
        contenedor.add(construirMillas(), MedioPago.Tipo.MILLAS.name());
        contenedor.add(new JLabel("Este medio de pago no está soportado por el prototipo."),
                MedioPago.Tipo.OTRO.name());

        panelSimulacion.add(new JLabel("Respuesta de la pasarela (solo prototipo):"));
        panelSimulacion.add(cmbSimulacion);

        add(contenedor, BorderLayout.CENTER);
        add(panelSimulacion, BorderLayout.SOUTH);
        mostrarMedio(MedioPago.Tipo.PSE);
    }

    // ---------- construcción de cada tarjeta ----------

    private JPanel construirPse() {
        JPanel p = nuevoPanel();
        fila(p, 0, "Banco:", cmbBancoPse);
        return p;
    }

    private JPanel construirTarjeta() {
        JPanel p = nuevoPanel();
        fila(p, 0, "Titular:", txtTitular);
        fila(p, 1, "Número de tarjeta:", txtNumeroTarjeta);
        fila(p, 2, "Vence (MM/AA):", txtVencimiento);
        fila(p, 3, "CVV:", txtCvv);
        return p;
    }

    private JPanel construirTransferencia() {
        JPanel p = nuevoPanel();
        fila(p, 0, "Banco de origen:", cmbBancoTransf);
        fila(p, 1, "N.º de comprobante:", txtComprobante);
        return p;
    }

    private JPanel construirBono() {
        JPanel p = nuevoPanel();
        fila(p, 0, "Código del bono:", txtCodigoBono);
        return p;
    }

    private JPanel construirMillas() {
        JPanel p = nuevoPanel();
        fila(p, 0, "Se descontarán:", lblMillas);
        return p;
    }

    private JPanel nuevoPanel() {
        return new JPanel(new GridBagLayout());
    }

    private void fila(JPanel p, int fila, String etiqueta, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.gridy = fila;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        p.add(new JLabel(etiqueta), c);
        c.gridx = 1;
        p.add(campo, c);
    }

    // ---------- API para el controlador ----------

    public void mostrarMedio(MedioPago.Tipo tipo) {
        cards.show(contenedor, tipo.name());
        boolean usaPasarela = tipo == MedioPago.Tipo.PSE
                || tipo == MedioPago.Tipo.TARJETA
                || tipo == MedioPago.Tipo.TRANSFERENCIA;
        panelSimulacion.setVisible(usaPasarela);
    }

    public void setMillasNecesarias(int millas, String equivalencia) {
        lblMillas.setText(millas + " millas  (" + equivalencia + ")");
    }

    /** @return mensaje de error, o null si los datos son válidos. */
    public String validar(MedioPago.Tipo tipo) {
        switch (tipo) {
            case TARJETA:
                if (txtTitular.getText().trim().isEmpty()) return "Ingrese el nombre del titular.";
                String num = soloDigitosTarjeta();
                if (num == null || num.length() < 13 || num.length() > 19)
                    return "El número de tarjeta debe tener entre 13 y 19 dígitos.";
                if (!vencimientoValido()) return "Fecha de vencimiento inválida o vencida (use MM/AA).";
                String cvv = new String(txtCvv.getPassword());
                if (!cvv.matches("\\d{3,4}")) return "El CVV debe tener 3 o 4 dígitos.";
                return null;
            case TRANSFERENCIA:
                if (txtComprobante.getText().trim().isEmpty()) return "Ingrese el número de comprobante.";
                return null;
            case BONO:
                if (txtCodigoBono.getText().trim().isEmpty()) return "Ingrese el código del bono.";
                return null;
            case OTRO:
                return "Seleccione un medio de pago soportado.";
            default:
                return null;
        }
    }

    public String getReferenciaCliente(MedioPago.Tipo tipo) {
        return tipo == MedioPago.Tipo.TRANSFERENCIA ? txtComprobante.getText().trim() : "";
    }

    public String getCodigoBono() { return txtCodigoBono.getText().trim(); }

    public String getResultadoSimulado() { return (String) cmbSimulacion.getSelectedItem(); }

    /** Borra datos sensibles (tarjeta/CVV) después de cada intento. */
    public void limpiar() {
        txtTitular.setText("");
        txtNumeroTarjeta.setText("");
        txtVencimiento.setText("");
        txtCvv.setText("");
        txtComprobante.setText("");
        txtCodigoBono.setText("");
    }

    // ---------- validaciones privadas ----------

    private String soloDigitosTarjeta() {
        String limpio = txtNumeroTarjeta.getText().replaceAll("[\\s-]", "");
        return limpio.matches("\\d+") ? limpio : null;
    }

    private boolean vencimientoValido() {
        String v = txtVencimiento.getText().trim();
        if (!v.matches("(0[1-9]|1[0-2])/\\d{2}")) return false;
        int mes = Integer.parseInt(v.substring(0, 2));
        int anio = 2000 + Integer.parseInt(v.substring(3, 5));
        return !YearMonth.of(anio, mes).isBefore(YearMonth.now());
    }
}
