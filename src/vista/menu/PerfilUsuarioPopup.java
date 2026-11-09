package vista.menu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Menú emergente (Popup) que muestra la información del usuario actual
 * y el botón para cerrar sesión. Se ancla debajo del botón de perfil.
 */
public class PerfilUsuarioPopup extends JPopupMenu {

    private final JLabel lblNombre = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblCorreo = new JLabel("", SwingConstants.CENTER);
    private final JButton btnCerrarSesion = new JButton("Cerrar sesión");

    public PerfilUsuarioPopup() {
        // Estilo del contenedor del popup
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBorder(new EmptyBorder(15, 20, 15, 20));
        contenedor.setBackground(Color.WHITE);

        // Configuración de las etiquetas de texto
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setForeground(new Color(50, 50, 50));
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCorreo.setForeground(new Color(120, 120, 120));
        lblCorreo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Configuración del botón de cierre de sesión
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCerrarSesion.setBackground(new Color(210, 40, 40)); // Rojo destacado
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ensamblado
        contenedor.add(lblNombre);
        contenedor.add(Box.createVerticalStrut(5));
        contenedor.add(lblCorreo);
        contenedor.add(Box.createVerticalStrut(15));
        contenedor.add(btnCerrarSesion);

        add(contenedor);
    }

    /**
     * Carga los datos del usuario logueado en el popup.
     */
    public void setDatosUsuario(String nombreCompleto, String correo) {
        lblNombre.setText(nombreCompleto);
        lblCorreo.setText(correo);
    }

    /**
     * Muestra el popup justo debajo del componente especificado (el botón de perfil).
     */
    public void mostrarDebajoDe(Component componente) {
        // Calcula la posición X para que el popup se alinee a la derecha del botón
        int x = componente.getWidth() - this.getPreferredSize().width;
        int y = componente.getHeight() + 2; // Un par de píxeles por debajo del botón
        
        // Evita que el popup se salga por la izquierda si es muy ancho
        if (x < 0) x = 0; 
        
        show(componente, x, y);
    }

    // ---------- API PARA EL CONTROLADOR ----------

    public void addCerrarSesionListener(ActionListener l) {
        // Al hacer clic, ocultamos el popup automáticamente además de notificar al controlador
        btnCerrarSesion.addActionListener(e -> {
            setVisible(false);
            l.actionPerformed(e);
        });
    }
}