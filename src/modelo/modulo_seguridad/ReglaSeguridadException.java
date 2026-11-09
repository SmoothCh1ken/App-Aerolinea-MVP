package modelo.modulo_seguridad;

/** Se lanza cuando no se cumple una regla de seguridad; el mensaje es para mostrárselo al usuario. */
public class ReglaSeguridadException extends Exception {

    private static final long serialVersionUID = 1L;

    public ReglaSeguridadException(String mensaje) {
        super(mensaje);
    }
}
