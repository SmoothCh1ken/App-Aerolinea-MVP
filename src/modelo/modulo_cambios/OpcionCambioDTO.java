package modelo.modulo_cambios;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Vuelo alterno disponible (misma ruta y misma tarifa). */
public class OpcionCambioDTO {
    public int idVuelo, idTarifa, cupos;
    public String numeroVuelo;
    public LocalDateTime salida, llegada;
    public BigDecimal precio, porcImpuesto;
}
