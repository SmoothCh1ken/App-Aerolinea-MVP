package modelo.modulo_pago;

/**
 * Punto de integración para el envío real de correos.
 * En el prototipo solo simula el envío (imprime en consola y devuelve true).
 * Para un envío real se puede usar JavaMail (jakarta.mail) dentro de enviar().
 */
public class ServicioCorreo {

    private ServicioCorreo() { }

    public static boolean enviar(String destino, String asunto, String mensaje) {
        System.out.println("[CORREO SIMULADO] Para: " + destino);
        System.out.println("Asunto: " + asunto);
        System.out.println(mensaje);
        return true;
    }
}
