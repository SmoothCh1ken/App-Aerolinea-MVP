package modelo.modulo_pago;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Una fila del resumen de compra: un tramo (ida o regreso) de un pasajero,
 * con su tarifa, descuentos, impuestos y servicios complementarios.
 */
public class LineaCompra {

    private final String pasajero;
    private final String categoria;
    private final String sentido;
    private final String numeroVuelo;
    private final String origen;
    private final String destino;
    private final Timestamp fechaSalida;
    private final String tarifa;
    private final BigDecimal precioBase;
    private final BigDecimal descuento;
    private final BigDecimal impuestos;
    private final BigDecimal servicios;
    private final String detalleServicios;

    public LineaCompra(String pasajero, String categoria, String sentido, String numeroVuelo,
                       String origen, String destino, Timestamp fechaSalida, String tarifa,
                       BigDecimal precioBase, BigDecimal descuento, BigDecimal impuestos,
                       BigDecimal servicios, String detalleServicios) {
        this.pasajero = pasajero;
        this.categoria = categoria;
        this.sentido = sentido;
        this.numeroVuelo = numeroVuelo;
        this.origen = origen;
        this.destino = destino;
        this.fechaSalida = fechaSalida;
        this.tarifa = tarifa;
        this.precioBase = precioBase;
        this.descuento = descuento;
        this.impuestos = impuestos;
        this.servicios = servicios;
        this.detalleServicios = detalleServicios;
    }

    public String getPasajero() { return pasajero; }
    public String getCategoria() { return categoria; }
    public String getSentido() { return sentido; }
    public String getNumeroVuelo() { return numeroVuelo; }
    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public Timestamp getFechaSalida() { return fechaSalida; }
    public String getTarifa() { return tarifa; }
    public BigDecimal getPrecioBase() { return precioBase; }
    public BigDecimal getDescuento() { return descuento; }
    public BigDecimal getImpuestos() { return impuestos; }
    public BigDecimal getServicios() { return servicios; }
    public String getDetalleServicios() { return detalleServicios; }

    /** precio base - descuento + impuestos + servicios. */
    public BigDecimal getSubtotal() {
        return precioBase.subtract(descuento).add(impuestos).add(servicios);
    }
}
