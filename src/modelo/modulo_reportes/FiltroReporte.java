package modelo.modulo_reportes;

import java.time.LocalDate;

/** Filtros del RF-33: fechas, ciudades, ruta y período. Un valor null significa "sin filtro". */
public class FiltroReporte {

    private LocalDate desde;
    private LocalDate hasta;
    private Integer idCiudadOrigen;
    private Integer idCiudadDestino;
    private Integer idRuta;
    private Periodo periodo = Periodo.MES;

    public LocalDate getDesde() { return desde; }
    public void setDesde(LocalDate desde) { this.desde = desde; }
    public LocalDate getHasta() { return hasta; }
    public void setHasta(LocalDate hasta) { this.hasta = hasta; }
    public Integer getIdCiudadOrigen() { return idCiudadOrigen; }
    public void setIdCiudadOrigen(Integer id) { this.idCiudadOrigen = id; }
    public Integer getIdCiudadDestino() { return idCiudadDestino; }
    public void setIdCiudadDestino(Integer id) { this.idCiudadDestino = id; }
    public Integer getIdRuta() { return idRuta; }
    public void setIdRuta(Integer idRuta) { this.idRuta = idRuta; }
    public Periodo getPeriodo() { return periodo; }
    public void setPeriodo(Periodo periodo) { this.periodo = periodo; }

    public boolean tieneGeografia() {
        return idCiudadOrigen != null || idCiudadDestino != null || idRuta != null;
    }
}
