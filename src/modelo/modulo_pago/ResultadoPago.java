package modelo.modulo_pago;

import java.util.ArrayList;
import java.util.List;

/** Resultado de un intento de pago (RF-19 y RF-20). */
public class ResultadoPago {

    private String estadoPago;          // Aprobado, Rechazado, Pendiente, ...
    private String mensaje;
    private String referencia;
    private boolean reservaConfirmada;
    private boolean puedeReintentar;
    private boolean correoEnviado;
    private final List<String> tiquetes = new ArrayList<>();

    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public boolean isReservaConfirmada() { return reservaConfirmada; }
    public void setReservaConfirmada(boolean v) { this.reservaConfirmada = v; }
    public boolean isPuedeReintentar() { return puedeReintentar; }
    public void setPuedeReintentar(boolean v) { this.puedeReintentar = v; }
    public boolean isCorreoEnviado() { return correoEnviado; }
    public void setCorreoEnviado(boolean v) { this.correoEnviado = v; }
    public List<String> getTiquetes() { return tiquetes; }
}
