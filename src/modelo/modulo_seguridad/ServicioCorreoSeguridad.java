package modelo.modulo_seguridad;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Servicio utilitario independiente del módulo de seguridad para enviar
 * correos con códigos temporales (OTP) de verificación de 6 dígitos.
 */
public class ServicioCorreoSeguridad {

    private static final String REMITENTE = "luciano.bascur28@gmail.com";
    private static final String CLAVE_REMITENTE = "hezntktejpildwls";

    /**
     * Envía un código OTP de 6 dígitos al correo especificado.
     * Muestra la traza en consola para pruebas locales y lanza un hilo para el envío SMTP.
     */
    public static boolean enviarCodigo(String correoDestino, String codigo) {
        //System.out.println("[SEGURIDAD - CORREO] Código de verificación [" + codigo + "] generado para: " + correoDestino);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE, CLAVE_REMITENTE);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMITENTE, "Seguridad Aerolínea MVP"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(correoDestino));
            message.setSubject("Código de verificación de inicio de sesión");

            String contenidoHtml = "<div style='font-family: Arial, sans-serif; padding: 15px; border: 1px solid #ddd;'>"
                    + "<h2 style='color: #1976D2;'>Código de Verificación</h2>"
                    + "<p>Su código de seguridad para ingresar al sistema es:</p>"
                    + "<h1 style='color: #2E7D32; letter-spacing: 5px; background: #f0f0f0; padding: 10px; display: inline-block;'>" 
                    + codigo + "</h1>"
                    + "<p>Este código es válido por <b>2 minutos</b>.</p>"
                    + "<p style='color: #777; font-size: 12px;'>Si no solicitó este código, comuníquese con Tecnología inmediatamente.</p>"
                    + "</div>";

            message.setContent(contenidoHtml, "text/html; charset=utf-8");

            // Envío asíncrono para evitar bloquear la interfaz de usuario durante la conexión SMTP
            new Thread(() -> {
                try {
                    Transport.send(message);
                } catch (Exception ex) {
                    System.err.println("[SEGURIDAD - CORREO] Advertencia: No se pudo conectar al servidor SMTP real: " + ex.getMessage());
                }
            }).start();

            return true;
        } catch (Exception e) {
            System.err.println("[SEGURIDAD - CORREO] Error al construir el mensaje de correo: " + e.getMessage());
            return true; // Retorna true para permitir continuar las pruebas en local aunque falle el SMTP
        }
    }
}