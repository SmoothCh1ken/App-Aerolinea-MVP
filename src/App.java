import controlador.menu.MenuPrincipalController;
import java.sql.Connection;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import modelo.ConexionDB;
import vista.menu.MenuPrincipalFrame;

/**
 * Punto de entrada único del sistema Aerolínea MVP.
 */
public class App {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            // 1. Aplicar el Look and Feel "Nimbus" (Diseño moderno que respeta los colores)
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("No se pudo aplicar el Look and Feel Nimbus.");
            }
            
            // 2. Verificar la Base de Datos antes de arrancar (Soluciona el problema de NullPointerException)
            try (Connection con = ConexionDB.conectar()) {
                if (con == null) {
                    JOptionPane.showMessageDialog(null, 
                            "No se pudo establecer conexión con la base de datos (aerolinea_app_db).\n"
                            + "Por favor, verifique que MySQL/XAMPP esté encendido y vuelva a intentar.", 
                            "Error Crítico de Conexión", JOptionPane.ERROR_MESSAGE);
                    System.exit(1); // Cierra la app con error
                }
            } catch (Exception e) {
                 JOptionPane.showMessageDialog(null, "Error al probar conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                 System.exit(1);
            }
            
            // 3. Iniciar el Menú Principal (irAlMenu() también libera las reservas expiradas, CA-24)
            MenuPrincipalFrame menuVista = new MenuPrincipalFrame();
            new MenuPrincipalController(menuVista).irAlMenu();
        });
    }
}