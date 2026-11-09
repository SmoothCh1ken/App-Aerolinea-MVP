package controlador.modulo_seguridad;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import modelo.modulo_seguridad.AuditoriaDAO;
import modelo.modulo_seguridad.FiltroAuditoria;
import modelo.modulo_seguridad.Incidente;
import modelo.modulo_seguridad.IncidenteDAO;
import modelo.modulo_seguridad.SesionActual;
import modelo.modulo_seguridad.UsuarioService;
import vista.modulo_seguridad.AuditoriaFrame;

/** Consulta de la auditoría (RF-31) y atención de incidentes de seguridad (RF-29). */
public class AuditoriaController {

    private final AuditoriaFrame vista;
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();
    private final IncidenteDAO incidenteDAO = new IncidenteDAO();
    private final UsuarioService servicio = new UsuarioService();
    private List<Incidente> incidentes = new ArrayList<>();

    public AuditoriaController(AuditoriaFrame vista, boolean verAuditoria, boolean verIncidentes) {
        this.vista = vista;
        if (verAuditoria) {
            vista.addBuscarListener(e -> buscar());
            vista.addLimpiarListener(e -> { vista.limpiarFiltros(); buscar(); });
            buscar();
        }
        if (verIncidentes) {
            vista.addActualizarIncidentesListener(e -> cargarIncidentes());
            vista.addAtenderListener(e -> atender(false));
            vista.addDesbloquearListener(e -> atender(true));
            cargarIncidentes();
        }
    }

    public static AuditoriaController iniciar(boolean verAuditoria, boolean verIncidentes) {
        AuditoriaFrame frame = new AuditoriaFrame(verAuditoria, verIncidentes);
        AuditoriaController controlador = new AuditoriaController(frame, verAuditoria, verIncidentes);
        GuardiaAcceso.registrar(frame);
        frame.setVisible(true);
        return controlador;
    }

    // ---------- auditoría ----------

    private void buscar() {
        FiltroAuditoria f = new FiltroAuditoria();
        f.setDesde(aFecha(vista.getDesde()));
        f.setHasta(aFecha(vista.getHasta()));
        if (f.getDesde() != null && f.getHasta() != null && f.getDesde().isAfter(f.getHasta())) {
            vista.mostrarAdvertencia("La fecha inicial no puede ser posterior a la final.");
            return;
        }
        f.setUsuario(vista.getUsuarioFiltro());
        f.setEvento(vista.getEventoFiltro());
        f.setResultado(vista.getResultadoFiltro());
        try {
            vista.mostrarAuditoria(auditoriaDAO.listar(f));
        } catch (SQLException ex) {
            vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
        }
    }

    private LocalDate aFecha(Date fecha) {
        return fecha == null ? null : fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // ---------- incidentes ----------

    private void cargarIncidentes() {
        try {
            incidentes = incidenteDAO.listar(vista.isSoloAbiertos());
            vista.mostrarIncidentes(incidentes);
        } catch (SQLException ex) {
            vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
        }
    }

    private void atender(boolean desbloquear) {
        int fila = vista.getIncidenteSeleccionado();
        if (fila < 0 || fila >= incidentes.size()) {
            vista.mostrarAdvertencia("Seleccione un incidente de la tabla.");
            return;
        }
        Incidente incidente = incidentes.get(fila);
        if (!"Abierto".equals(incidente.getEstado())) {
            vista.mostrarInfo("Ese incidente ya fue atendido.");
            return;
        }
        try {
            servicio.atenderIncidente(incidente, desbloquear, SesionActual.getUsuario());
            cargarIncidentes();
            vista.mostrarInfo(desbloquear ? "Cuenta desbloqueada e incidente atendido."
                                          : "Incidente marcado como atendido.");
        } catch (SQLException ex) {
            vista.mostrarError("Error consultando la base de datos: " + ex.getMessage());
        }
    }
}
