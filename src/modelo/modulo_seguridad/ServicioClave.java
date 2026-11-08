package modelo.modulo_seguridad;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Manejo seguro de contraseñas: nunca se guardan en claro. Se guarda una sal aleatoria
 * por usuario y el resultado de PBKDF2 (HMAC-SHA256, 120.000 iteraciones).
 */
public final class ServicioClave {

    private static final int ITERACIONES = 120_000;
    private static final int BITS = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private ServicioClave() { }

    /** Sal aleatoria de 16 bytes, en Base64. */
    public static String nuevaSal() {
        byte[] sal = new byte[16];
        ALEATORIO.nextBytes(sal);
        return Base64.getEncoder().encodeToString(sal);
    }

    public static String hash(char[] clave, String salBase64) {
        PBEKeySpec spec = new PBEKeySpec(clave, Base64.getDecoder().decode(salBase64), ITERACIONES, BITS);
        try {
            SecretKeyFactory fabrica = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return Base64.getEncoder().encodeToString(fabrica.generateSecret(spec).getEncoded());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña.", e);
        } finally {
            spec.clearPassword();
        }
    }

    /** Compara en tiempo constante para no filtrar información por la duración de la comparación. */
    public static boolean verificar(char[] clave, String salBase64, String hashEsperado) {
        byte[] calculado = Base64.getDecoder().decode(hash(clave, salBase64));
        byte[] guardado = Base64.getDecoder().decode(hashEsperado);
        return MessageDigest.isEqual(calculado, guardado);
    }

    /** @return el mensaje de error si la contraseña no cumple la política, o null si es válida. */
    public static String validarPolitica(char[] clave, String usuario) {
        if (clave == null || clave.length < 8) {
            return "La contraseña debe tener al menos 8 caracteres.";
        }
        if (clave.length > 64) {
            return "La contraseña no puede superar los 64 caracteres.";
        }
        boolean letra = false;
        boolean digito = false;
        for (char c : clave) {
            if (Character.isLetter(c)) letra = true;
            if (Character.isDigit(c)) digito = true;
        }
        if (!letra || !digito) {
            return "La contraseña debe combinar letras y números.";
        }
        if (usuario != null && new String(clave).equalsIgnoreCase(usuario)) {
            return "La contraseña no puede ser igual al nombre de usuario.";
        }
        return null;
    }
}
