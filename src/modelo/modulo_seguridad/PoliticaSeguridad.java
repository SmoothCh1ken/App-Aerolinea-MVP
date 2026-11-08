package modelo.modulo_seguridad;

/** Valores configurables de la política de seguridad (tabla Politica_seguridad). */
public class PoliticaSeguridad {

    private final int maxIntentos;
    private final int minutosBloqueo;
    private final int minutosInactividad;

    public PoliticaSeguridad(int maxIntentos, int minutosBloqueo, int minutosInactividad) {
        this.maxIntentos = maxIntentos;
        this.minutosBloqueo = minutosBloqueo;
        this.minutosInactividad = minutosInactividad;
    }

    public int getMaxIntentos() { return maxIntentos; }
    public int getMinutosBloqueo() { return minutosBloqueo; }
    public int getMinutosInactividad() { return minutosInactividad; }
}
