package modelo.modulo_pago;

import java.math.BigDecimal;

/** Entidad de la tabla Pago. Los estados coinciden con el ENUM de la BD. */
public class Pago {

    public static final String PENDIENTE = "Pendiente";
    public static final String APROBADO = "Aprobado";
    public static final String RECHAZADO = "Rechazado";
    public static final String CANCELADO = "Cancelado";
    public static final String EXPIRADO = "Expirado";
    public static final String ERROR_COMUNICACION = "Error de comunicaci\u00f3n";

    private int idPago;
    private int idReserva;
    private int idMedioPago;
    private BigDecimal valor;
    private String estado;
    private String referenciaExterna;
    private Integer idBono;
    private Integer millasUsadas;

    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }
    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }
    public int getIdMedioPago() { return idMedioPago; }
    public void setIdMedioPago(int idMedioPago) { this.idMedioPago = idMedioPago; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getReferenciaExterna() { return referenciaExterna; }
    public void setReferenciaExterna(String referenciaExterna) { this.referenciaExterna = referenciaExterna; }
    public Integer getIdBono() { return idBono; }
    public void setIdBono(Integer idBono) { this.idBono = idBono; }
    public Integer getMillasUsadas() { return millasUsadas; }
    public void setMillasUsadas(Integer millasUsadas) { this.millasUsadas = millasUsadas; }
}
