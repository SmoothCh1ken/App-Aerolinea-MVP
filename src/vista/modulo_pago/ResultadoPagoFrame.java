package vista.modulo_pago;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import modelo.modulo_pago.Pago;
import modelo.modulo_pago.ResultadoPago;

/** Pantalla 3 del módulo: resultado del pago y tiquetes emitidos (RF-19 y RF-20). */
public class ResultadoPagoFrame extends JFrame {

    private final JLabel lblEstado = new JLabel(" ", JLabel.CENTER);
    private final JTextArea txtMensaje = new JTextArea(4, 30);
    private final JLabel lblReferencia = new JLabel(" ");
    private final DefaultListModel<String> modeloTiquetes = new DefaultListModel<>();
    private final JLabel lblCorreo = new JLabel(" ");
    private final JButton btnReintentar = new JButton("Reintentar pago");
    private final JButton btnFinalizar = new JButton("Finalizar");

    public ResultadoPagoFrame() {
        super("Pago de reserva - Resultado");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(500, 440);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        lblEstado.setFont(lblEstado.getFont().deriveFont(Font.BOLD, 20f));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(14, 8, 6, 8));
        add(lblEstado, BorderLayout.NORTH);

        txtMensaje.setEditable(false);
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.setBackground(getBackground());

        JPanel centro = new JPanel(new BorderLayout(6, 6));
        centro.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        JPanel arriba = new JPanel(new BorderLayout(4, 4));
        arriba.add(txtMensaje, BorderLayout.CENTER);
        arriba.add(lblReferencia, BorderLayout.SOUTH);
        centro.add(arriba, BorderLayout.NORTH);

        JList<String> lista = new JList<>(modeloTiquetes);
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createTitledBorder("Tiquetes electrónicos emitidos"));
        centro.add(scroll, BorderLayout.CENTER);
        centro.add(lblCorreo, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(btnReintentar);
        sur.add(btnFinalizar);
        add(sur, BorderLayout.SOUTH);
    }

    public void mostrar(ResultadoPago r) {
        lblEstado.setText(r.getEstadoPago());
        lblEstado.setForeground(colorDe(r.getEstadoPago()));
        txtMensaje.setText(r.getMensaje());
        lblReferencia.setText(r.getReferencia() == null ? " " : "Referencia: " + r.getReferencia());

        modeloTiquetes.clear();
        for (String t : r.getTiquetes()) modeloTiquetes.addElement(t);

        if (r.isReservaConfirmada()) {
            lblCorreo.setText(r.isCorreoEnviado()
                    ? "Se envió la confirmación al correo registrado."
                    : "No se pudo enviar el correo (el comprador no tiene correo válido).");
        } else {
            lblCorreo.setText(" ");
        }
        btnReintentar.setVisible(r.isPuedeReintentar());
    }

    private Color colorDe(String estado) {
        if (Pago.APROBADO.equals(estado)) return new Color(0, 120, 0);
        if (Pago.PENDIENTE.equals(estado)) return new Color(200, 120, 0);
        return new Color(180, 0, 0);
    }

    public void addReintentarListener(ActionListener l) { btnReintentar.addActionListener(l); }
    public void addFinalizarListener(ActionListener l) { btnFinalizar.addActionListener(l); }

    public void addCierreListener(Runnable accion) {
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { accion.run(); }
        });
    }
}
