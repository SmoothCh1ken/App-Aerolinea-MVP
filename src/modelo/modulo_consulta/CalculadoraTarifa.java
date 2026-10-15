package modelo.modulo_consulta;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Regla única de precios de un tramo, la misma que usa el módulo de cambios:
 *   precio_base = precio del vuelo x factor de la categoría del pasajero
 *   impuestos   = precio_base x % de impuesto de la ruta
 */
public final class CalculadoraTarifa {

    // ids de Categoria_pasajero en la base de datos
    public static final int ID_ADULTO = 1;
    public static final int ID_NINO = 2;
    public static final int ID_BEBE = 3;

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private CalculadoraTarifa() { }

    /** Traduce el texto de la categoría que maneja la vista a su id en la BD. */
    public static int idCategoria(String categoria) {
        if ("Niño".equals(categoria)) return ID_NINO;
        if ("Bebé".equals(categoria)) return ID_BEBE;
        return ID_ADULTO;
    }

    public static BigDecimal precioBase(VueloDTO vuelo, BigDecimal factor) {
        return BigDecimal.valueOf(vuelo.getPrecioBase()).multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal impuesto(VueloDTO vuelo, BigDecimal precioBase) {
        return precioBase.multiply(BigDecimal.valueOf(vuelo.getPorcentajeImpuesto()))
                .divide(CIEN, 2, RoundingMode.HALF_UP);
    }

    /** Lo que paga un pasajero por un tramo: base + impuestos (sin servicios). */
    public static BigDecimal totalTramo(VueloDTO vuelo, BigDecimal factor) {
        BigDecimal base = precioBase(vuelo, factor);
        return base.add(impuesto(vuelo, base));
    }
}
