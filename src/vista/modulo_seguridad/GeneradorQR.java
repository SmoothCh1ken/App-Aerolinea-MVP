package vista.modulo_seguridad;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.awt.image.BufferedImage;
import java.net.URLEncoder;
import javax.swing.ImageIcon;

/**
 * Utilidad gráfica para generar un Código QR a partir del secreto MFA.
 * Utiliza la librería externa ZXing.
 */
public class GeneradorQR {

    private static final String EMISOR = "AerolineaMVP";
    private static final int ANCHO_QR = 220;
    private static final int ALTO_QR = 220;

    private GeneradorQR() { }

    /**
     * Construye la URI estándar de autenticación y la renderiza como un ícono de Swing.
     * 
     * @param identificador El nombre de usuario o correo que se mostrará en la App.
     * @param secretoMfa La cadena Base32 generada para el usuario.
     * @return Un ImageIcon que contiene el código QR, o null si ocurre un error.
     */
    public static ImageIcon generarIconoQR(String identificador, String secretoMfa) {
        try {
            // 1. Formatear la URL para que sea compatible con la mayoría de apps (Google Authenticator, Authy, etc.)
            // Formato: otpauth://totp/Emisor:Usuario?secret=SECRETO&issuer=Emisor
            String idCodificado = URLEncoder.encode(identificador, "UTF-8").replace("+", "%20");
            String uriTotp = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                    EMISOR, idCodificado, secretoMfa, EMISOR);

            // 2. Usar ZXing para calcular la matriz de bits del código QR
            BitMatrix matriz = new MultiFormatWriter().encode(uriTotp, BarcodeFormat.QR_CODE, ANCHO_QR, ALTO_QR);

            // 3. Convertir la matriz matemática en una imagen de Java (BufferedImage)
            BufferedImage imagen = MatrixToImageWriter.toBufferedImage(matriz);

            // 4. Retornarla encapsulada para Swing
            return new ImageIcon(imagen);

        } catch (Exception e) {
            System.err.println("Error interno generando el Código QR: " + e.getMessage());
            return null;
        }
    }
}