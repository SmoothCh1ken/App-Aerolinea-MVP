package vista.modulo_seguridad;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Panel de seguridad del funcionario: sesión actual y accesos según su rol (RF-27). */
public class PanelSeguridadFrame extends JFrame {

    private final JLabel lblSesion = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel lblRol = new JLabel(" ", SwingConstants.CENTER);
    private final JButton btnUsuarios = new JButton("Gestión de usuarios");
    private final JButton btnAuditoria = new JButton("Auditoría e incidentes");
    private final JButton btnClave = new JButton("Cambiar mi contraseña");
    private final JButton btnCerrarSesion = new JButton("Cerrar sesión");
    private final JButton btnVolver = new JButton("Volver al menú");

    public PanelSeguridadFrame() {
        super("Seguridad y Usuarios");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(420, 390);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(8, 8));

        lblSesion.setFont(new Font("Segoe UI", Font.BOLD, 17));
        JPanel norte = new JPanel(new GridLayout(2, 1));
        norte.setBorder(BorderFactory.createEmptyBorder(16, 10, 4, 10));
        norte.add(lblSesion);
        norte.add(lblRol);
        add(norte, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(5, 1, 10, 10));
        centro.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));
        centro.add(btnUsuarios);
        centro.add(btnAuditoria);
        centro.add(btnClave);
        centro.add(btnCerrarSesion);
        centro.add(btnVolver);
        add(centro, BorderLayout.CENTER);
    }

    // ---------- API para el controlador ----------

    public void setSesion(String nombre, String rol) {
        lblSesion.setText(nombre);
        lblRol.setText("Rol: " + rol);
    }

    /** Habilita solo lo que el rol del funcionario permite. */
    public void setAccesos(boolean usuarios, boolean auditoria) {
        btnUsuarios.setEnabled(usuarios);
        btnAuditoria.setEnabled(auditoria);
    }

    public void addUsuariosListener(ActionListener l) { btnUsuarios.addActionListener(l); }
    public void addAuditoriaListener(ActionListener l) { btnAuditoria.addActionListener(l); }
    public void addClaveListener(ActionListener l) { btnClave.addActionListener(l); }
    public void addCerrarSesionListener(ActionListener l) { btnCerrarSesion.addActionListener(l); }
    public void addVolverListener(ActionListener l) { btnVolver.addActionListener(l); }

    public void addCierreListener(Runnable accion) {
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { accion.run(); }
        });
    }

    public boolean confirmar(String texto) {
        return JOptionPane.showConfirmDialog(this, texto, "Confirmar", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    public void mostrarInfo(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
