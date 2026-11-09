package vista.modulo_seguridad;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import modelo.modulo_seguridad.Rol;
import modelo.modulo_seguridad.Usuario;

/** Formulario para crear una cuenta (todos los campos) o editar el cargo y el rol de una existente. */
public class UsuarioFormDialog extends JDialog {

    private final JTextField txtUsuario = new JTextField(20);
    private final JTextField txtNombres = new JTextField(20);
    private final JTextField txtApellidos = new JTextField(20);
    private final JTextField txtDocumento = new JTextField(20);
    private final JTextField txtCorreo = new JTextField(20);
    private final JTextField txtCargo = new JTextField(20);
    private final JComboBox<Rol> cmbRol = new JComboBox<>();
    private final JPasswordField txtClave = new JPasswordField(20);
    private final JPasswordField txtConfirmar = new JPasswordField(20);
    private final boolean nuevo;
    private boolean aceptado;

    public UsuarioFormDialog(Window propietario, boolean nuevo, List<Rol> roles) {
        super(propietario, nuevo ? "Nuevo funcionario" : "Editar rol y cargo", Dialog.ModalityType.APPLICATION_MODAL);
        this.nuevo = nuevo;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(6, 6));
        for (Rol r : roles) cmbRol.addItem(r);

        JPanel campos = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 10, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        int f = 0;
        fila(campos, c, f++, "Usuario (institucional):", txtUsuario);
        fila(campos, c, f++, "Nombres:", txtNombres);
        fila(campos, c, f++, "Apellidos:", txtApellidos);
        fila(campos, c, f++, "N.º de documento:", txtDocumento);
        fila(campos, c, f++, "Correo institucional:", txtCorreo);
        fila(campos, c, f++, "Cargo:", txtCargo);
        fila(campos, c, f++, "Rol:", cmbRol);
        if (nuevo) {
            fila(campos, c, f++, "Contraseña inicial:", txtClave);
            fila(campos, c, f++, "Confirmar contraseña:", txtConfirmar);
        } else {
            // al editar solo se cambian el cargo y el rol
            for (JComponent bloqueado : new JComponent[] {txtUsuario, txtNombres, txtApellidos, txtDocumento, txtCorreo}) {
                bloqueado.setEnabled(false);
            }
        }
        add(campos, BorderLayout.CENTER);

        JButton btnAceptar = new JButton(nuevo ? "Crear cuenta" : "Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnAceptar.addActionListener(e -> { aceptado = true; setVisible(false); });
        btnCancelar.addActionListener(e -> { aceptado = false; setVisible(false); });
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCancelar);
        botones.add(btnAceptar);
        add(botones, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(propietario);
    }

    private void fila(JPanel p, GridBagConstraints c, int fila, String etiqueta, JComponent campo) {
        c.gridy = fila; c.gridx = 0;
        p.add(new JLabel(etiqueta), c);
        c.gridx = 1;
        p.add(campo, c);
    }

    // ---------- API para el controlador ----------

    public boolean isNuevo() { return nuevo; }

    /** Muestra el formulario y espera. @return true si se pulsó el botón principal. */
    public boolean pedir() {
        aceptado = false;
        setVisible(true);
        return aceptado;
    }

    public void cargar(Usuario u) {
        txtUsuario.setText(u.getUsuario());
        txtNombres.setText(u.getNombres());
        txtApellidos.setText(u.getApellidos());
        txtDocumento.setText(u.getDocumento());
        txtCorreo.setText(u.getCorreo());
        txtCargo.setText(u.getCargo());
        for (int i = 0; i < cmbRol.getItemCount(); i++) {
            if (cmbRol.getItemAt(i).getId() == u.getIdRol()) {
                cmbRol.setSelectedIndex(i);
            }
        }
    }

    public String getUsuario() { return txtUsuario.getText(); }
    public String getNombres() { return txtNombres.getText(); }
    public String getApellidos() { return txtApellidos.getText(); }
    public String getDocumento() { return txtDocumento.getText(); }
    public String getCorreo() { return txtCorreo.getText(); }
    public String getCargo() { return txtCargo.getText(); }
    public Rol getRol() { return (Rol) cmbRol.getSelectedItem(); }
    public char[] getClave() { return txtClave.getPassword(); }
    public char[] getConfirmacion() { return txtConfirmar.getPassword(); }

    public void limpiarClaves() {
        txtClave.setText("");
        txtConfirmar.setText("");
    }

    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
