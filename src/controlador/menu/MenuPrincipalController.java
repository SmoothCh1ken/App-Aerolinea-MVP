package controlador.menu;

import controlador.modulo_consulta.BuscarYSeleccionarVueloController;
import controlador.modulo_pago.ResumenCompraController;
import controlador.modulo_seguridad.GuardiaAcceso;
import controlador.modulo_seguridad.PanelSeguridadController;
import modelo.modulo_seguridad.Modulo;
import java.sql.SQLException;
import modelo.modulo_pago.PagoDAO;
import javax.swing.JOptionPane;
import modelo.modulo_consulta.VueloDAO;
import vista.menu.MenuPrincipalFrame;
import vista.modulo_cambios.GestionReservaFrame;
import vista.modulo_consulta.BuscarYSeleccionarVueloFrame;
import controlador.modulo_reportes.ReportesController;

/**
 * Controlador central que implementa la navegación.
 * Coordina el salto entre los distintos módulos del sistema.
 */
public class MenuPrincipalController implements Navegador {
    
    private final MenuPrincipalFrame vista;

    public MenuPrincipalController(MenuPrincipalFrame vista) {
        this.vista = vista;
        configurarEventos();
        GuardiaAcceso.instalar(this::irAlMenu); // seguridad: si la sesión expira, vuelve al menú
    }

    private void configurarEventos() {
        vista.addReservarListener(e -> iniciarReserva());
        vista.addPagarListener(e -> iniciarPago(null));
        vista.addGestionarListener(e -> iniciarGestion(null, null));
        vista.addOperacionesListener(e -> abrirOperaciones());
        vista.addReportesListener(e -> abrirReportes());
        vista.addSeguridadListener(e -> iniciarSeguridad());
    }

    @Override
    public void irAlMenu() {
        liberarReservasExpiradas(); // CA-24: libera cupos de reservas vencidas
        vista.setVisible(true);
        vista.toFront();
    }

    /** Mantenimiento ligero: marca como "Expirada" las reservas cuyo plazo de pago ya venció. */
    private void liberarReservasExpiradas() {
        try {
            new PagoDAO().liberarReservasExpiradas();
        } catch (SQLException ex) {
            System.err.println("No se pudieron liberar las reservas expiradas: " + ex.getMessage());
        }
    }

    @Override
    public void iniciarReserva() {
        // Oculta el menú
        vista.setVisible(false);
        
        // Arranca el Módulo de Consulta (RF-01)
        BuscarYSeleccionarVueloFrame frameConsulta = new BuscarYSeleccionarVueloFrame();
        // Nota: Le pasamos 'this' para que el controlador de consulta sepa a quién llamar para volver al menú o ir a pagar.
        new BuscarYSeleccionarVueloController(frameConsulta, new VueloDAO(), this); 
        frameConsulta.setVisible(true);
    }

    @Override
    public void iniciarPago(String pnr) {
        // Oculta el menú
        vista.setVisible(false);
        
        // Arranca el Módulo de Pago (RF-17); al cerrarse vuelve al menú mediante el Navegador
        ResumenCompraController.iniciar(pnr, this);
    }

    @Override
    public void iniciarGestion(String pnr, String apellido) {
        // Oculta el menú
        vista.setVisible(false);
        
        // Arranca el Módulo de Cambios (RF-27) en modo cliente; al cerrarse vuelve al menú
        GestionReservaFrame frameGestion = new GestionReservaFrame(GestionReservaFrame.Modo.CLIENTE, this::irAlMenu);
        frameGestion.precargar(pnr, apellido);
        frameGestion.setVisible(true);
    }
    
    @Override
    public void iniciarSeguridad() {
        // Inicio de sesión con segundo factor (si aún no hay sesión) y panel de seguridad
        if (!GuardiaAcceso.autenticar(vista)) {
            return;
        }
        vista.setVisible(false);
        PanelSeguridadController.iniciar(this::irAlMenu);
    }

    private void abrirOperaciones() {
        // RF-27: solo entra el funcionario cuyo rol tiene acceso a Operaciones (reemplaza el PIN 1234)
        if (!GuardiaAcceso.autorizar(Modulo.OPERACIONES, vista)) {
            return;
        }
        vista.setVisible(false);
        GestionReservaFrame frameOperaciones = new GestionReservaFrame(GestionReservaFrame.Modo.OPERACIONES, this::irAlMenu);
        GuardiaAcceso.registrar(frameOperaciones); // se cierra solo si la sesión expira por inactividad
        frameOperaciones.setVisible(true);

    }
    
    @Override
    public void iniciarReportes() {
        vista.setVisible(false);
        ReportesController.iniciar(this::irAlMenu);
    }

    private void abrirReportes() {
    // RF-27: solo entra el funcionario cuyo rol tiene acceso a Reportes
    if (!GuardiaAcceso.autorizar(Modulo.REPORTES, vista)) {
        return;
    }
    iniciarReportes();
    }
}