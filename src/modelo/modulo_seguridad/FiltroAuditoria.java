package modelo.modulo_seguridad;

import java.time.LocalDate;

/** Filtros de la consulta de auditoría. Un valor null o vacío significa "sin filtro". */
public class FiltroAuditoria {

    private LocalDate desde;
    private LocalDate hasta;
    private String usuario;
    private String evento;
    private String resultado;

    public LocalDate getDesde() { return desde; }
    public void setDesde(LocalDate v) { this.desde = v; }
    public LocalDate getHasta() { return hasta; }
    public void setHasta(LocalDate v) { this.hasta = v; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String v) { this.usuario = v; }
    public String getEvento() { return evento; }
    public void setEvento(String v) { this.evento = v; }
    public String getResultado() { return resultado; }
    public void setResultado(String v) { this.resultado = v; }
}
