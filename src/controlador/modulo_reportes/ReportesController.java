package controlador.modulo_reportes;

import modelo.modulo_reportes.ExportadorExcel;
import modelo.modulo_reportes.ExportadorPDF;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import modelo.modulo_reportes.ExportadorCSV;
import modelo.modulo_reportes.FiltroReporte;
import modelo.modulo_reportes.IndicadoresTablero;
import modelo.modulo_reportes.OpcionFiltro;
import modelo.modulo_reportes.ReportesDAO;
import modelo.modulo_reportes.ResultadoReporte;
import modelo.modulo_reportes.TableroDAO;
import modelo.modulo_reportes.TipoReporte;
import vista.modulo_reportes.ReportesFrame;
import controlador.modulo_seguridad.GuardiaAcceso;

/**
 * Controlador del módulo de Reportes Gerenciales y Analítica (RF-32, RF-33 y RF-34).
 *
 * Desde el menú principal se abre con:
 *     ReportesController.iniciar(this::irAlMenu);
 */
public class ReportesController {

    private static final int INTERVALO_MS = 30_000;
    private static final Locale CO = Locale.forLanguageTag("es-CO");
    private static final NumberFormat MONEDA = NumberFormat.getCurrencyInstance(CO);
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    static {
        MONEDA.setMinimumFractionDigits(0);
        MONEDA.setMaximumFractionDigits(0);
    }

    private final ReportesFrame vista;
    private final Runnable alSalir; // acción al cerrar (volver al menú); puede ser null
    private final ReportesDAO reportesDAO = new ReportesDAO();
    private final TableroDAO tableroDAO = new TableroDAO();
    private final Timer temporizador;
    private ResultadoReporte ultimoReporte;
    private boolean erroresDeTableroAvisados;

    public ReportesController(ReportesFrame vista, Runnable alSalir) {
        this.vista = vista;
        this.alSalir = alSalir;

        vista.addAplicarListener(e -> aplicarFiltros());
        vista.addLimpiarListener(e -> { vista.limpiarFiltros(); aplicarFiltros(); });
        vista.addActualizarListener(e -> actualizarTablero(true));
        vista.addGenerarListener(e -> generarReporte());
        vista.addExportarListener(e -> exportar());
        vista.addTipoListener(e -> mostrarNota());
        vista.addVolverListener(e -> volver());
        vista.addCierreListener(this::volver);

        cargarFiltros();
        mostrarNota();

        // RF-34: el tablero se refresca solo mientras esté a la vista
        temporizador = new Timer(INTERVALO_MS, e -> {
            if (vista.isTableroActivo() && vista.isActualizacionAutomatica()) {
                actualizarTablero(false);
            }
        });
        temporizador.start();
        actualizarTablero(true);
    }

    /** Crea la ventana y su controlador. */
    public static ReportesController iniciar(Runnable alSalir) {
        ReportesFrame frame = new ReportesFrame();
        ReportesController controlador = new ReportesController(frame, alSalir);
        GuardiaAcceso.registrar(frame);
        frame.setVisible(true);
        return controlador;
    }

