package vista.modulo_seguridad;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Ventana modal de inicio de sesión (RF-28), en dos pasos:
 * 1) usuario y contraseña, 2) código de 6 dígitos del segundo factor.
 */
public class LoginDialog extends JDialog {

    private static final String PASO_USUARIO = "USUARIO";
    private static final String PASO_MFA = "MFA";

    private final CardLayout pasos = new CardLayout();
    private final JPanel contenedor = new JPanel(pasos);

    private final JTextField txtUsuario = new JTextField(18);
    private final JPasswordField txtClave = new JPasswordField(18);
    private final JButton btnIngresar = new JButton("Continuar");

    private final JLabel lblSaludo = new JLabel(" ", SwingConstants.CENTER);
    private final JTextField txtCodigo = new JTextField(8);
    private final JButton btnVerificar = new JButton("Verificar e ingresar");
    private final JButton btnVolver = new JButton("Volver");

    private final JButton btnCancelar = new JButton("Cancelar");
    private final JLabel lblMensaje = new JLabel(" ", SwingConstants.CENTER);

    public LoginDialog(Window propietario) {
        super(propietario, "Acceso a módulos internos", Dialog.ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(420, 310);
        setLocationRelativeTo(propietario);
        setResizable(false);
        setLayout(new BorderLayout(6, 6));

        JLabel titulo = new JLabel("Ingreso de funcionarios", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(14, 0, 4, 0));
        add(titulo, BorderLayout.NORTH);

        contenedor.add(construirPasoUsuario(), PASO_USUARIO);
        contenedor.add(construirPasoMfa(), PASO_MFA);
        add(contenedor, BorderLayout.CENTER);

        lblMensaje.setForeground(new Color(180, 0, 0));
        lblMensaje.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        JPanel sur = new JPanel(new BorderLayout());
        sur.add(lblMensaje, BorderLayout.NORTH);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botones.add(btnCancelar);
        sur.add(botones, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);

        mostrarPasoUsuario();
    }

    private JPanel construirPasoUsuario() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0; c.gridy = 0; p.add(new JLabel("Usuario:"), c);
        c.gridx = 1; p.add(txtUsuario, c);
        c.gridx = 0; c.gridy = 1; p.add(new JLabel("Contraseña:"), c);
        c.gridx = 1; p.add(txtClave, c);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER;
        btnIngresar.setBackground(new Color(25, 115, 210));
        btnIngresar.setForeground(Color.WHITE);
        p.add(btnIngresar, c);
        return p;
    }

    private JPanel construirPasoMfa() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        lblSaludo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        p.add(lblSaludo, c);
        c.gridy = 1;
        p.add(new JLabel("<html><center>Ingrese el código de 6 dígitos de su<br>aplicación autenticadora.</center></html>",
                SwingConstants.CENTER), c);
        c.gridy = 2;
        txtCodigo.setHorizontalAlignment(SwingConstants.CENTER);
        txtCodigo.setFont(new Font("Monospaced", Font.BOLD, 22));
        p.add(txtCodigo, c);
        c.gridy = 3; c.gridwidth = 1;
        btnVerificar.setBackground(new Color(40, 160, 70));
        btnVerificar.setForeground(Color.WHITE);
        p.add(btnVolver, c);
        c.gridx = 1;
        p.add(btnVerificar, c);
        return p;
    }

    // ---------- API para el controlador ----------

    public String getUsuario() { return txtUsuario.getText().trim(); }

    /** El controlador debe borrar el arreglo cuando termine de usarlo. */
    public char[] getClave() { return txtClave.getPassword(); }

    public String getCodigo() { return txtCodigo.getText().trim(); }

    public void mostrarPasoUsuario() {
        pasos.show(contenedor, PASO_USUARIO);
        txtClave.setText("");
        txtCodigo.setText("");
        getRootPane().setDefaultButton(btnIngresar);
        txtUsuario.requestFocusInWindow();
    }

    public void mostrarPasoMfa(String nombre) {
        lblSaludo.setText("Hola, " + nombre);
        txtCodigo.setText("");
        pasos.show(contenedor, PASO_MFA);
        getRootPane().setDefaultButton(btnVerificar);
        txtCodigo.requestFocusInWindow();
    }

    public void limpiarClave() { txtClave.setText(""); }

    public void mostrarError(String texto) {
        lblMensaje.setText("<html><center>" + texto + "</center></html>");
    }

    public void limpiarMensaje() { lblMensaje.setText(" "); }

    public void addIngresarListener(ActionListener l) { btnIngresar.addActionListener(l); }
    public void addVerificarListener(ActionListener l) { btnVerificar.addActionListener(l); }
    public void addVolverListener(ActionListener l) { btnVolver.addActionListener(l); }
    public void addCancelarListener(ActionListener l) { btnCancelar.addActionListener(l); }

    /** La X de la ventana equivale a "Cancelar". */
    public void addCierreListener(Runnable accion) {
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { accion.run(); }
        });
    }
}
