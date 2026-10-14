package modelo.modulo_cambios;

import java.math.BigDecimal;

/** Resultado de calcular un cambio o un reembolso. */
public class CotizacionDTO {
    public String motivo;

    // Cambio: neto > 0 = valor a pagar; neto < 0 = saldo a favor
    public BigDecimal cargo = BigDecimal.ZERO;
    public BigDecimal diferencia = BigDecimal.ZERO;
    public BigDecimal neto = BigDecimal.ZERO;

    // Reembolso
    public BigDecimal base = BigDecimal.ZERO;
    public BigDecimal penalidad = BigDecimal.ZERO;
    public BigDecimal reembolso = BigDecimal.ZERO;
}