    /** Solo para probar el módulo de forma independiente (Run File). */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorada) { }
            iniciar(() -> System.exit(0));
        });
    }

    // ---------- filtros ----------

    private void cargarFiltros() {
        try {
            List<OpcionFiltro> ciudades = reportesDAO.listarCiudades();
            vista.cargarOrigenes(ciudades);
            vista.cargarDestinos(ciudades);
            vista.cargarRutas(reportesDAO.listarRutas());
        } catch (SQLException ex) {
            vista.mostrarError("No se pudieron cargar los filtros: " + ex.getMessage());
        }
    }

    /** Lee los filtros de la pantalla. Devuelve null (y avisa) si son inválidos. */
    private FiltroReporte leerFiltro(boolean avisar) {
        FiltroReporte f = new FiltroReporte();
        f.setDesde(aFecha(vista.getDesde()));
        f.setHasta(aFecha(vista.getHasta()));
        if (f.getDesde() != null && f.getHasta() != null && f.getDesde().isAfter(f.getHasta())) {
            if (avisar) vista.mostrarAdvertencia("La fecha inicial no puede ser posterior a la final.");
            return null;
        }
        f.setIdCiudadOrigen(idDe(vista.getOrigen()));
        f.setIdCiudadDestino(idDe(vista.getDestino()));
        if (f.getIdCiudadOrigen() != null && f.getIdCiudadOrigen().equals(f.getIdCiudadDestino())) {
            if (avisar) vista.mostrarAdvertencia("Origen y destino no pueden ser la misma ciudad.");
            return null;
        }
        f.setIdRuta(idDe(vista.getRuta()));
        f.setPeriodo(vista.getPeriodo());
        return f;
    }

    private LocalDate aFecha(Date fecha) {
        return fecha == null ? null : fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Integer idDe(OpcionFiltro opcion) {
        return opcion == null ? null : opcion.getId();
    }

    private void aplicarFiltros() {
        if (leerFiltro(true) == null) return;
        actualizarTablero(true);
        if (ultimoReporte != null) {
            generarReporte(); // vuelve a generar el reporte con los filtros nuevos
        }
    }

    // ---------- tablero (RF-34) ----------

    private void actualizarTablero(boolean avisarErrores) {
        FiltroReporte f = leerFiltro(avisarErrores);
        if (f == null) return;
        try {
            IndicadoresTablero t = tableroDAO.obtener(f);
            vista.setIndicadores(new String[] {
                MONEDA.format(t.getVentasTotal()),
                String.valueOf(t.getTiquetesVendidos()),
                MONEDA.format(t.getIngresosServicios()),
                porcentaje(t.getOcupacionPromedio()),
                String.valueOf(t.getReservasConfirmadas()),
                String.valueOf(t.getReservasPendientes()),
                t.getVuelosReprogramados() + " / " + t.getVuelosCancelados(),
                porcentaje(t.tasaAprobacionPagos()),
                MONEDA.format(t.getReembolsos())
            });
            vista.setEstadoTablero("Actualizado: " + t.getActualizadoEn().format(FORMATO_HORA));
            erroresDeTableroAvisados = false;
        } catch (SQLException ex) {
            vista.setEstadoTablero("Error al actualizar: " + ex.getMessage());
            // la actualización automática no debe llenar la pantalla de ventanas de error
            if (avisarErrores && !erroresDeTableroAvisados) {
                erroresDeTableroAvisados = true;
                vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
            }
        }
    }

    private String porcentaje(double valor) {
        return String.format(CO, "%.1f%%", valor);
    }

    // ---------- reportes (RF-32) ----------

    private void mostrarNota() {
        TipoReporte tipo = vista.getTipoReporte();
        vista.setNota(tipo == null ? " " : tipo.getNota());
    }

    private void generarReporte() {
        FiltroReporte f = leerFiltro(true);
        TipoReporte tipo = vista.getTipoReporte();
        if (f == null || tipo == null) return;
        try {
            ResultadoReporte resultado = reportesDAO.generar(tipo, f);
            ultimoReporte = resultado;
            vista.mostrarReporte(resultado.getColumnas(), formatear(resultado));
            if (resultado.getFilas().isEmpty()) {
                vista.mostrarInfo("No hay datos para los filtros seleccionados.");
            }
        } catch (SQLException ex) {
            vista.mostrarError("Error generando el reporte: " + ex.getMessage());
        }
    }

    /** Pasa los valores a texto: dinero con formato de moneda y decimales con una cifra. */
    private List<String[]> formatear(ResultadoReporte r) {
        List<String[]> filas = new ArrayList<>();
        for (Object[] fila : r.getFilas()) {
            String[] texto = new String[fila.length];
            for (int i = 0; i < fila.length; i++) {
                Object v = fila[i];
                if (v instanceof BigDecimal && r.esMoneda(i)) {
                    texto[i] = MONEDA.format(v);
                } else if (v instanceof Double) {
                    texto[i] = String.format(CO, "%.1f", (Double) v);
                } else {
                    texto[i] = String.valueOf(v);
                }
            }
            filas.add(texto);
        }
        return filas;
    }

   private void exportar() {
        if (ultimoReporte == null) {
            vista.mostrarAdvertencia("Primero genere un reporte.");
            return;
        }
        
        // Generar un nombre base sugerido limpio
        String nombreBase = "reporte_" + ultimoReporte.getTitulo().toLowerCase(CO).replaceAll("[^a-z0-9]+", "_");
        
        // Llamar al nuevo selector que soporta Excel, PDF y CSV
        File archivo = vista.elegirArchivoExportacion(nombreBase);
        if (archivo == null) return;

        try {
            String nombreArchivo = archivo.getName().toLowerCase(CO);
            
            if (nombreArchivo.endsWith(".pdf")) {
                ExportadorPDF.exportar(archivo, ultimoReporte);
            } else if (nombreArchivo.endsWith(".xlsx")) {
                ExportadorExcel.exportar(archivo, ultimoReporte);
            } else {
                ExportadorCSV.exportar(archivo, ultimoReporte);
            }
            
            vista.mostrarInfo("Reporte exportado exitosamente en:\n" + archivo.getAbsolutePath());
            
        } catch (IOException ex) {
            vista.mostrarError("No se pudo exportar el archivo: " + ex.getMessage());
        }
    }

    // ---------- salida ----------

    private void volver() {
        temporizador.stop();
        vista.dispose();
        if (alSalir != null) {
            alSalir.run();
        }
    }
}
