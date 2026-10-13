package controlador.modulo_consulta;

import modelo.modulo_consulta.Ciudad;
import modelo.modulo_consulta.ClaseServicio;
import modelo.modulo_consulta.VueloDAO;
import modelo.modulo_consulta.VueloDTO;
import vista.modulo_consulta.BuscarYSeleccionarVueloFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Controlador principal para la ventana de Buscar y Seleccionar Vuelos.
 * Integra la captura anticipada de la fecha de regreso para la subconsulta EXISTS.
 */
public class BuscarYSeleccionarVueloController implements ActionListener {

    private final BuscarYSeleccionarVueloFrame vista;
    private final VueloDAO modeloDAO;
    private final SimpleDateFormat formatoFechaTabla;
    private final SimpleDateFormat formatoSql;
    private final DecimalFormat formatoMoneda;

    // Variables de Estado
    private boolean buscandoRegreso = false;
    private VueloDTO vueloIdaSeleccionado = null;
    private List<VueloDTO> ultimaListaVuelosConsultada = null;
    private int totalPasajerosCalculado = 1;

    public BuscarYSeleccionarVueloController(BuscarYSeleccionarVueloFrame vista, VueloDAO modeloDAO) {
        this.vista = vista;
        this.modeloDAO = modeloDAO;
        this.formatoFechaTabla = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.formatoSql = new SimpleDateFormat("yyyy-MM-dd");
        this.formatoMoneda = new DecimalFormat("$ #,##0.00");

        inicializarVista();
        configurarEventos();
    }

    private void inicializarVista() {
        cargarListasDesplegables();
        vista.getLblEstadoBusqueda().setText("Paso 1: Configure su búsqueda y presione Consultar.");
        vista.setVisible(true);
    }

    private void cargarListasDesplegables() {
        List<Ciudad> ciudades = modeloDAO.obtenerTodasLasCiudades();
        vista.getCmbCiudadOrigen().removeAllItems();
        vista.getCmbCiudadDestino().removeAllItems();
        for (Ciudad c : ciudades) {
            vista.getCmbCiudadOrigen().addItem(c);
            vista.getCmbCiudadDestino().addItem(c);
        }
        if (ciudades.size() > 1) vista.getCmbCiudadDestino().setSelectedIndex(1);

        List<ClaseServicio> clases = modeloDAO.obtenerClasesServicio();
        vista.getCmbClaseServicio().removeAllItems();
        for (ClaseServicio cs : clases) {
            vista.getCmbClaseServicio().addItem(cs);
        }
    }

    private void configurarEventos() {
        vista.getBtnConsultar().addActionListener(this);
        vista.getBtnSeleccionarVuelo().addActionListener(this);
        vista.getRbtnSoloIda().addActionListener(e -> gestionarTipoViaje());
        vista.getRbtnIdaYVuelta().addActionListener(e -> gestionarTipoViaje());
    }

    private void gestionarTipoViaje() {
        boolean esIdaYVuelta = vista.getRbtnIdaYVuelta().isSelected();
        vista.getDcFechaRegreso().setEnabled(esIdaYVuelta);
        if (!esIdaYVuelta) {
            vista.getDcFechaRegreso().setDate(null);
        }
        reiniciarEstadoBusqueda();
    }

