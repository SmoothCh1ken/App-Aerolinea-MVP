package modelo.modulo_seguridad;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Segundo factor de autenticación (RF-28): código de 6 dígitos que cambia cada 30 segundos
 * (TOTP, RFC 6238), compatible con Google Authenticator, Microsoft Authenticator o Authy.
 * El secreto se agrega a la aplicación como "clave manual" (Base32).
 *
 * Para probar sin celular: ejecute esta clase (Run File) y escriba el secreto del usuario.
 */
public final class ServicioTotp {

    private static final String ALFABETO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int PASO_SEGUNDOS = 30;
    private static final int DIGITOS = 6;

    private ServicioTotp() { }

    /** Secreto nuevo de 80 bits, en Base32 (16 caracteres). */
    public static String generarSecreto() {
        byte[] bytes = new byte[10];
        new SecureRandom().nextBytes(bytes);
        return codificar(bytes);
    }

    /** Código válido en este momento para el secreto dado. */
    public static String codigoActual(String secreto) {
        return calcular(secreto, System.currentTimeMillis() / 1000 / PASO_SEGUNDOS, DIGITOS);
    }

    /** Acepta el código del paso anterior, el actual y el siguiente (tolera diferencias de reloj). */
    public static boolean verificar(String secreto, String codigo) {
        if (secreto == null || codigo == null || !codigo.trim().matches("\\d{6}")) {
            return false;
        }
        byte[] recibido = codigo.trim().getBytes();
        long paso = System.currentTimeMillis() / 1000 / PASO_SEGUNDOS;
        boolean valido = false;
        for (long p = paso - 1; p <= paso + 1; p++) {
            // no se corta el ciclo al primer acierto para que el tiempo no dependa del resultado
            valido |= MessageDigest.isEqual(recibido, calcular(secreto, p, DIGITOS).getBytes());
        }
        return valido;
    }

    /** HOTP (RFC 4226) sobre el contador de tiempo. */
    static String calcular(String secreto, long contador, int digitos) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(decodificar(secreto), "HmacSHA1"));
            byte[] h = mac.doFinal(ByteBuffer.allocate(8).putLong(contador).array());
            int desplazamiento = h[h.length - 1] & 0x0F;
            int binario = ((h[desplazamiento] & 0x7F) << 24)
                        | ((h[desplazamiento + 1] & 0xFF) << 16)
                        | ((h[desplazamiento + 2] & 0xFF) << 8)
                        | (h[desplazamiento + 3] & 0xFF);
            int modulo = 1;
            for (int i = 0; i < digitos; i++) modulo *= 10;
            return String.format("%0" + digitos + "d", binario % modulo);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo calcular el código TOTP.", e);
        }
    }

    // ---------- Base32 (RFC 4648) ----------

    static String codificar(byte[] datos) {
        StringBuilder sb = new StringBuilder();
        int buffer = 0;
        int bits = 0;
        for (byte b : datos) {
            buffer = (buffer << 8) | (b & 0xFF);
            bits += 8;
            while (bits >= 5) {
                sb.append(ALFABETO.charAt((buffer >> (bits - 5)) & 31));
                bits -= 5;
            }
        }
        if (bits > 0) {
            sb.append(ALFABETO.charAt((buffer << (5 - bits)) & 31));
        }
        return sb.toString();
    }

    static byte[] decodificar(String texto) {
        String limpio = texto.replace(" ", "").replace("=", "").toUpperCase();
        byte[] salida = new byte[limpio.length() * 5 / 8];
        int buffer = 0;
        int bits = 0;
        int indice = 0;
        for (char c : limpio.toCharArray()) {
            int valor = ALFABETO.indexOf(c);
            if (valor < 0) {
                throw new IllegalArgumentException("Secreto Base32 inválido.");
            }
            buffer = (buffer << 5) | valor;
            bits += 5;
            if (bits >= 8) {
                salida[indice++] = (byte) ((buffer >> (bits - 8)) & 0xFF);
                bits -= 8;
            }
        }
        return salida;
    }

    /** Herramienta de prueba: muestra el código vigente de un secreto. */
    public static void main(String[] args) {
        String secreto = args.length > 0 ? args[0]
                : javax.swing.JOptionPane.showInputDialog("Secreto MFA del usuario (Base32):");
        if (secreto == null || secreto.trim().isEmpty()) {
            return;
        }
        String mensaje = "Código actual: " + codigoActual(secreto.trim())
                + "  (cambia cada " + PASO_SEGUNDOS + " segundos)";
        System.out.println(mensaje);
        if (args.length == 0) {
            javax.swing.JOptionPane.showMessageDialog(null, mensaje);
        }
    }
}
