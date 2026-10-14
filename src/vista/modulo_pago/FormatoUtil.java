package vista.modulo_pago;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formatos de moneda y fecha para mostrar en pantalla. */
public final class FormatoUtil {

    private static final NumberFormat MONEDA = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static {
        MONEDA.setMinimumFractionDigits(0);
        MONEDA.setMaximumFractionDigits(0);
    }

    private FormatoUtil() { }

    public static String moneda(BigDecimal valor) {
        return valor == null ? "-" : MONEDA.format(valor);
    }

    public static String fecha(Timestamp t) {
        return t == null ? "-" : t.toLocalDateTime().format(FECHA);
    }
}
