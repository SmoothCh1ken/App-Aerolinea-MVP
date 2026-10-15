package controlador.modulo_consulta;

import modelo.modulo_consulta.*;
import vista.modulo_consulta.BuscarYSeleccionarVueloFrame;
import vista.modulo_consulta.PasajeroFormPanel;
import vista.modulo_consulta.RegistroPasajerosFrame;
import controlador.menu.Navegador;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vista.modulo_consulta.ConfirmacionReservaFrame;

/**
 * Controlador para el registro de pasajeros (RF-06) y comprador (RF-07).
 * Actualizado para gestionar Nacionalidad y Ciudad de Residencia estrictas.
 */
public class RegistroPasajerosController implements ActionListener {

    private final RegistroPasajerosFrame vista;
    private final VueloDAO modeloDAO;
    
    private final VueloDTO vueloIda;
    private final VueloDTO vueloRegreso;
    private final int cantidadAdultos;
    private final int cantidadNinos;
    private final int cantidadBebes;

    private final List<PasajeroFormPanel> listaPanelesPasajeros;
    private final List<Ciudad> listaCiudades;
    private final DecimalFormat formatoMoneda;
    private final Map<Integer, BigDecimal> factoresCategoria; // id_categoria -> factor de tarifa
    private Navegador navegador;

    public RegistroPasajerosController(
            RegistroPasajerosFrame vista, 
            VueloDAO modeloDAO, 
            VueloDTO vueloIda, 
            VueloDTO vueloRegreso, 
            int adultos, int ninos, int bebes,
            Navegador navegador
    ) {
        this.vista = vista;
        this.modeloDAO = modeloDAO;
        this.vueloIda = vueloIda;
        this.vueloRegreso = vueloRegreso;
        this.cantidadAdultos = adultos;
        this.cantidadNinos = ninos;
        this.cantidadBebes = bebes;
        this.navegador = navegador;

        this.listaPanelesPasajeros = new ArrayList<>();
        this.listaCiudades = modeloDAO.obtenerTodasLasCiudades();
        this.factoresCategoria = modeloDAO.obtenerFactoresCategoria();
        this.formatoMoneda = new DecimalFormat("$ #,##0.00");

        inicializarVista();
        configurarEventos();
    }

    private void inicializarVista() {
        mostrarResumenItinerario();
        cargarCiudadesComprador();
        generarFormulariosPasajeros();
        gestionarEstadoComprador();
        vista.setVisible(true);
    }

    /** Lo que paga UN pasajero de esa categoría: ida (+ regreso) con factor e impuestos. */
    private BigDecimal totalPorCategoria(int idCategoria) {
        BigDecimal factor = factoresCategoria.getOrDefault(idCategoria, BigDecimal.ONE);
        BigDecimal total = CalculadoraTarifa.totalTramo(vueloIda, factor);
        if (vueloRegreso != null) {
            total = total.add(CalculadoraTarifa.totalTramo(vueloRegreso, factor));
        }
        return total;
    }

    private void mostrarResumenItinerario() {
        BigDecimal totalEstimado = totalPorCategoria(CalculadoraTarifa.ID_ADULTO).multiply(BigDecimal.valueOf(cantidadAdultos))
                .add(totalPorCategoria(CalculadoraTarifa.ID_NINO).multiply(BigDecimal.valueOf(cantidadNinos)))
                .add(totalPorCategoria(CalculadoraTarifa.ID_BEBE).multiply(BigDecimal.valueOf(cantidadBebes)));

        StringBuilder resumen = new StringBuilder("<html>");
        resumen.append("<b>Itinerario de Ida:</b> ").append(vueloIda.getNumeroVuelo())
               .append(" (").append(vueloIda.getAeropuertoOrigen()).append(" &rarr; ").append(vueloIda.getAeropuertoDestino()).append(")");
        
        if (vueloRegreso != null) {
            resumen.append(" | <b>Regreso:</b> ").append(vueloRegreso.getNumeroVuelo())
                   .append(" (").append(vueloRegreso.getAeropuertoOrigen()).append(" &rarr; ").append(vueloRegreso.getAeropuertoDestino()).append(")");
        }
        
        resumen.append("<br><b>Pasajeros:</b> ").append(cantidadAdultos).append(" Adulto(s), ")
               .append(cantidadNinos).append(" Niño(s), ").append(cantidadBebes).append(" Bebé(s)");
        resumen.append(" | <b>Total Estimado (con impuestos):</b> <font color='green'>").append(formatoMoneda.format(totalEstimado)).append("</font>");
        resumen.append("</html>");

        vista.getLblResumenItinerario().setText(resumen.toString());
    }

