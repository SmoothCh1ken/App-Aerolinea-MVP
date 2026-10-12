package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // Configuración para MySQL en XAMPP
    private static final String URL = "jdbc:mysql://localhost:3306/aerolinea_app_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Por defecto en XAMPP va vacío

    /**
     * Obtiene una conexión activa a la base de datos
     * @return Connection objeto de conexión
     */
    public static Connection conectar() {
        Connection conexion = null;
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establecer la conexión
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver de MySQL no encontrado. Verifica las Librerías.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
        return conexion;
    }

    // Método main rápido para probar la conexión
    public static void main(String[] args) {
        Connection con = ConexionDB.conectar();
        if (con != null) {
            System.out.println("¡CONEXIÓN EXITOSA A LA BASE DE DATOS aerolinea_app_db!");
            try {
                con.close(); // Cerramos la prueba
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Fallo al establecer la conexión.");
        }
    }
}