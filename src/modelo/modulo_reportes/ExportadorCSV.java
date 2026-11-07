package modelo.modulo_reportes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

/**
 * Guarda un reporte como CSV. Usa ";" como separador y UTF-8 con BOM
 * para que Excel en español abra bien las columnas y las tildes.
 */
public final class ExportadorCSV {

    private ExportadorCSV() { }

    public static void exportar(File archivo, ResultadoReporte reporte) throws IOException {
        try (Writer w = new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8)) {
            w.write('\uFEFF');
            String[] columnas = reporte.getColumnas();
            Object[] encabezado = new Object[columnas.length];
            System.arraycopy(columnas, 0, encabezado, 0, columnas.length);
            w.write(linea(encabezado));
            for (Object[] fila : reporte.getFilas()) {
                w.write(linea(fila));
            }
        }
    }

    private static String linea(Object[] valores) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) sb.append(';');
            sb.append(celda(valores[i]));
        }
        return sb.append("\r\n").toString();
    }

    private static String celda(Object valor) {
        String texto = valor instanceof BigDecimal ? ((BigDecimal) valor).toPlainString() : String.valueOf(valor);
        if (texto.contains(";") || texto.contains("\"") || texto.contains("\n")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }
}
