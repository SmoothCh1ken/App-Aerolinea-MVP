package modelo.modulo_reportes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Valores del tablero ejecutivo (RF-34). */
public class IndicadoresTablero {

    private BigDecimal ventasTotal = BigDecimal.ZERO;
    private long tiquetesVendidos;
    private BigDecimal ingresosServicios = BigDecimal.ZERO;
    private double ocupacionPromedio;
    private long reservasConfirmadas;
    private long reservasPendientes;
    private long vuelosReprogramados;
    private long vuelosCancelados;
    private long pagosAprobados;
    private long pagosTotal;
    private BigDecimal reembolsos = BigDecimal.ZERO;
    private LocalDateTime actualizadoEn;

    /** % de pagos aprobados sobre el total de intentos de pago. */
    public double tasaAprobacionPagos() {
        return pagosTotal == 0 ? 0 : pagosAprobados * 100.0 / pagosTotal;
    }

    public BigDecimal getVentasTotal() { return ventasTotal; }
    public void setVentasTotal(BigDecimal v) { this.ventasTotal = v; }
    public long getTiquetesVendidos() { return tiquetesVendidos; }
    public void setTiquetesVendidos(long v) { this.tiquetesVendidos = v; }
    public BigDecimal getIngresosServicios() { return ingresosServicios; }
    public void setIngresosServicios(BigDecimal v) { this.ingresosServicios = v; }
    public double getOcupacionPromedio() { return ocupacionPromedio; }
    public void setOcupacionPromedio(double v) { this.ocupacionPromedio = v; }
    public long getReservasConfirmadas() { return reservasConfirmadas; }
    public void setReservasConfirmadas(long v) { this.reservasConfirmadas = v; }
    public long getReservasPendientes() { return reservasPendientes; }
    public void setReservasPendientes(long v) { this.reservasPendientes = v; }
    public long getVuelosReprogramados() { return vuelosReprogramados; }
    public void setVuelosReprogramados(long v) { this.vuelosReprogramados = v; }
    public long getVuelosCancelados() { return vuelosCancelados; }
    public void setVuelosCancelados(long v) { this.vuelosCancelados = v; }
    public long getPagosAprobados() { return pagosAprobados; }
    public void setPagosAprobados(long v) { this.pagosAprobados = v; }
    public long getPagosTotal() { return pagosTotal; }
    public void setPagosTotal(long v) { this.pagosTotal = v; }
    public BigDecimal getReembolsos() { return reembolsos; }
    public void setReembolsos(BigDecimal v) { this.reembolsos = v; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime v) { this.actualizadoEn = v; }
}
