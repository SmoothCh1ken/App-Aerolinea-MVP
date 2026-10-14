package modelo.modulo_pago;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Resumen de la compra (RF-17): datos de la reserva, del comprador y todas las
 * líneas (pasajeros, itinerario, tarifas, servicios, impuestos y descuentos).
 */
public class ResumenCompra {

    public static final String ESTADO_PENDIENTE = "Pendiente de pago";

    private final int idReserva;
    private final String codigoReserva;
    private final String estadoReserva;
    private final Timestamp fechaExpiracion;
    private final int idComprador;
    private final String nombreComprador;
    private final String correoComprador;
    private final List<LineaCompra> lineas = new ArrayList<>();

    public ResumenCompra(int idReserva, String codigoReserva, String estadoReserva,
                         Timestamp fechaExpiracion, int idComprador,
                         String nombreComprador, String correoComprador) {
        this.idReserva = idReserva;
        this.codigoReserva = codigoReserva;
        this.estadoReserva = estadoReserva;
        this.fechaExpiracion = fechaExpiracion;
        this.idComprador = idComprador;
        this.nombreComprador = nombreComprador;
        this.correoComprador = correoComprador;
    }

    public void agregarLinea(LineaCompra l) { lineas.add(l); }

    public int getIdReserva() { return idReserva; }
    public String getCodigoReserva() { return codigoReserva; }
    public String getEstadoReserva() { return estadoReserva; }
    public Timestamp getFechaExpiracion() { return fechaExpiracion; }
    public int getIdComprador() { return idComprador; }
    public String getNombreComprador() { return nombreComprador; }
    public String getCorreoComprador() { return correoComprador; }
    public List<LineaCompra> getLineas() { return lineas; }

    /** Solo las reservas "Pendiente de pago" admiten un pago. */
    public boolean isPagable() { return ESTADO_PENDIENTE.equals(estadoReserva); }

    public BigDecimal getTotalBase() {
        BigDecimal t = BigDecimal.ZERO;
        for (LineaCompra l : lineas) t = t.add(l.getPrecioBase());
        return t;
    }

    public BigDecimal getTotalDescuentos() {
        BigDecimal t = BigDecimal.ZERO;
        for (LineaCompra l : lineas) t = t.add(l.getDescuento());
        return t;
    }

    public BigDecimal getTotalImpuestos() {
        BigDecimal t = BigDecimal.ZERO;
        for (LineaCompra l : lineas) t = t.add(l.getImpuestos());
        return t;
    }

    public BigDecimal getTotalServicios() {
        BigDecimal t = BigDecimal.ZERO;
        for (LineaCompra l : lineas) t = t.add(l.getServicios());
        return t;
    }

    /** Total a pagar = base - descuentos + impuestos + servicios. */
    public BigDecimal getTotal() {
        return getTotalBase().subtract(getTotalDescuentos())
                .add(getTotalImpuestos()).add(getTotalServicios());
    }
}
