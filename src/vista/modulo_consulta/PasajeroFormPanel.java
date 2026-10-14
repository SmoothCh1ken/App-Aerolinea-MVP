package vista.modulo_consulta;

import com.toedter.calendar.JDateChooser;
import modelo.modulo_consulta.Ciudad;
import modelo.modulo_consulta.PasajeroDTO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel reutilizable que contiene el formulario de datos para un pasajero.
 * Actualizado para exigir Nacionalidad, Dirección y normalizar Tipos de Documento.
 */
public class PasajeroFormPanel extends JPanel {

    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JComboBox<String> cmbTipoDocumento;
    private JTextField txtNumeroDocumento;
    private JDateChooser dcFechaNacimiento;
    private JComboBox<Ciudad> cmbCiudadResidencia;
    private JComboBox<String> cmbNacionalidad;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtCorreo;

    private String categoriaPasajero;
    private boolean esAdulto;

    public PasajeroFormPanel(int numeroPasajero, String categoria) {
        this.categoriaPasajero = categoria;
        this.esAdulto = categoria.equals("Adulto");
        initComponents(numeroPasajero, categoria);
    }

    private void initComponents(int numeroPasajero, String categoria) {
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                " Pasajero " + numeroPasajero + " - " + categoria + " ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(30, 60, 90)
        ));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // --- Fila 1: Nombres y Apellidos ---
        gbc.gridy = 0;
        gbc.gridx = 0; add(new JLabel("Nombres: *"), gbc);
        txtNombres = new JTextField();
        gbc.gridx = 1; add(txtNombres, gbc);

        gbc.gridx = 2; add(new JLabel("Apellidos: *"), gbc);
        txtApellidos = new JTextField();
        gbc.gridx = 3; add(txtApellidos, gbc);

        // --- Fila 2: Documento ---
        gbc.gridy = 1;
        gbc.gridx = 0; add(new JLabel("Tipo Documento: *"), gbc);
        // Tipos normalizados según aerolinea_app_db.sql
        cmbTipoDocumento = new JComboBox<>(new String[]{"CC", "TI", "RC", "Pasaporte", "CE", "DNI"});
        if (categoria.equals("Niño") || categoria.equals("Bebé")) {
            cmbTipoDocumento.setSelectedItem("RC");
        }
        gbc.gridx = 1; add(cmbTipoDocumento, gbc);

        gbc.gridx = 2; add(new JLabel("N° Documento: *"), gbc);
        txtNumeroDocumento = new JTextField();
        gbc.gridx = 3; add(txtNumeroDocumento, gbc);

        // --- Fila 3: Nacimiento y Nacionalidad ---
        gbc.gridy = 2;
        gbc.gridx = 0; add(new JLabel("Fecha Nacimiento: *"), gbc);
        dcFechaNacimiento = new JDateChooser();
        dcFechaNacimiento.setDateFormatString("yyyy-MM-dd");
        gbc.gridx = 1; add(dcFechaNacimiento, gbc);

        gbc.gridx = 2; add(new JLabel("Nacionalidad: *"), gbc);
        String[] nacionalidades = {"Colombiana", "Estadounidense", "Española", "Peruana", "Argentina", "Mexicana", "Otra"};
        cmbNacionalidad = new JComboBox<>(nacionalidades);
        gbc.gridx = 3; add(cmbNacionalidad, gbc);

        // --- Fila 4: Residencia y Dirección ---
        gbc.gridy = 3;
        gbc.gridx = 0; add(new JLabel("Ciudad Residencia: *"), gbc);
        cmbCiudadResidencia = new JComboBox<>();
        gbc.gridx = 1; add(cmbCiudadResidencia, gbc);

        String reqContacto = esAdulto ? " *" : " (Opc)";
        gbc.gridx = 2; add(new JLabel("Dirección:" + reqContacto), gbc);
        txtDireccion = new JTextField();
        gbc.gridx = 3; add(txtDireccion, gbc);

        // --- Fila 5: Contacto ---
        gbc.gridy = 4;
        gbc.gridx = 0; add(new JLabel("Teléfono:" + reqContacto), gbc);
        txtTelefono = new JTextField();
        gbc.gridx = 1; add(txtTelefono, gbc);

        gbc.gridx = 2; add(new JLabel("Correo Electrónico:" + reqContacto), gbc);
        txtCorreo = new JTextField();
        gbc.gridx = 3; add(txtCorreo, gbc);
    }

    public void cargarCiudades(List<Ciudad> ciudades) {
        cmbCiudadResidencia.removeAllItems();
        for (Ciudad ciudad : ciudades) {
            cmbCiudadResidencia.addItem(ciudad);
        }
    }

    /**
     * Valida y empaqueta la información. Si es adulto, exige dirección, teléfono y correo.
     */
    public PasajeroDTO obtenerDatosSiValido() {
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String numDoc = txtNumeroDocumento.getText().trim();
        java.util.Date fechaNac = dcFechaNacimiento.getDate();
        String direccion = txtDireccion.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        // Validación básica global
        if (nombres.isEmpty() || apellidos.isEmpty() || numDoc.isEmpty() || fechaNac == null) {
            mostrarError("Por favor complete los nombres, apellidos, documento y fecha de nacimiento.");
            return null;
        }

        // Validación estricta para Adultos (Potenciales compradores)
        if (esAdulto) {
            if (direccion.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
                mostrarError("La Dirección, Teléfono y Correo son obligatorios para los pasajeros Adultos.");
                return null;
            }
        }

        PasajeroDTO dto = new PasajeroDTO();
        dto.setNombres(nombres);
        dto.setApellidos(apellidos);
        dto.setTipoDocumento((String) cmbTipoDocumento.getSelectedItem());
        dto.setNumeroDocumento(numDoc);
        dto.setFechaNacimiento(fechaNac);
        dto.setCategoria(categoriaPasajero);
        dto.setNacionalidad((String) cmbNacionalidad.getSelectedItem());
        dto.setCiudadResidencia((Ciudad) cmbCiudadResidencia.getSelectedItem());
        dto.setDireccion(direccion);
        dto.setTelefono(telefono);
        dto.setCorreo(correo);

        return dto;
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Datos Incompletos", JOptionPane.WARNING_MESSAGE);
    }
}