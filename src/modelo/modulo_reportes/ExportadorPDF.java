package modelo.modulo_reportes;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Exportador de reportes gerenciales a formato PDF utilizando iText 5.
 */
public final class ExportadorPDF {

    private ExportadorPDF() { }

    public static void exportar(File archivo, ResultadoReporte reporte) throws IOException {
        // Usamos formato carta en orientación horizontal (rotate) para que las tablas con varias columnas luzcan bien
        Document documento = new Document(PageSize.LETTER.rotate(), 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(archivo));
            documento.open();

            // Fuentes corporativas
            Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, new BaseColor(25, 115, 210));
            Font fuenteSub = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
            Font fuenteEncabezadoTabla = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
            Font fuenteCelda = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.DARK_GRAY);

            // Encabezado del documento
            Paragraph titulo = new Paragraph("Aerolínea MVP - " + reporte.getTitulo(), fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);

            String fechaGen = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            Paragraph sub = new Paragraph("Generado el: " + fechaGen, fuenteSub);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(15);
            documento.add(sub);

            // Construcción de la tabla
            String[] columnas = reporte.getColumnas();
            PdfPTable tabla = new PdfPTable(columnas.length);
            tabla.setWidthPercentage(100);

            // Celdas de encabezado
            for (String col : columnas) {
                PdfPCell celda = new PdfPCell(new Phrase(col, fuenteEncabezadoTabla));
                celda.setBackgroundColor(new BaseColor(25, 115, 210));
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setPadding(6);
                tabla.addCell(celda);
            }

            // Filas de datos con alternancia de colores para legibilidad
            boolean alternar = false;
            for (Object[] fila : reporte.getFilas()) {
                BaseColor colorFondo = alternar ? new BaseColor(245, 245, 245) : BaseColor.WHITE;
                for (int i = 0; i < fila.length; i++) {
                    String valor = fila[i] != null ? fila[i].toString() : "";
                    PdfPCell celda = new PdfPCell(new Phrase(valor, fuenteCelda));
                    celda.setBackgroundColor(colorFondo);
                    celda.setPadding(5);
                    
                    // Alineación a la derecha si es columna monetaria o numérica
                    if (reporte.esMoneda(i) || esNumero(valor)) {
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    } else {
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                    }
                    
                    tabla.addCell(celda);
                }
                alternar = !alternar;
            }

            documento.add(tabla);
            documento.close();

        } catch (DocumentException e) {
            throw new IOException("Error al generar el documento PDF: " + e.getMessage(), e);
        }
    }

    private static boolean esNumero(String str) {
        if (str == null || str.isEmpty()) return false;
        String limpio = str.replace("$", "").replace(".", "").replace(",", "").replace("%", "").trim();
        try {
            Double.parseDouble(limpio);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}