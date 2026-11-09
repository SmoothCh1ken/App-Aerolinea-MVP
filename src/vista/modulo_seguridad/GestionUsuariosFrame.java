package vista.modulo_seguridad;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.ImageIcon;
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

    /** 
     * Muestra un secreto de segundo factor utilizando un Código QR y un campo de texto alternativo.
     * @param identificador Nombre de usuario (ej: "j.perez") para etiquetar el QR.
     */
    public void mostrarSecreto(String titulo, String explicacion, String secreto, String identificador) {
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Texto Explicativo superior
        JLabel lblExplicacion = new JLabel("<html><div style='text-align: center; width: 300px;'>" + explicacion + "</div></html>");
        lblExplicacion.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(lblExplicacion);
        panelPrincipal.add(Box.createVerticalStrut(15));

        // 2. Generación y renderizado del Código QR
        ImageIcon iconoQR = GeneradorQR.generarIconoQR(identificador, secreto);
        JLabel lblQR = new JLabel();
        if (iconoQR != null) {
            lblQR.setIcon(iconoQR);
        } else {
            lblQR.setText("[Error renderizando código QR]");
            lblQR.setForeground(java.awt.Color.RED);
        }
        lblQR.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(lblQR);
        panelPrincipal.add(Box.createVerticalStrut(15));

        // 3. Etiqueta intermedia
        JLabel lblAlternativa = new JLabel("O ingrese esta clave manualmente:");
        lblAlternativa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAlternativa.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(lblAlternativa);
        panelPrincipal.add(Box.createVerticalStrut(5));

        // 4. Campo de texto seleccionable (Fallback)
        JTextField campo = new JTextField(secreto);
        campo.setEditable(false);
        campo.setFont(new Font("Monospaced", Font.BOLD, 18));
        campo.setHorizontalAlignment(SwingConstants.CENTER);
        campo.setMaximumSize(new Dimension(300, 35)); // Evita que se estire en el BoxLayout
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(campo);

        JOptionPane.showMessageDialog(this, panelPrincipal, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

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