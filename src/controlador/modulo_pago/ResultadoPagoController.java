package controlador.modulo_pago;

import modelo.modulo_pago.ResultadoPago;
import vista.modulo_pago.ResultadoPagoFrame;

/** Muestra el resultado del pago y gestiona "Reintentar" / "Finalizar". */
public class ResultadoPagoController {

    public ResultadoPagoController(ResultadoPagoFrame vista, ResultadoPago resultado,
                                   Runnable alReintentar, Runnable alFinalizar) {
        vista.mostrar(resultado);
        vista.addReintentarListener(e -> alReintentar.run());
        vista.addFinalizarListener(e -> alFinalizar.run());
        vista.addCierreListener(alFinalizar);
    }
}