    private void cargarCiudadesComprador() {
        vista.getCmbCompCiudadResidencia().removeAllItems();
        for (Ciudad ciudad : listaCiudades) {
            vista.getCmbCompCiudadResidencia().addItem(ciudad);
        }
    }

    private void generarFormulariosPasajeros() {
        JPanel contenedor = vista.getPanelContenedorPasajeros();
        contenedor.removeAll();
        listaPanelesPasajeros.clear();
        vista.getCmbPasajeroComprador().removeAllItems();

        int contadorGlobal = 1;

        // 1. Adultos (Únicos que pueden ser seleccionados como compradores)
        for (int i = 1; i <= cantidadAdultos; i++) {
            PasajeroFormPanel panel = new PasajeroFormPanel(contadorGlobal, "Adulto");
            panel.cargarCiudades(listaCiudades);
            listaPanelesPasajeros.add(panel);
            contenedor.add(panel);
            contenedor.add(Box.createVerticalStrut(10));
            
            vista.getCmbPasajeroComprador().addItem("Pasajero " + contadorGlobal + " - Adulto " + i);
            contadorGlobal++;
        }

        // 2. Niños
        for (int i = 1; i <= cantidadNinos; i++) {
            PasajeroFormPanel panel = new PasajeroFormPanel(contadorGlobal, "Niño");
            panel.cargarCiudades(listaCiudades);
            listaPanelesPasajeros.add(panel);
            contenedor.add(panel);
            contenedor.add(Box.createVerticalStrut(10));
            contadorGlobal++;
        }

        // 3. Bebés
        for (int i = 1; i <= cantidadBebes; i++) {
            PasajeroFormPanel panel = new PasajeroFormPanel(contadorGlobal, "Bebé");
            panel.cargarCiudades(listaCiudades);
            listaPanelesPasajeros.add(panel);
            contenedor.add(panel);
            contenedor.add(Box.createVerticalStrut(10));
            contadorGlobal++;
        }

        contenedor.revalidate();
        contenedor.repaint();
    }

    private void configurarEventos() {
        vista.getChkCompradorEsPasajero().addActionListener(e -> gestionarEstadoComprador());
        vista.getBtnAtras().addActionListener(this);
        vista.getBtnContinuar().addActionListener(this);
        vista.addCierreListener(this::cancelarYVolverAlMenu);
    }

