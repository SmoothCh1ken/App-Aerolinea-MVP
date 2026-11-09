package modelo.modulo_seguridad;

import java.util.EnumSet;
import java.util.Set;

/**
 * Sesión del funcionario que inició sesión (la aplicación es de escritorio y de un solo usuario a la vez).
 * Guarda quién es, a qué módulos puede entrar (RF-27) y cuándo fue su última actividad (RF-30).
 */
public final class SesionActual {

    private static Usuario usuario;
    private static Set<Modulo> modulos = EnumSet.noneOf(Modulo.class);
    private static long limiteInactividadMs;
    private static volatile long ultimaActividad;

    private SesionActual() { }

    public static synchronized void iniciar(Usuario u, Set<Modulo> permitidos, int minutosInactividad) {
        usuario = u;
        modulos = EnumSet.noneOf(Modulo.class);
        modulos.addAll(permitidos);
        limiteInactividadMs = minutosInactividad * 60_000L;
        registrarActividad();
    }

    public static synchronized void cerrar() {
        usuario = null;
        modulos = EnumSet.noneOf(Modulo.class);
    }

    public static synchronized boolean activa() { return usuario != null; }

    public static synchronized Usuario getUsuario() { return usuario; }

    public static synchronized boolean tienePermiso(Modulo modulo) { return modulos.contains(modulo); }

    public static synchronized long getLimiteInactividadMs() { return limiteInactividadMs; }

    public static void registrarActividad() { ultimaActividad = System.currentTimeMillis(); }

    public static long milisegundosInactivo() { return System.currentTimeMillis() - ultimaActividad; }
}
