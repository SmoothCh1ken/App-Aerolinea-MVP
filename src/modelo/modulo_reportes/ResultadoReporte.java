package modelo.modulo_reportes;

import java.util.ArrayList;
import java.util.List;

/** Tabla de resultados de un reporte: columnas, filas y cuáles columnas son dinero. */
public class ResultadoReporte {

    private final String titulo;
    private final String[] columnas;
    private final int[] columnasMoneda;
    private final List<Object[]> filas = new ArrayList<>();

    public ResultadoReporte(String titulo, String[] columnas, int[] columnasMoneda) {
        this.titulo = titulo;
        this.columnas = columnas;
        this.columnasMoneda = columnasMoneda;
    }

    public void agregarFila(Object[] fila) { filas.add(fila); }

    public String getTitulo() { return titulo; }
    public String[] getColumnas() { return columnas; }
    public List<Object[]> getFilas() { return filas; }

    public boolean esMoneda(int columna) {
        for (int c : columnasMoneda) {
            if (c == columna) return true;
        }
        return false;
    }
}
