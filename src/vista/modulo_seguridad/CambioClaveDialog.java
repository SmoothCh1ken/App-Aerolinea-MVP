package vista.modulo_seguridad;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/** Diálogo para que el funcionario cambie su propia contraseña. */
public class CambioClaveDialog extends JDialog {

    private final JPasswordField txtActual = new JPasswordField(18);
    private final JPasswordField txtNueva = new JPasswordField(18);
    private final JPasswordField txtConfirmar = new JPasswordField(18);
    private boolean aceptado;

    public CambioClaveDialog(Window propietario) {
        super(propietario, "Cambiar mi contraseña", Dialog.ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(6, 6));

        JPanel campos = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 10, 6, 10);
        c.anchor = GridBagConstraints.WEST;
        fila(campos, c, 0, "Contraseña actual:", txtActual);
        fila(campos, c, 1, "Contraseña nueva:", txtNueva);
        fila(campos, c, 2, "Confirmar nueva:", txtConfirmar);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        campos.add(new JLabel("<html><small>Mínimo 8 caracteres, con letras y números.</small></html>"), c);
        add(campos, BorderLayout.CENTER);

        JButton btnAceptar = new JButton("Cambiar contraseña");
        JButton btnCancelar = new JButton("Cancelar");
        btnAceptar.addActionListener(e -> { aceptado = true; setVisible(false); });
        btnCancelar.addActionListener(e -> { aceptado = false; setVisible(false); });
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCancelar);
        botones.add(btnAceptar);
        add(botones, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnAceptar);

        pack();
        setLocationRelativeTo(propietario);
    }

    private void fila(JPanel p, GridBagConstraints c, int fila, String etiqueta, JPasswordField campo) {
        c.gridy = fila; c.gridx = 0; c.gridwidth = 1;
        p.add(new JLabel(etiqueta), c);
        c.gridx = 1;
        p.add(campo, c);
    }

    /** Muestra el diálogo y espera. @return true si el usuario pulsó "Cambiar contraseña". */
    public boolean pedir() {
        aceptado = false;
        txtActual.setText("");
        txtNueva.setText("");
        txtConfirmar.setText("");
        setVisible(true);
        return aceptado;
    }

    public char[] getActual() { return txtActual.getPassword(); }
    public char[] getNueva() { return txtNueva.getPassword(); }
    public char[] getConfirmacion() { return txtConfirmar.getPassword(); }

    public void limpiar() {
        txtActual.setText("");
        txtNueva.setText("");
        txtConfirmar.setText("");
    }

    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
