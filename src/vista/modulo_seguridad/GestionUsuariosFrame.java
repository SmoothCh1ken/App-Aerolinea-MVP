package vista.modulo_seguridad;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modelo.modulo_seguridad.Usuario;

/** Pantalla de administración de cuentas institucionales y roles (RF-26 y RF-27). */
public class GestionUsuariosFrame extends JFrame {

    private static final String[] COLUMNAS = {
        "Usuario", "Nombre", "Documento", "Correo institucional", "Cargo", "Rol", "Estado", "Intentos", "Último acceso"
    };

    private final DefaultTableModel modelo = new DefaultTableModel(COLUMNAS, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    private final JButton btnNuevo = new JButton("Nuevo funcionario");
    private final JButton btnEditar = new JButton("Editar rol y cargo");
    private final JButton btnEstado = new JButton("Activar / Desactivar");
    private final JButton btnClave = new JButton("Restablecer contraseña");
    private final JButton btnMfa = new JButton("Restablecer 2.º factor");
    private final JButton btnDesbloquear = new JButton("Desbloquear");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnCerrar = new JButton("Cerrar");

    public GestionUsuariosFrame() {
        super("Gestión de usuarios y roles");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1150, 520);
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout(8, 8));
        principal.setBorder(new EmptyBorder(10, 12, 10, 12));
        setContentPane(principal);

        tabla.setRowHeight(24);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Cuentas institucionales (una por funcionario)"));
        principal.add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnEstado);
        botones.add(btnClave);
        botones.add(btnMfa);
        botones.add(btnDesbloquear);
        botones.add(btnActualizar);
        botones.add(btnCerrar);
        principal.add(botones, BorderLayout.SOUTH);

        btnCerrar.addActionListener(e -> dispose());
    }

    // ---------- API para el controlador ----------

    public void mostrarUsuarios(List<Usuario> usuarios) {
        modelo.setRowCount(0);
        for (Usuario u : usuarios) {
            modelo.addRow(new Object[] {
                u.getUsuario(), u.getNombreCompleto(), u.getDocumento(), u.getCorreo(), u.getCargo(),
                u.getRolNombre(), u.getEstado(), u.getIntentosFallidos(),
                u.getUltimoAcceso() == null ? "-" : u.getUltimoAcceso()
            });
        }
    }

    /** @return la posición de la fila elegida, o -1 si no hay ninguna. */
    public int getFilaSeleccionada() { return tabla.getSelectedRow(); }

    public void addNuevoListener(ActionListener l) { btnNuevo.addActionListener(l); }
    public void addEditarListener(ActionListener l) { btnEditar.addActionListener(l); }
    public void addEstadoListener(ActionListener l) { btnEstado.addActionListener(l); }
    public void addClaveListener(ActionListener l) { btnClave.addActionListener(l); }
    public void addMfaListener(ActionListener l) { btnMfa.addActionListener(l); }
    public void addDesbloquearListener(ActionListener l) { btnDesbloquear.addActionListener(l); }
    public void addActualizarListener(ActionListener l) { btnActualizar.addActionListener(l); }

    public boolean confirmar(String texto) {
        return JOptionPane.showConfirmDialog(this, texto, "Confirmar", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    public void mostrarInfo(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarAdvertencia(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Muestra un secreto de segundo factor en un campo que se puede seleccionar y copiar. */
    public void mostrarSecreto(String titulo, String explicacion, String secreto) {
        javax.swing.JTextField campo = new javax.swing.JTextField(secreto);
        campo.setEditable(false);
        campo.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 18));
        campo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(new javax.swing.JLabel("<html>" + explicacion + "</html>"), BorderLayout.NORTH);
        panel.add(campo, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(this, panel, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    /** Pide una contraseña nueva dos veces. @return la clave, o null si se cancela o no coinciden. */
    public char[] pedirClaveNueva(String titulo) {
        javax.swing.JPasswordField a = new javax.swing.JPasswordField(18);
        javax.swing.JPasswordField b = new javax.swing.JPasswordField(18);
        JPanel panel = new JPanel(new java.awt.GridLayout(4, 1, 4, 4));
        panel.add(new javax.swing.JLabel("Contraseña nueva:"));
        panel.add(a);
        panel.add(new javax.swing.JLabel("Confirmar contraseña:"));
        panel.add(b);
        if (JOptionPane.showConfirmDialog(this, panel, titulo, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return null;
        }
        char[] primera = a.getPassword();
        if (!java.util.Arrays.equals(primera, b.getPassword())) {
            java.util.Arrays.fill(primera, '\0');
            mostrarAdvertencia("Las contraseñas no coinciden.");
            return null;
        }
        return primera;
    }
}
