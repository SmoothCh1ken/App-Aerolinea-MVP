package modelo.modulo_consulta;

import java.sql.Timestamp;

/**
 * Objeto DTO (Data Transfer Object) para transportar los datos consolidados 
 * de la búsqueda de vuelos hacia la tabla de resultados en la Vista.
 */
public class VueloDTO {

    // Atributos privados en lowerCamelCase
    private int idVuelo;
    private String numeroVuelo;
    private String aeropuertoOrigen;   // Ej: "BOG - El Dorado"
    private String aeropuertoDestino;  // Ej: "MDE - José María Córdova"
    private Timestamp fechaHoraSalida;
    private Timestamp fechaHoraLlegada;
    private int escalas;
    private String detalleEscala;
    private double precioBase;
    private String nombreTarifa;       // Ej: "XS (Basic)" o "M (Classic)"

    // Constructor vacío
    public VueloDTO() {
    }

    // Constructor completo
    public VueloDTO(int idVuelo, String numeroVuelo, String aeropuertoOrigen, String aeropuertoDestino,
                    Timestamp fechaHoraSalida, Timestamp fechaHoraLlegada, int escalas,
                    String detalleEscala, double precioBase, String nombreTarifa) {
        this.idVuelo = idVuelo;
        this.numeroVuelo = numeroVuelo;
        this.aeropuertoOrigen = aeropuertoOrigen;
        this.aeropuertoDestino = aeropuertoDestino;
        this.fechaHoraSalida = fechaHoraSalida;
        this.fechaHoraLlegada = fechaHoraLlegada;
        this.escalas = escalas;
        this.detalleEscala = detalleEscala;
        this.precioBase = precioBase;
        this.nombreTarifa = nombreTarifa;
    }

    // --- GETTERS Y SETTERS ---

    public int getIdVuelo() {
        return idVuelo;
    }

    public void setIdVuelo(int idVuelo) {
        this.idVuelo = idVuelo;
    }

    public String getNumeroVuelo() {
        return numeroVuelo;
    }

    public void setNumeroVuelo(String numeroVuelo) {
        this.numeroVuelo = numeroVuelo;
    }

    public String getAeropuertoOrigen() {
        return aeropuertoOrigen;
    }

    public void setAeropuertoOrigen(String aeropuertoOrigen) {
        this.aeropuertoOrigen = aeropuertoOrigen;
    }

    public String getAeropuertoDestino() {
        return aeropuertoDestino;
    }

    public void setAeropuertoDestino(String aeropuertoDestino) {
        this.aeropuertoDestino = aeropuertoDestino;
    }

    public Timestamp getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(Timestamp fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public Timestamp getFechaHoraLlegada() {
        return fechaHoraLlegada;
    }

    public void setFechaHoraLlegada(Timestamp fechaHoraLlegada) {
        this.fechaHoraLlegada = fechaHoraLlegada;
    }

    public int getEscalas() {
        return escalas;
    }

    public void setEscalas(int escalas) {
        this.escalas = escalas;
    }

    public String getDetalleEscala() {
        return detalleEscala;
    }

    public void setDetalleEscala(String detalleEscala) {
        this.detalleEscala = detalleEscala;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    public String getNombreTarifa() {
        return nombreTarifa;
    }

    public void setNombreTarifa(String nombreTarifa) {
        this.nombreTarifa = nombreTarifa;
    }
}