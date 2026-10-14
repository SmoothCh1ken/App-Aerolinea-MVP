package modelo.modulo_cambios;

import java.util.ArrayList;

public class ReservaInfoDTO {
    public int idReserva;
    public String codigo, estado, titular, apellido, correo;
    public ArrayList<TramoInfoDTO> tramos = new ArrayList<>();

    /** Tramos de un sentido ("Ida" o "Regreso"). */
    public ArrayList<TramoInfoDTO> tramos(String sentido) {
        ArrayList<TramoInfoDTO> lista = new ArrayList<>();
        for (TramoInfoDTO t : tramos) {
            if (t.sentido.equals(sentido)) {
                lista.add(t);
            }
        }
        return lista;
    }

    /** Sentidos distintos que tiene la reserva (Ida, Regreso). */
    public ArrayList<String> sentidos() {
        ArrayList<String> lista = new ArrayList<>();
        for (TramoInfoDTO t : tramos) {
            if (!lista.contains(t.sentido)) {
                lista.add(t.sentido);
            }
        }
        return lista;
    }
}
