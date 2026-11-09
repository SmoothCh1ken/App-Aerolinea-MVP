package modelo.modulo_seguridad;

/**
 * Punto de integración para avisar al área de Tecnología (RF-29).
 * En el prototipo solo imprime el aviso en consola; el incidente además queda guardado
 * en la tabla Incidente_seguridad y se ve en la pantalla de incidentes.
 * Para un aviso real se puede enviar un correo o un mensaje a una mesa de ayuda aquí.
 */
public final class ServicioReporteTi {

    private ServicioReporteTi() { }

    public static void enviar(String asunto, String mensaje) {
        System.out.println("[REPORTE A TECNOLOGÍA] " + asunto);
        System.out.println(mensaje);
    }
}
