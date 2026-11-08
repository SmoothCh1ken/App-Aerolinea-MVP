package modelo.modulo_pago;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;

/**
 * Utilidad para generar el Boarding Pass (Tiquete) en formato PDF directamente en memoria RAM.
 */
public class GeneradorTiquetePDF {

    private GeneradorTiquetePDF() { }

    /**
     * Dibuja el tiquete en PDF y lo devuelve como un arreglo de bytes (ideal para adjuntar en correos).
     */
    public static byte[] generar(String codigoReserva, String nombrePasajero, String documento, 
                                 String numVuelo, String origen, String destino, String fecha, String clase) {
        
        // Configuramos la hoja en tamaño A4 horizontal (apaisado) para simular un tiquete
        Document document = new Document(PageSize.A4.rotate(), 40, 40, 40, 40);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            // 1. Cabecera (Logo/Nombre Aerolínea)
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, new BaseColor(0, 102, 204)); // Azul corporativo
            Paragraph titulo = new Paragraph("AirColombia - Boarding Pass", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(30);
            document.add(titulo);

            // 2. Tabla de Datos dividida en 2 columnas (Pasajero a la izq, Vuelo a la der)
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingAfter(40);

            // Columna Izquierda: Datos del Pasajero
            PdfPCell celdaPasajero = new PdfPCell();
            celdaPasajero.setBorderColor(BaseColor.LIGHT_GRAY);
            celdaPasajero.setPadding(15);
            celdaPasajero.addElement(new Paragraph("DATOS DEL PASAJERO", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            celdaPasajero.addElement(new Paragraph("Nombre: " + nombrePasajero));
            celdaPasajero.addElement(new Paragraph("Documento: " + documento));
            celdaPasajero.addElement(new Paragraph("Clase: " + clase));
            
            // Columna Derecha: Datos del Vuelo
            PdfPCell celdaVuelo = new PdfPCell();
            celdaVuelo.setBorderColor(BaseColor.LIGHT_GRAY);
            celdaVuelo.setPadding(15);
            celdaVuelo.addElement(new Paragraph("DATOS DEL VUELO", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            celdaVuelo.addElement(new Paragraph("Reserva: " + codigoReserva));
            celdaVuelo.addElement(new Paragraph("Vuelo: " + numVuelo));
            celdaVuelo.addElement(new Paragraph("Ruta: " + origen + " -> " + destino));
            celdaVuelo.addElement(new Paragraph("Fecha: " + fecha));

            table.addCell(celdaPasajero);
            table.addCell(celdaVuelo);
            document.add(table);

            // 3. Código de Barras Simulado (Para realismo)
            Barcode128 barcode = new Barcode128();
            barcode.setCode(codigoReserva + "-" + documento); // Genera un código único basado en la reserva
            Image imgBarcode = barcode.createImageWithBarcode(writer.getDirectContent(), BaseColor.BLACK, BaseColor.BLACK);
            imgBarcode.setAlignment(Element.ALIGN_CENTER);
            imgBarcode.scalePercent(150);
            document.add(imgBarcode);
            
            // 4. Pie de página
            Paragraph textoAgradecimiento = new Paragraph("¡Gracias por volar con AirColombia!", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
            textoAgradecimiento.setAlignment(Element.ALIGN_CENTER);
            textoAgradecimiento.setSpacingBefore(15f);
            document.add(textoAgradecimiento);

            document.close();
        } catch (Exception e) {
            System.err.println("[Generador PDF] Error al generar el PDF del tiquete: " + e.getMessage());
            e.printStackTrace();
        }

        return baos.toByteArray(); // Devuelve el PDF completo alojado en la RAM
    }
}