    private void gestionarEstadoComprador() {
        boolean esPasajero = vista.getChkCompradorEsPasajero().isSelected();
        vista.getCmbPasajeroComprador().setEnabled(esPasajero);

        boolean habilitarManuales = !esPasajero;
        vista.getTxtCompNombres().setEnabled(habilitarManuales);
        vista.getTxtCompApellidos().setEnabled(habilitarManuales);
        vista.getCmbCompTipoDoc().setEnabled(habilitarManuales);
        vista.getTxtCompNumDoc().setEnabled(habilitarManuales);
        vista.getCmbCompNacionalidad().setEnabled(habilitarManuales);
        vista.getCmbCompCiudadResidencia().setEnabled(habilitarManuales);
        vista.getTxtCompTelefono().setEnabled(habilitarManuales);
        vista.getTxtCompCorreo().setEnabled(habilitarManuales);
        vista.getTxtCompDireccion().setEnabled(habilitarManuales);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnAtras()) {
            volverASeleccionVuelos();
        } else if (e.getSource() == vista.getBtnContinuar()) {
            procesarRegistroReserva();
        }
    }

    /** La X de la ventana: abandona el registro y regresa al menú principal. */
    private void cancelarYVolverAlMenu() {
        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Desea cancelar el registro y volver al menú principal? Se perderán los datos ingresados.",
                "Volver al menú", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            vista.dispose();
            navegador.irAlMenu();
        }
    }

    private void volverASeleccionVuelos() {
        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Desea regresar a la selección de vuelos? Se perderán los datos ingresados en este formulario.",
                "Volver a Búsqueda", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            vista.dispose();
            navegador.iniciarReserva(); // El navegador se encarga de reabrir la Vista 1
        }
    }

    private void procesarRegistroReserva() {
        List<PasajeroDTO> pasajerosProcesados = new ArrayList<>();

        // 1. Extraer y validar datos de cada pasajero
        for (PasajeroFormPanel panel : listaPanelesPasajeros) {
            PasajeroDTO dto = panel.obtenerDatosSiValido();
            if (dto == null) {
                return; // Validación fallida en algún panel
            }
            pasajerosProcesados.add(dto);
        }

        // 2. Extraer y estructurar datos del comprador (RF-07)
        CompradorDTO compradorProcesado = new CompradorDTO();

        if (vista.getChkCompradorEsPasajero().isSelected()) {
            int indexSeleccionado = vista.getCmbPasajeroComprador().getSelectedIndex();
            if (indexSeleccionado >= 0 && indexSeleccionado < pasajerosProcesados.size()) {
                PasajeroDTO pasajeroElegido = pasajerosProcesados.get(indexSeleccionado);
                
                compradorProcesado.setNombres(pasajeroElegido.getNombres());
                compradorProcesado.setApellidos(pasajeroElegido.getApellidos());
                compradorProcesado.setTipoDocumento(pasajeroElegido.getTipoDocumento());
                compradorProcesado.setNumeroDocumento(pasajeroElegido.getNumeroDocumento());
                compradorProcesado.setNacionalidad(pasajeroElegido.getNacionalidad());
                compradorProcesado.setCiudadResidencia(pasajeroElegido.getCiudadResidencia());
                compradorProcesado.setDireccion(pasajeroElegido.getDireccion());
                compradorProcesado.setTelefono(pasajeroElegido.getTelefono());
                compradorProcesado.setCorreo(pasajeroElegido.getCorreo());
            }
        } else {
            // Comprador externo manual
            String nombres = vista.getTxtCompNombres().getText().trim();
            String apellidos = vista.getTxtCompApellidos().getText().trim();
            String numDoc = vista.getTxtCompNumDoc().getText().trim();
            String direccion = vista.getTxtCompDireccion().getText().trim();
            String telefono = vista.getTxtCompTelefono().getText().trim();
            String correo = vista.getTxtCompCorreo().getText().trim();

            if (nombres.isEmpty() || apellidos.isEmpty() || numDoc.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
                JOptionPane.showMessageDialog(vista, 
                        "Complete todos los datos obligatorios del Comprador (Nombres, Apellidos, Documento, Dirección, Teléfono y Correo).",
                        "Datos Comprador Incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            compradorProcesado.setNombres(nombres);
            compradorProcesado.setApellidos(apellidos);
            compradorProcesado.setTipoDocumento((String) vista.getCmbCompTipoDoc().getSelectedItem());
            compradorProcesado.setNumeroDocumento(numDoc);
            compradorProcesado.setNacionalidad((String) vista.getCmbCompNacionalidad().getSelectedItem());
            compradorProcesado.setCiudadResidencia((Ciudad) vista.getCmbCompCiudadResidencia().getSelectedItem());
            compradorProcesado.setDireccion(direccion);
            compradorProcesado.setTelefono(telefono);
            compradorProcesado.setCorreo(correo);
        }

        // 3. Crear el paquete unificado ReservaDTO
        ReservaDTO reserva = new ReservaDTO();
        reserva.setVueloIda(vueloIda);
        reserva.setVueloRegreso(vueloRegreso);
        reserva.setListaPasajeros(pasajerosProcesados);
        reserva.setComprador(compradorProcesado);
        
        BigDecimal totalReserva = BigDecimal.ZERO;
        for (PasajeroDTO p : pasajerosProcesados) {
            totalReserva = totalReserva.add(totalPorCategoria(CalculadoraTarifa.idCategoria(p.getCategoria())));
        }
        reserva.setTotalPagar(totalReserva.doubleValue());

        // Transición definitiva hacia la Vista 3
        ConfirmacionReservaFrame vista3 = new ConfirmacionReservaFrame();
        ReservaDAO reservaDAO = new ReservaDAO();
        ConfirmacionReservaController confirmacionReservaController = new ConfirmacionReservaController(vista3, reservaDAO, reserva, navegador);
        vista.dispose(); // Cierra la Vista 2
    }
}