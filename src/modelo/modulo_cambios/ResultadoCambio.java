package modelo.modulo_cambios;

/** Resultado de aplicar un cambio de vuelo: id del cambio y estado de los correos enviados. */
public class ResultadoCambio {
    public int idCambio;
    public ResumenNotificacion correos = new ResumenNotificacion();
}
