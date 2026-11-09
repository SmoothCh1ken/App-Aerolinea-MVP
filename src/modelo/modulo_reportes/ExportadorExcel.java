package modelo.modulo_reportes;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Exportador de reportes gerenciales a formato Excel (.xlsx) utilizando Apache POI.
 */
public final class ExportadorExcel {

    private ExportadorExcel() { }

    public static void exportar(File archivo, ResultadoReporte reporte) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte Gerencial");

            // Estilos corporativos
            Font fontHeader = workbook.createFont();
            fontHeader.setBold(true);
            fontHeader.setColor(IndexedColors.WHITE.getIndex());

            CellStyle styleHeader = workbook.createCellStyle();
            styleHeader.setFont(fontHeader);
            styleHeader.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleHeader.setAlignment(HorizontalAlignment.CENTER);
            styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);

            // Estilos celdas normales y numéricas
            CellStyle styleTexto = workbook.createCellStyle();
            styleTexto.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle styleNumero = workbook.createCellStyle();
            styleNumero.setAlignment(HorizontalAlignment.RIGHT);
            styleNumero.setVerticalAlignment(VerticalAlignment.CENTER);

            int rowIndex = 0;

            // 1. Fila de Encabezados
            Row headerRow = sheet.createRow(rowIndex++);
            headerRow.setHeightInPoints(24);
            String[] columnas = reporte.getColumnas();
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(styleHeader);
            }

            // 2. Filas de Datos
            for (Object[] filaDatos : reporte.getFilas()) {
                Row row = sheet.createRow(rowIndex++);
                row.setHeightInPoints(20);
                for (int i = 0; i < filaDatos.length; i++) {
                    Cell cell = row.createCell(i);
                    Object valor = filaDatos[i];

                    if (valor == null) {
                        cell.setCellValue("");
                        cell.setCellStyle(styleTexto);
                    } else if (valor instanceof BigDecimal) {
                        cell.setCellValue(((BigDecimal) valor).doubleValue());
                        cell.setCellStyle(styleNumero);
                    } else if (valor instanceof Double) {
                        cell.setCellValue((Double) valor);
                        cell.setCellStyle(styleNumero);
                    } else if (valor instanceof Long) {
                        cell.setCellValue((Long) valor);
                        cell.setCellStyle(styleNumero);
                    } else if (valor instanceof Integer) {
                        cell.setCellValue((Integer) valor);
                        cell.setCellStyle(styleNumero);
                    } else {
                        cell.setCellValue(valor.toString());
                        cell.setCellStyle(styleTexto);
                    }
                }
            }

            // 3. Autoajustar el ancho de todas las columnas para que no se corten los textos
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
                // Añadir un pequeño margen extra de seguridad para el ancho
                int anchoActual = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.max(anchoActual + 1024, 4000));
            }

            // Guardar archivo en disco
            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                workbook.write(fos);
            }
        }
    }
}