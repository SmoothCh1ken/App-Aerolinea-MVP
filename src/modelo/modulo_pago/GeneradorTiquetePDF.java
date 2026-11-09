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
import java.util.List;

/**
 * Utilidad para generar el Boarding Pass (Tiquete) en formato PDF directamente en memoria RAM.
 * Un PDF por pasajero: una página por cada tramo (Ida y, si existe, Regreso).
 */
public class GeneradorTiquetePDF {

    /** Datos de un tramo (una página del PDF). */
    public static class Tramo {
        public final String sentido, numeroVuelo, origen, destino, fecha, clase;

        public Tramo(String sentido, String numeroVuelo, String origen, String destino,
                     String fecha, String clase) {
            this.sentido = sentido; this.numeroVuelo = numeroVuelo;
            this.origen = origen; this.destino = destino;
            this.fecha = fecha; this.clase = clase;
        }
    }

    private GeneradorTiquetePDF() { }

    /**
     * Dibuja el tiquete en PDF y lo devuelve como un arreglo de bytes (ideal para adjuntar en correos).
     */
    public static byte[] generar(String codigoReserva, String numeroTiquete,
                                 String nombrePasajero, String documento, List<Tramo> tramos) {

        // Configuramos la hoja en tamaño A4 horizontal (apaisado) para simular un tiquete
        Document document = new Document(PageSize.A4.rotate(), 40, 40, 40, 40);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            for (int i = 0; i < tramos.size(); i++) {
                Tramo t = tramos.get(i);
                if (i > 0) document.newPage();

                // 1. Cabecera (Logo/Nombre Aerolínea)
                Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, new BaseColor(0, 102, 204)); // Azul corporativo
                Paragraph titulo = new Paragraph("AirColombia - Boarding Pass", fontTitulo);
                titulo.setAlignment(Element.ALIGN_CENTER);
                titulo.setSpacingAfter(5);
                document.add(titulo);

                Paragraph tramo = new Paragraph("TRAMO DE " + t.sentido.toUpperCase(),
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY));
                tramo.setAlignment(Element.ALIGN_CENTER);
                tramo.setSpacingAfter(25);
                document.add(tramo);

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
                celdaPasajero.addElement(new Paragraph("Clase: " + t.clase));

                // Columna Derecha: Datos del Vuelo
                PdfPCell celdaVuelo = new PdfPCell();
                celdaVuelo.setBorderColor(BaseColor.LIGHT_GRAY);
                celdaVuelo.setPadding(15);
                celdaVuelo.addElement(new Paragraph("DATOS DEL VUELO", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                celdaVuelo.addElement(new Paragraph("Reserva: " + codigoReserva));
                celdaVuelo.addElement(new Paragraph("Tiquete: " + numeroTiquete));
                celdaVuelo.addElement(new Paragraph("Vuelo: " + t.numeroVuelo));
                celdaVuelo.addElement(new Paragraph("Ruta: " + t.origen + " -> " + t.destino));
                celdaVuelo.addElement(new Paragraph("Fecha: " + t.fecha));

                table.addCell(celdaPasajero);
                table.addCell(celdaVuelo);
                document.add(table);

                // 3. Código de Barras Simulado (único por tiquete y tramo)
                Barcode128 barcode = new Barcode128();
                barcode.setCode(numeroTiquete + "-" + t.sentido);
                Image imgBarcode = barcode.createImageWithBarcode(writer.getDirectContent(), BaseColor.BLACK, BaseColor.BLACK);
                imgBarcode.setAlignment(Element.ALIGN_CENTER);
                imgBarcode.scalePercent(150);
                document.add(imgBarcode);

                // 4. Pie de página
                Paragraph textoAgradecimiento = new Paragraph("¡Gracias por volar con AirColombia!", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
                textoAgradecimiento.setAlignment(Element.ALIGN_CENTER);
                textoAgradecimiento.setSpacingBefore(15f);
                document.add(textoAgradecimiento);
            }

            document.close();
        } catch (Exception e) {
            System.err.println("[Generador PDF] Error al generar el PDF del tiquete: " + e.getMessage());
            e.printStackTrace();
        }

        return baos.toByteArray(); // Devuelve el PDF completo alojado en la RAM
    }
}
