package controlador.menu;

import controlador.modulo_consulta.BuscarYSeleccionarVueloController;
import controlador.modulo_pago.ResumenCompraController;
import controlador.modulo_seguridad.GuardiaAcceso;
import controlador.modulo_seguridad.PanelSeguridadController;
import modelo.modulo_seguridad.Modulo;
import modelo.modulo_seguridad.SesionActual;
import modelo.modulo_seguridad.Usuario;
import java.sql.SQLException;
import modelo.modulo_pago.PagoDAO;
import javax.swing.JOptionPane;
import modelo.modulo_consulta.VueloDAO;
import vista.menu.MenuPrincipalFrame;
import vista.menu.PerfilUsuarioPopup;
import vista.modulo_cambios.GestionReservaFrame;
import vista.modulo_consulta.BuscarYSeleccionarVueloFrame;
import controlador.modulo_reportes.ReportesController;

/**
 * Controlador central que implementa la navegación.
 * Coordina el salto entre los distintos módulos del sistema.
 */
public class MenuPrincipalController implements Navegador {
    
    private final MenuPrincipalFrame vista;
    private final PerfilUsuarioPopup popupPerfil;

    public MenuPrincipalController(MenuPrincipalFrame vista) {
        this.vista = vista;
        this.popupPerfil = new PerfilUsuarioPopup(); // Instanciamos el nuevo popup
        configurarEventos();
        GuardiaAcceso.instalar(this::irAlMenu); // seguridad: si la sesión expira, vuelve al menú
        actualizarVistaSesion(); // Ajusta la interfaz al estado inicial
    }

    private void configurarEventos() {
        // --- Nuevos eventos para la barra superior ---
        vista.addUsuarioListener(e -> manejarBotonUsuario());
        popupPerfil.addCerrarSesionListener(e -> cerrarSesion());

        // --- Eventos originales de los módulos ---
        vista.addReservarListener(e -> iniciarReserva());
        vista.addPagarListener(e -> iniciarPago(null));
        vista.addGestionarListener(e -> iniciarGestion(null, null));
        vista.addOperacionesListener(e -> abrirOperaciones());
        vista.addReportesListener(e -> abrirReportes());
        vista.addSeguridadListener(e -> iniciarSeguridad());
    }

    // ---------- LÓGICA DE SESIÓN (NUEVA) ----------

    private void manejarBotonUsuario() {
        if (SesionActual.activa()) {
            // Si ya está logueado, desplegamos el popup con sus datos
            Usuario u = SesionActual.getUsuario();
            popupPerfil.setDatosUsuario(u.getNombreCompleto(), u.getCorreo());
            popupPerfil.mostrarDebajoDe(vista.getBtnUsuario());
        } else {
            // Si no está logueado, invocamos el proceso de Login
            if (GuardiaAcceso.autenticar(vista)) {
                actualizarVistaSesion(); // Si se logueó con éxito, actualizamos la UI
            }
        }
    }

    private void cerrarSesion() {
        GuardiaAcceso.cerrarSesion();
        actualizarVistaSesion();
    }

    /** Muestra u oculta los módulos de administración en base a la sesión activa. */
    private void actualizarVistaSesion() {
        if (SesionActual.activa()) {
            Usuario u = SesionActual.getUsuario();
            // Enviamos el primer nombre o usuario para que quepa en el botón
            vista.setEstadoSesion(true, u.getNombres()); 
        } else {
            vista.setEstadoSesion(false, null);
        }
    }

    // ---------- NAVEGACIÓN Y DELEGACIÓN (ORIGINAL INTACTA) ----------

    @Override
    public void irAlMenu() {
        liberarReservasExpiradas(); // CA-24: libera cupos de reservas vencidas
        actualizarVistaSesion(); // Paso 4: Refresca por si la sesión expiró por inactividad
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
        vista.setVisible(false);
        BuscarYSeleccionarVueloFrame frameConsulta = new BuscarYSeleccionarVueloFrame();
        new BuscarYSeleccionarVueloController(frameConsulta, new VueloDAO(), this); 
        frameConsulta.setVisible(true);
    }

    @Override
    public void iniciarPago(String pnr) {
        vista.setVisible(false);
        ResumenCompraController.iniciar(pnr, this);
    }

    @Override
    public void iniciarGestion(String pnr, String apellido) {
        vista.setVisible(false);
        GestionReservaFrame frameGestion = new GestionReservaFrame(GestionReservaFrame.Modo.CLIENTE, this::irAlMenu);
        frameGestion.precargar(pnr, apellido);
        frameGestion.setVisible(true);
    }
    
    @Override
    public void iniciarSeguridad() {
        // Mantiene la doble validación por seguridad
        if (!GuardiaAcceso.autenticar(vista)) {
            return;
        }
        vista.setVisible(false);
        PanelSeguridadController.iniciar(this::irAlMenu);
    }

    private void abrirOperaciones() {
        // RF-27: solo entra el funcionario cuyo rol tiene acceso a Operaciones
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