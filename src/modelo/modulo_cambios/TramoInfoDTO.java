package modelo.modulo_cambios;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Un tramo (ida o regreso) de un pasajero, con las condiciones de su tarifa. */
public class TramoInfoDTO {
    public int idTramo, idVuelo, idRuta, idTarifa, horasMinimas;
    public String pasajero, categoria, sentido, numeroVuelo, origen, destino, estadoVuelo, tarifa;
    public LocalDateTime salida, llegada;
    public BigDecimal factor, cargoCambio, penalidad, precioBase, descuento, impuestos;
    public boolean ocupaCupo, permiteCambios, permiteReembolso;

    // Precios que tendrá el tramo si se aplica un cambio de vuelo (los calcula el controlador)
    public BigDecimal nuevoPrecioBase, nuevoImpuestos;

    /** Total que pagó el pasajero por este tramo. */
    public BigDecimal total() {
        return precioBase.subtract(descuento).add(impuestos);
    }
}
