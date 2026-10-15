package controlador.menu;

/**
 * Interfaz Mediator que define las rutas de navegación del sistema.
 * Ningún módulo debe conocer a otro, solo le piden al Navegador a dónde ir.
 */
public interface Navegador {
    void irAlMenu();
    void iniciarReserva();
    void iniciarPago(String pnr);
    void iniciarGestion(String pnr, String apellido);
}