    private void reiniciarEstadoBusqueda() {
        buscandoRegreso = false;
        vueloIdaSeleccionado = null;
        vista.getBtnSeleccionarVuelo().setText("Seleccionar Vuelo de Ida");
        vista.getLblEstadoBusqueda().setText("Paso 1: Configure su búsqueda y presione Consultar.");
        vista.getModeloTabla().setRowCount(0);
        vista.getBtnSeleccionarVuelo().setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnConsultar()) {
            if (buscandoRegreso) {
                int confirm = JOptionPane.showConfirmDialog(vista, "Si realiza una nueva consulta se perderá su selección de Ida. ¿Continuar?", "Nueva Búsqueda", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    reiniciarEstadoBusqueda();
                    ejecutarConsultaBusqueda();
                }
            } else {
                ejecutarConsultaBusqueda();
            }
        } else if (e.getSource() == vista.getBtnSeleccionarVuelo()) {
            procesarSeleccionVuelo();
        }
    }

    /**
     * Ejecuta la búsqueda inicial de IDA. Si el viaje es de IDA y VUELTA, 
     * valida y envía la fecha de regreso al DAO para garantizar opciones compatibles.
     */
    private void ejecutarConsultaBusqueda() {
        Ciudad origen = (Ciudad) vista.getCmbCiudadOrigen().getSelectedItem();
        Ciudad destino = (Ciudad) vista.getCmbCiudadDestino().getSelectedItem();
        ClaseServicio clase = (ClaseServicio) vista.getCmbClaseServicio().getSelectedItem();

        if (origen == null || destino == null || clase == null) return;
        if (origen.getIdCiudad() == destino.getIdCiudad()) {
            JOptionPane.showMessageDialog(vista, "Origen y Destino no pueden ser la misma ciudad.");
            return;
        }

        Date dateIda = vista.getDcFechaIda().getDate();
        if (dateIda == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione la fecha de Ida.");
            return;
        }

        // --- NUEVO: Validar y capturar Fecha de Regreso anticipadamente ---
        String fechaRegresoSql = null;
        if (vista.getRbtnIdaYVuelta().isSelected()) {
            Date dateRegreso = vista.getDcFechaRegreso().getDate();
            if (dateRegreso == null) {
                JOptionPane.showMessageDialog(vista, "Para viajes de Ida y Regreso, seleccione una fecha de regreso.");
                return;
            }
            // Validación para evitar que el regreso sea en el pasado relativo a la ida
            if (dateRegreso.before(dateIda)) {
                JOptionPane.showMessageDialog(vista, "La fecha de regreso no puede ser anterior a la de ida.");
                return;
            }
            fechaRegresoSql = formatoSql.format(dateRegreso);
        }

        int adultos = (int) vista.getSpnAdultos().getValue();
        int ninos = (int) vista.getSpnNinos().getValue();
        int bebes = (int) vista.getSpnBebes().getValue();
        totalPasajerosCalculado = adultos + ninos + bebes;

        String ordenamiento = (String) vista.getCmbOrdenamiento().getSelectedItem();
        boolean soloDirectos = vista.getChkSoloDirectos().isSelected();
        boolean conEquipaje = vista.getChkConEquipaje().isSelected();
        double precioMax = (double) vista.getSpnPrecioMaximo().getValue();

        vista.getLblEstadoBusqueda().setText("MOSTRANDO OFERTA: Vuelos de IDA");
        
        // Se envía el parámetro fechaRegresoSql al nuevo método del DAO
        ultimaListaVuelosConsultada = modeloDAO.buscarVuelosAvanzado(
                origen.getIdCiudad(), destino.getIdCiudad(), formatoSql.format(dateIda), fechaRegresoSql,
                clase.getIdClase(), totalPasajerosCalculado, null,
                ordenamiento, soloDirectos, precioMax, conEquipaje
        );

        actualizarTablaResultados(ultimaListaVuelosConsultada);
        vista.getBtnSeleccionarVuelo().setEnabled(!ultimaListaVuelosConsultada.isEmpty());
        vista.getBtnSeleccionarVuelo().setText("Seleccionar Vuelo de Ida");
    }

    private void actualizarTablaResultados(List<VueloDTO> vuelos) {
        DefaultTableModel modelo = vista.getModeloTabla();
        modelo.setRowCount(0);

        if (vuelos == null || vuelos.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No se encontraron vuelos que cumplan con su búsqueda.");
            return;
        }

        for (VueloDTO v : vuelos) {
            Object[] fila = new Object[]{
                v.getNumeroVuelo(),
                formatoFechaTabla.format(v.getFechaHoraSalida()),
                formatoFechaTabla.format(v.getFechaHoraLlegada()),
                v.getDuracion(),
                v.getAeropuertoOrigen(),
                v.getAeropuertoDestino(),
                v.getEscalas() == 0 ? "Directo" : v.getEscalas(),
                v.getAeronave(),
                v.getClaseServicio() + " - " + v.getNombreTarifa(),
                v.getCuposDisponibles(),
                formatoMoneda.format(v.getPrecioBase()),
                v.getRestricciones()
            };
            modelo.addRow(fila);
        }
    }

    private void procesarSeleccionVuelo() {
        int filaSeleccionada = vista.getTblVuelos().getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione un vuelo de la tabla.");
            return;
        }

        VueloDTO vueloSeleccionado = ultimaListaVuelosConsultada.get(filaSeleccionada);

        if (!buscandoRegreso) {
            vueloIdaSeleccionado = vueloSeleccionado;
            if (vista.getRbtnSoloIda().isSelected()) {
                avanzarSiguienteModulo(vueloIdaSeleccionado, null);
            } else {
                prepararBusquedaRegreso();
            }
        } else {
            avanzarSiguienteModulo(vueloIdaSeleccionado, vueloSeleccionado);
        }
    }

    /**
     * Ejecuta la consulta de la vuelta. Para esta búsqueda no hay "fechaRegreso",
     * por lo que el parámetro correspondiente se envía como null.
     */
    private void prepararBusquedaRegreso() {
        Date dateRegreso = vista.getDcFechaRegreso().getDate();
        if (dateRegreso == null) return; // Ya fue validado en ejecutarConsultaBusqueda

        buscandoRegreso = true;
        vista.getLblEstadoBusqueda().setText("MOSTRANDO OFERTA: Vuelos de REGRESO (Compatibles con Ida)");
        
        Ciudad origen = (Ciudad) vista.getCmbCiudadOrigen().getSelectedItem();
        Ciudad destino = (Ciudad) vista.getCmbCiudadDestino().getSelectedItem();
        ClaseServicio clase = (ClaseServicio) vista.getCmbClaseServicio().getSelectedItem();
        
        String ordenamiento = (String) vista.getCmbOrdenamiento().getSelectedItem();
        boolean soloDirectos = vista.getChkSoloDirectos().isSelected();
        boolean conEquipaje = vista.getChkConEquipaje().isSelected();
        double precioMax = (double) vista.getSpnPrecioMaximo().getValue();

        // El parámetro fechaRegreso va null (4to argumento) porque estamos consultando la vuelta en sí.
        ultimaListaVuelosConsultada = modeloDAO.buscarVuelosAvanzado(
                destino.getIdCiudad(), origen.getIdCiudad(), formatoSql.format(dateRegreso), null,
                clase.getIdClase(), totalPasajerosCalculado, vueloIdaSeleccionado.getFechaHoraLlegada(),
                ordenamiento, soloDirectos, precioMax, conEquipaje
        );

        actualizarTablaResultados(ultimaListaVuelosConsultada);
        vista.getBtnSeleccionarVuelo().setEnabled(!ultimaListaVuelosConsultada.isEmpty());
        vista.getBtnSeleccionarVuelo().setText("Finalizar Selección (Regreso)");
    }

    private void avanzarSiguienteModulo(VueloDTO ida, VueloDTO regreso) {
        String resumen = "¡Selección Exitosa!\n\nIda: " + ida.getNumeroVuelo() + "\n";
        if (regreso != null) resumen += "Regreso: " + regreso.getNumeroVuelo() + "\n";
        resumen += "Total Pasajeros: " + totalPasajerosCalculado;
        
        JOptionPane.showMessageDialog(vista, resumen, "Transición a Registro de Pasajeros", JOptionPane.INFORMATION_MESSAGE);
        vista.dispose(); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BuscarYSeleccionarVueloFrame vista = new BuscarYSeleccionarVueloFrame();
            VueloDAO dao = new VueloDAO();
            new BuscarYSeleccionarVueloController(vista, dao);
        });
    }
}