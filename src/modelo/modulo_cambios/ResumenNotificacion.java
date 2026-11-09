package modelo.modulo_cambios;

/** Cuántos correos se intentaron enviar y cuántos salieron realmente. */
public class ResumenNotificacion {
    public int total, enviados;

    public void agregar(boolean enviado) {
        total++;
        if (enviado) {
            enviados++;
        }
    }

    public int fallidos() {
        return total - enviados;
    }
}
