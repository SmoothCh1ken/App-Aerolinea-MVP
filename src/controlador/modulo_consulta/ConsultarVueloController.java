package controlador.modulo_consulta;

import modelo.modulo_consulta.Ciudad;
import modelo.modulo_consulta.VueloDAO;
import modelo.modulo_consulta.VueloDTO;
import vista.modulo_consulta.ConsultarVueloFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConsultarVueloController implements ActionListener {

    private final ConsultarVueloFrame vista;
    private final VueloDAO modeloDAO;
    private final SimpleDateFormat formatoFechaTabla;
    private final DecimalFormat formatoMoneda;
    private final SimpleDateFormat formatoSql; // Para transformar el objeto Date a String

    public ConsultarVueloController(ConsultarVueloFrame vista, VueloDAO modeloDAO) {
        this.vista = vista;
        this.modeloDAO = modeloDAO;
        this.formatoFechaTabla = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.formatoSql = new SimpleDateFormat("yyyy-MM-dd");
        this.formatoMoneda = new DecimalFormat("$ #,##0.00");

        inicializarVista();
        configurarEventos();
    }

    private void inicializarVista() {
        cargarCiudades();
        vista.setVisible(true);
    }

    private void cargarCiudades() {
        List<Ciudad> ciudades = modeloDAO.obtenerTodasLasCiudades();
        vista.getCmbCiudadOrigen().removeAllItems();
        vista.getCmbCiudadDestino().removeAllItems();

        for (Ciudad ciudad : ciudades) {
            vista.getCmbCiudadOrigen().addItem(ciudad);
            vista.getCmbCiudadDestino().addItem(ciudad);
        }

        if (ciudades.size() > 1) {
            vista.getCmbCiudadDestino().setSelectedIndex(1);
        }
    }

    private void configurarEventos() {
        vista.getBtnConsultar().addActionListener(this);
        vista.getRbtnSoloIda().addActionListener(e -> gestionarTipoViaje());
        vista.getRbtnIdaYVuelta().addActionListener(e -> gestionarTipoViaje());
    }

    private void gestionarTipoViaje() {
        boolean esIdaYVuelta = vista.getRbtnIdaYVuelta().isSelected();
        vista.getDcFechaRegreso().setEnabled(esIdaYVuelta);
        
        if (!esIdaYVuelta) {
            // Si elige Solo Ida, limpiamos el calendario de regreso poniéndolo en null
            vista.getDcFechaRegreso().setDate(null);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnConsultar()) {
            consultar();
        }
    }

    public void consultar() {
        Ciudad origen = (Ciudad) vista.getCmbCiudadOrigen().getSelectedItem();
        Ciudad destino = (Ciudad) vista.getCmbCiudadDestino().getSelectedItem();

        if (origen == null || destino == null) {
            JOptionPane.showMessageDialog(vista, "Debe seleccionar la ciudad de origen y destino.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (origen.getIdCiudad() == destino.getIdCiudad()) {
            JOptionPane.showMessageDialog(vista, "La ciudad de origen y de destino no pueden ser la misma.",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- VALIDACIÓN DE JCALENDAR ---
        // Extraemos el objeto Date directo del JDateChooser
        Date dateIda = vista.getDcFechaIda().getDate();
        
        if (dateIda == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione la fecha de viaje de ida utilizando el calendario.",
                    "Campo Requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convertimos el Date a String para MySQL
        String fechaIdaSql = formatoSql.format(dateIda);

        boolean esIdaYVuelta = vista.getRbtnIdaYVuelta().isSelected();
        String fechaRegresoSql = null;

        if (esIdaYVuelta) {
            Date dateRegreso = vista.getDcFechaRegreso().getDate();
            
            if (dateRegreso == null) {
                JOptionPane.showMessageDialog(vista, "Al seleccionar viaje de ida y regreso, la fecha de regreso es obligatoria.",
                        "Criterio de Aceptación CA-02", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Comparamos directamente los objetos Date (mucho más seguro que comparar Strings)
            if (dateRegreso.before(dateIda)) {
                JOptionPane.showMessageDialog(vista, "La fecha de regreso no puede ser anterior a la fecha de ida.",
                        "Error en Fechas", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            fechaRegresoSql = formatoSql.format(dateRegreso);
        }

        List<VueloDTO> vuelosIda = modeloDAO.buscarVuelos(origen.getIdCiudad(), destino.getIdCiudad(), fechaIdaSql);
        List<VueloDTO> vuelosRegreso = null;
        
        if (esIdaYVuelta) {
            vuelosRegreso = modeloDAO.buscarVuelos(destino.getIdCiudad(), origen.getIdCiudad(), fechaRegresoSql);
        }

        actualizarTablaResultados(vuelosIda, vuelosRegreso);
    }

    private void actualizarTablaResultados(List<VueloDTO> vuelosIda, List<VueloDTO> vuelosRegreso) {
        DefaultTableModel modelo = vista.getModeloTabla();
        modelo.setRowCount(0); 

        int totalEncontrados = 0;

        if (vuelosIda != null && !vuelosIda.isEmpty()) {
            for (VueloDTO v : vuelosIda) {
                Object[] fila = new Object[]{
                    v.getNumeroVuelo() + " (IDA)",
                    v.getAeropuertoOrigen(),
                    v.getAeropuertoDestino(),
                    formatoFechaTabla.format(v.getFechaHoraSalida()),
                    formatoFechaTabla.format(v.getFechaHoraLlegada()),
                    v.getEscalas() == 0 ? "Directo" : v.getEscalas() + " Escala(s)",
                    v.getDetalleEscala() == null ? "N/A" : v.getDetalleEscala(),
                    v.getNombreTarifa(),
                    formatoMoneda.format(v.getPrecioBase())
                };
                modelo.addRow(fila);
                totalEncontrados++;
            }
        }

        if (vuelosRegreso != null && !vuelosRegreso.isEmpty()) {
            for (VueloDTO v : vuelosRegreso) {
                Object[] fila = new Object[]{
                    v.getNumeroVuelo() + " (REGRESO)",
                    v.getAeropuertoOrigen(),
                    v.getAeropuertoDestino(),
                    formatoFechaTabla.format(v.getFechaHoraSalida()),
                    formatoFechaTabla.format(v.getFechaHoraLlegada()),
                    v.getEscalas() == 0 ? "Directo" : v.getEscalas() + " Escala(s)",
                    v.getDetalleEscala() == null ? "N/A" : v.getDetalleEscala(),
                    v.getNombreTarifa(),
                    formatoMoneda.format(v.getPrecioBase())
                };
                modelo.addRow(fila);
                totalEncontrados++;
            }
        }

        if (totalEncontrados == 0) {
            JOptionPane.showMessageDialog(vista, "No se encontraron vuelos disponibles que coincidan con la búsqueda.",
                    "Sin Oferta Disponible", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConsultarVueloFrame vista = new ConsultarVueloFrame();
            VueloDAO modeloDAO = new VueloDAO();
            new ConsultarVueloController(vista, modeloDAO);
        });
    }
}