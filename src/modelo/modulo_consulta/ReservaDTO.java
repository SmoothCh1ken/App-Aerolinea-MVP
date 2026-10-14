package modelo.modulo_consulta;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) principal que agrupa la selección de vuelos,
 * la lista de pasajeros y los datos del comprador para ser procesados.
 */
public class ReservaDTO {

    private String codigoReserva; // Generado al confirmar (RF-08)
    private VueloDTO vueloIda;
    private VueloDTO vueloRegreso; // Opcional (null si es Solo Ida)
    private List<PasajeroDTO> listaPasajeros;
    private CompradorDTO comprador;
    private double totalPagar;

    public ReservaDTO() {
        this.listaPasajeros = new ArrayList<>();
    }

    // --- GETTERS Y SETTERS ---

    public String getCodigoReserva() { return codigoReserva; }
    public void setCodigoReserva(String codigoReserva) { this.codigoReserva = codigoReserva; }

    public VueloDTO getVueloIda() { return vueloIda; }
    public void setVueloIda(VueloDTO vueloIda) { this.vueloIda = vueloIda; }

    public VueloDTO getVueloRegreso() { return vueloRegreso; }
    public void setVueloRegreso(VueloDTO vueloRegreso) { this.vueloRegreso = vueloRegreso; }

    public List<PasajeroDTO> getListaPasajeros() { return listaPasajeros; }
    public void setListaPasajeros(List<PasajeroDTO> listaPasajeros) { this.listaPasajeros = listaPasajeros; }

    public CompradorDTO getComprador() { return comprador; }
    public void setComprador(CompradorDTO comprador) { this.comprador = comprador; }

    public double getTotalPagar() { return totalPagar; }
    public void setTotalPagar(double totalPagar) { this.totalPagar = totalPagar; }
}