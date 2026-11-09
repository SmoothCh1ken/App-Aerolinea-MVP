package modelo.modulo_pago;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import javax.mail.util.ByteArrayDataSource;
import java.util.List;

/**
 * Punto de integración para el envío real de correos utilizando Apache Commons Email.
 */
public class ServicioCorreo {
    
    // Configuración del servidor
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String CORREO_ORIGEN = "luciano.bascur28@gmail.com"; 
    private static final String PASSWORD_APP = "hezntktejpildwls";

    private ServicioCorreo() { }

    private static void configurarServidor(org.apache.commons.mail.Email email) throws Exception {
        email.setHostName(SMTP_HOST);
        email.setSmtpPort(SMTP_PORT);
        email.setAuthenticator(new DefaultAuthenticator(CORREO_ORIGEN, PASSWORD_APP));
        email.setStartTLSEnabled(true);
        email.setFrom(CORREO_ORIGEN, "AirColombia Notificaciones");
    }

    /**
     * Envia un correo de texto plano (Ideal para cancelaciones / cambios de vuelo).
     */
    public static boolean enviar(String destino, String asunto, String mensaje) {
        try {
            SimpleEmail email = new SimpleEmail();
            configurarServidor(email);
            
            email.addTo(destino);
            email.setSubject(asunto);
            email.setMsg(mensaje);
            
            email.send();
            return true;
        } catch (Exception e) {
            System.err.println("[SISTEMA - ERROR DE CORREO] No se pudo enviar el correo simple.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Envia un correo con un archivo PDF adjunto desde la Memoria RAM.
     */
    public static boolean enviarConAdjunto(String destino, String asunto, String mensaje, byte[] pdfEnMemoria, String nombreArchivoPDF) {
        try {
            MultiPartEmail email = new MultiPartEmail();
            configurarServidor(email);
            
            email.addTo(destino);
            email.setSubject(asunto);
            email.setMsg(mensaje);
            
            ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfEnMemoria, "application/pdf");
            email.attach(dataSource, nombreArchivoPDF, "Documento adjunto", EmailAttachment.ATTACHMENT);
            
            email.send();
            return true;
            
        } catch (Exception e) {
            System.err.println("[SISTEMA - ERROR DE CORREO] No se pudo enviar el correo con adjunto.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * NUEVO: Envia un correo con MÚLTIPLES archivos PDF adjuntos desde la Memoria RAM.
     * Ideal para reservas con varios pasajeros.
     */
    public static boolean enviarMultiplesAdjuntos(String destino, String asunto, String mensaje, List<byte[]> pdfs, List<String> nombresArchivos) {
        try {
            System.out.println("[SISTEMA] Preparando correo con " + pdfs.size() + " tiquetes adjuntos para: " + destino + "...");
            
            MultiPartEmail email = new MultiPartEmail();
            configurarServidor(email);
            
            email.addTo(destino);
            email.setSubject(asunto);
            email.setMsg(mensaje);
            
            // Iterar sobre las listas para adjuntar cada PDF generado
            for (int i = 0; i < pdfs.size(); i++) {
                ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfs.get(i), "application/pdf");
                email.attach(dataSource, nombresArchivos.get(i), "Tiquete de abordaje", EmailAttachment.ATTACHMENT);
            }
            
            email.send();
            System.out.println("[SISTEMA] ¡Correo con tiquetes enviado exitosamente a " + destino + "!");
            return true;
            
        } catch (Exception e) {
            System.err.println("[SISTEMA - ERROR DE CORREO] No se pudo enviar el correo con múltiples adjuntos.");
            e.printStackTrace();
            return false;
        }
    }
}