package modelo.modulo_pago;

/**
 * Datos que la vista entrega al modelo para procesar un pago.
 * IMPORTANTE: nunca se guardan número de tarjeta ni CVV; solo se usan en la vista.
 */
public class SolicitudPago {

    private final MedioPago medioPago;
    private final String referenciaCliente; // p. ej. número de comprobante de la transferencia
    private final String codigoBono;        // solo para bono corporativo
    private final String resultadoSimulado; // estado que devolverá la pasarela simulada

    public SolicitudPago(MedioPago medioPago, String referenciaCliente,
                         String codigoBono, String resultadoSimulado) {
        this.medioPago = medioPago;
        this.referenciaCliente = referenciaCliente;
        this.codigoBono = codigoBono;
        this.resultadoSimulado = resultadoSimulado;
    }

    public MedioPago getMedioPago() { return medioPago; }
    public String getReferenciaCliente() { return referenciaCliente; }
    public String getCodigoBono() { return codigoBono; }
    public String getResultadoSimulado() { return resultadoSimulado; }
}
