import com.formdev.flatlaf.FlatLightLaf;
import controlador.menu.MenuPrincipalController;
import java.sql.Connection;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import modelo.ConexionDB;
import vista.menu.MenuPrincipalFrame;

/**
 * Punto de entrada unico del sistema Aerolinea MVP.
 */
public class App {
    
    public static void main(String[] args) {
        // 1. Aplicar el Look and Feel "FlatLaf" en su version clara ANTES de instanciar Swing
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("No se pudo inicializar FlatLaf.");
        }

        SwingUtilities.invokeLater(() -> {
            
            // 2. Verificar la Base de Datos antes de arrancar
            try (Connection con = ConexionDB.conectar()) {
                if (con == null) {
                    JOptionPane.showMessageDialog(null, 
                            "No se pudo establecer conexion con la base de datos (aerolinea_app_db).\n"
                            + "Por favor, verifique que MySQL/XAMPP este encendido y vuelva a intentar.", 
                            "Error Critico de Conexion", JOptionPane.ERROR_MESSAGE);
                    System.exit(1); 
                }
            } catch (Exception e) {
                 JOptionPane.showMessageDialog(null, "Error al probar conexion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                 System.exit(1);
            }
            
            // 3. Iniciar el Menu Principal
            MenuPrincipalFrame menuVista = new MenuPrincipalFrame();
            new MenuPrincipalController(menuVista).irAlMenu();
        });
    }
}