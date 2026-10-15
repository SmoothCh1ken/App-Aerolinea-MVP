package modelo.modulo_consulta;

import java.sql.Timestamp;

/**
 * Objeto DTO (Data Transfer Object) expandido para cumplir con el RF-03.
 * Transporta los datos consolidados de la búsqueda de vuelos hacia la Vista.
 */
public class VueloDTO {

    // Identificadores para la lógica de reserva
    private int idVuelo;
    private int idTarifa;

    // Datos Operacionales (RF-03)
    private String numeroVuelo;
    private String aeropuertoOrigen;
    private String aeropuertoDestino;
    private Timestamp fechaHoraSalida;
    private Timestamp fechaHoraLlegada;
    private String duracion; // Se calculará en formato "Xh Ym"
    private int escalas;
    private String detalleEscala;
    private String aeronave; // Matrícula o Modelo

    // Datos Comerciales y Tarifarios (RF-03 y Filtros)
    private String claseServicio;
    private String nombreTarifa;
    private double precioBase;
    private double porcentajeImpuesto; // % de impuesto de la ruta
    private int cuposDisponibles;
    private int equipajeIncluido; // Para el filtro de equipaje
    private String restricciones; // Ej: "Cambios: Sí | Reembolso: No"

    // Constructor vacío
    public VueloDTO() {
    }

    // --- GETTERS Y SETTERS ---

    public int getIdVuelo() { return idVuelo; }
    public void setIdVuelo(int idVuelo) { this.idVuelo = idVuelo; }

    public int getIdTarifa() { return idTarifa; }
    public void setIdTarifa(int idTarifa) { this.idTarifa = idTarifa; }

    public String getNumeroVuelo() { return numeroVuelo; }
    public void setNumeroVuelo(String numeroVuelo) { this.numeroVuelo = numeroVuelo; }

    public String getAeropuertoOrigen() { return aeropuertoOrigen; }
    public void setAeropuertoOrigen(String aeropuertoOrigen) { this.aeropuertoOrigen = aeropuertoOrigen; }

    public String getAeropuertoDestino() { return aeropuertoDestino; }
    public void setAeropuertoDestino(String aeropuertoDestino) { this.aeropuertoDestino = aeropuertoDestino; }

    public Timestamp getFechaHoraSalida() { return fechaHoraSalida; }
    public void setFechaHoraSalida(Timestamp fechaHoraSalida) { this.fechaHoraSalida = fechaHoraSalida; }

    public Timestamp getFechaHoraLlegada() { return fechaHoraLlegada; }
    public void setFechaHoraLlegada(Timestamp fechaHoraLlegada) { this.fechaHoraLlegada = fechaHoraLlegada; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    public int getEscalas() { return escalas; }
    public void setEscalas(int escalas) { this.escalas = escalas; }

    public String getDetalleEscala() { return detalleEscala; }
    public void setDetalleEscala(String detalleEscala) { this.detalleEscala = detalleEscala; }

    public String getAeronave() { return aeronave; }
    public void setAeronave(String aeronave) { this.aeronave = aeronave; }

    public String getClaseServicio() { return claseServicio; }
    public void setClaseServicio(String claseServicio) { this.claseServicio = claseServicio; }

    public String getNombreTarifa() { return nombreTarifa; }
    public void setNombreTarifa(String nombreTarifa) { this.nombreTarifa = nombreTarifa; }

    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }

    public int getCuposDisponibles() { return cuposDisponibles; }
    public void setCuposDisponibles(int cuposDisponibles) { this.cuposDisponibles = cuposDisponibles; }

    public int getEquipajeIncluido() { return equipajeIncluido; }
    public void setEquipajeIncluido(int equipajeIncluido) { this.equipajeIncluido = equipajeIncluido; }

    public double getPorcentajeImpuesto() { return porcentajeImpuesto; }
    public void setPorcentajeImpuesto(double porcentajeImpuesto) { this.porcentajeImpuesto = porcentajeImpuesto; }

    public String getRestricciones() { return restricciones; }
    public void setRestricciones(String restricciones) { this.restricciones = restricciones; }
}