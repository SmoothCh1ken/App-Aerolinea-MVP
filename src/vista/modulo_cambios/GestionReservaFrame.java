package vista.modulo_cambios;

import controlador.modulo_cambios.CambiosController;
import modelo.modulo_cambios.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/** Pantalla del módulo 4: gestión de reservas (pestaña 1) y operaciones de vuelos (pestaña 2). */
public class GestionReservaFrame extends JFrame {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** CLIENTE: solo "Mi reserva". OPERACIONES: solo la gestión de vuelos (personal de la aerolínea). */
    public enum Modo { CLIENTE, OPERACIONES }

    private final CambiosController controlador = new CambiosController();
    private final Runnable alSalir;   // acción al cerrar (volver al menú); puede ser null
    private ReservaInfoDTO reserva;   // la reserva que se está mostrando

    // Pestaña 1
    private JTextField txtCodigo = new JTextField(9);
    private JTextField txtApellido = new JTextField(12);
    private JTextArea txtDetalle = new JTextArea();

    // Pestaña 2
    private DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Vuelo", "Ruta", "Salida", "Llegada", "Estado"}, 0);
    private JTable tabla = new JTable(modeloTabla);

    public GestionReservaFrame(Modo modo, Runnable alSalir) {
        this.alSalir = alSalir;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(860, 540);
        setLocationRelativeTo(null);

        if (modo == Modo.CLIENTE) {
            setTitle("Mi reserva - Cambios, cancelaciones y reembolsos");
            add(crearPanelReserva(), BorderLayout.CENTER);
        } else {
            setTitle("Operaciones de vuelos - Aerolínea");
            add(crearPanelOperaciones(), BorderLayout.CENTER);
            cargarVuelos();
        }

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnMenu = new JButton("Volver al menú");
        btnMenu.addActionListener(e -> salir());
        barra.add(btnMenu);
        add(barra, BorderLayout.SOUTH);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { salir(); }
        });
    }

    /** Cierra la ventana y regresa al menú principal. */
    private void salir() {
        dispose();
        if (alSalir != null) {
            alSalir.run();
        }
    }

    /** Rellena código y apellido (por ejemplo, al venir desde un pago) y consulta de una vez. */
    public void precargar(String codigo, String apellido) {
        if (codigo != null) {
            txtCodigo.setText(codigo);
        }
        if (apellido != null) {
            txtApellido.setText(apellido);
        }
        if (codigo != null && apellido != null) {
            consultar();
        }
    }

    // =====================================================================
    // PESTAÑA 1: MI RESERVA
    // =====================================================================

    private JPanel crearPanelReserva() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Parte de arriba: código, apellido y botón consultar
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnConsultar = new JButton("Consultar");
        arriba.add(new JLabel("Código de reserva:"));
        arriba.add(txtCodigo);
        arriba.add(new JLabel("Apellido del titular:"));
        arriba.add(txtApellido);
        arriba.add(btnConsultar);

        // Centro: detalle de la reserva
        txtDetalle.setEditable(false);
        txtDetalle.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        // Parte de abajo: botones de acciones
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnHistorial = new JButton("Historial y notificaciones");
        JButton btnModificar = new JButton("Modificar vuelo");
        JButton btnCancelar = new JButton("Cancelar y solicitar reembolso");
        abajo.add(btnHistorial);
        abajo.add(btnModificar);
        abajo.add(btnCancelar);

        panel.add(arriba, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtDetalle), BorderLayout.CENTER);
        panel.add(abajo, BorderLayout.SOUTH);

        btnConsultar.addActionListener(e -> consultar());
        btnHistorial.addActionListener(e -> verHistorial());
        btnModificar.addActionListener(e -> modificarVuelo());
        btnCancelar.addActionListener(e -> cancelarReserva());
        return panel;
    }

    private void consultar() {
        try {
            reserva = controlador.consultar(txtCodigo.getText(), txtApellido.getText());
            mostrarReserva();
        } catch (Exception ex) {
            reserva = null;
            txtDetalle.setText("");
            mostrarError(ex);
        }
    }

    /** Vuelve a leer la reserva de la base de datos para ver los cambios hechos. */
    private void refrescarReserva() throws Exception {
        reserva = controlador.consultar(reserva.codigo, reserva.apellido);
        mostrarReserva();
    }

    private void mostrarReserva() {
        StringBuilder sb = new StringBuilder();
        sb.append("Reserva: ").append(reserva.codigo).append("  [").append(reserva.estado).append("]\n");
        sb.append("Titular: ").append(reserva.titular).append("  (").append(reserva.correo).append(")\n");

        for (String sentido : reserva.sentidos()) {
            ArrayList<TramoInfoDTO> tramos = reserva.tramos(sentido);
            TramoInfoDTO primero = tramos.get(0);

            sb.append("\n").append(sentido).append(": ").append(primero.numeroVuelo).append("  ")
              .append(primero.origen).append("->").append(primero.destino).append("  ")
              .append(primero.salida.format(FORMATO)).append(" / ").append(primero.llegada.format(FORMATO))
              .append("  [").append(primero.estadoVuelo).append("]\n");

            sb.append("   Tarifa ").append(primero.tarifa);
            if (primero.permiteCambios) {
                sb.append(" | cambios: cargo $").append(primero.cargoCambio);
            } else {
                sb.append(" | NO permite cambios");
            }
            if (primero.permiteReembolso) {
                sb.append(" | reembolso: penalidad ").append(primero.penalidad).append("%");
            } else {
                sb.append(" | NO permite reembolso");
            }
            sb.append("\n");

            for (TramoInfoDTO t : tramos) {
                sb.append("   - ").append(t.pasajero).append(" (").append(t.categoria).append(")  $")
                  .append(t.total()).append("\n");
            }
        }
        txtDetalle.setText(sb.toString());
        txtDetalle.setCaretPosition(0);
    }

    private void modificarVuelo() {
        try {
            exigirReserva();

            // 1. Elegir el tramo (si hay ida y regreso se pregunta cuál)
            ArrayList<String> sentidos = reserva.sentidos();
            String sentido = sentidos.get(0);
            if (sentidos.size() > 1) {
                Object elegido = JOptionPane.showInputDialog(this, "¿Qué tramo desea modificar?", "Modificar vuelo",
                        JOptionPane.QUESTION_MESSAGE, null, sentidos.toArray(), sentidos.get(0));
                if (elegido == null) {
                    return;
                }
                sentido = (String) elegido;
            }

            // 2. Buscar los vuelos alternos
            ArrayList<OpcionCambioDTO> opciones = controlador.opciones(reserva, sentido);
            if (opciones.isEmpty()) {
                mostrarInfo("No hay vuelos alternos disponibles para este tramo.");
                return;
            }

            // 3. Armar el texto de cada opción con su precio
            String[] textos = new String[opciones.size()];
            CotizacionDTO[] cotizaciones = new CotizacionDTO[opciones.size()];
            for (int i = 0; i < opciones.size(); i++) {
                OpcionCambioDTO o = opciones.get(i);
                cotizaciones[i] = controlador.cotizarCambio(reserva, sentido, o);
                CotizacionDTO q = cotizaciones[i];

                String valor = q.neto.signum() > 0 ? "a pagar $" + q.neto : "saldo a favor $" + q.neto.negate();
                textos[i] = o.numeroVuelo + "  " + o.salida.format(FORMATO) + "  ->  " + valor
                          + "  (cargo $" + q.cargo + ")";
            }

            // 4. El usuario escoge una opción
            Object seleccion = JOptionPane.showInputDialog(this,
                    "Elija el nuevo vuelo para el tramo de " + sentido.toLowerCase() + ":", "Modificar vuelo",
                    JOptionPane.QUESTION_MESSAGE, null, textos, textos[0]);
            if (seleccion == null) {
                return;
            }
            int posicion = 0;
            for (int i = 0; i < textos.length; i++) {
                if (textos[i].equals(seleccion)) {
                    posicion = i;
                }
            }

            // 5. Aplicar el cambio
            int idCambio = controlador.cambiar(reserva, sentido, opciones.get(posicion));
            refrescarReserva();

            CotizacionDTO elegida = cotizaciones[posicion];
            if (elegida.neto.signum() > 0) {
                mostrarInfo("Cambio #" + idCambio + " registrado. Queda pendiente el pago de $" + elegida.neto + ".");
            } else if (elegida.neto.signum() < 0) {
                mostrarInfo("Cambio #" + idCambio + " aplicado. Saldo a favor: $" + elegida.neto.negate()
                        + " (reembolso solicitado).");
            } else {
                mostrarInfo("Cambio #" + idCambio + " aplicado.");
            }
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void cancelarReserva() {
        try {
            exigirReserva();
            CotizacionDTO q = controlador.cotizarReembolso(reserva);

            int respuesta = JOptionPane.showConfirmDialog(this,
                    "Se cancelará la reserva " + reserva.codigo + " (" + q.motivo + ").\n"
                  + "Valor de los tiquetes: $" + q.base + "\n"
                  + "Penalidad: $" + q.penalidad + "\n"
                  + "Reembolso estimado: $" + q.reembolso + "\n"
                  + "¿Confirmar?", "Cancelar reserva", JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }

            controlador.cancelar(reserva);
            refrescarReserva();
            mostrarInfo("Cancelación registrada. La solicitud de reembolso quedó en estado 'Solicitado'.");
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void verHistorial() {
        try {
            exigirReserva();
            ArrayList<String> lista = controlador.historial(reserva.idReserva);
            if (lista.isEmpty()) {
                mostrarInfo("La reserva no tiene cambios, reembolsos ni notificaciones.");
                return;
            }
            JTextArea area = new JTextArea(String.join("\n\n", lista), 12, 70);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Historial y notificaciones",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    // =====================================================================
    // PESTAÑA 2: OPERACIONES DE VUELOS
    // =====================================================================

    private JPanel crearPanelOperaciones() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnReprogramar = new JButton("Reprogramar vuelo");
        JButton btnCancelarVuelo = new JButton("Cancelar vuelo");
        abajo.add(btnActualizar);
        abajo.add(btnReprogramar);
        abajo.add(btnCancelarVuelo);

        tabla.setDefaultEditor(Object.class, null);   // la tabla no se puede editar
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(abajo, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> cargarVuelos());
        btnReprogramar.addActionListener(e -> reprogramarVuelo());
        btnCancelarVuelo.addActionListener(e -> cancelarVuelo());
        return panel;
    }

    private void cargarVuelos() {
        try {
            modeloTabla.setRowCount(0);
            for (VueloInfoDTO v : controlador.vuelos()) {
                modeloTabla.addRow(new Object[]{v.id, v.numero, v.ruta,
                        v.salida.format(FORMATO), v.llegada.format(FORMATO), v.estado});
            }
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    /** Devuelve el vuelo seleccionado en la tabla (o null si no hay ninguno). */
    private VueloInfoDTO vueloSeleccionado() throws Exception {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarInfo("Seleccione un vuelo de la tabla.");
            return null;
        }
        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        return controlador.vuelo(id);
    }

    private void reprogramarVuelo() {
        try {
            VueloInfoDTO v = vueloSeleccionado();
            if (v == null) {
                return;
            }
            String texto = JOptionPane.showInputDialog(this,
                    "Horas a mover el vuelo " + v.numero + " (negativo = adelantar):", "2");
            if (texto == null) {
                return;
            }
            int horas = Integer.parseInt(texto.trim());
            int notificados = controlador.reprogramarVuelo(v, horas);
            cargarVuelos();
            mostrarInfo("Vuelo reprogramado. Pasajeros notificados: " + notificados);
        } catch (NumberFormatException ex) {
            mostrarInfo("Ingrese un número entero de horas.");
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void cancelarVuelo() {
        try {
            VueloInfoDTO v = vueloSeleccionado();
            if (v == null) {
                return;
            }
            int respuesta = JOptionPane.showConfirmDialog(this, "¿Cancelar el vuelo " + v.numero + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
            int notificados = controlador.cancelarVuelo(v);
            cargarVuelos();
            mostrarInfo("Vuelo cancelado. Pasajeros notificados: " + notificados);
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    // UTILIDADES
   

    private void exigirReserva() throws Exception {
        if (reserva == null) {
            throw new Exception("Primero consulte una reserva con su código y apellido.");
        }
    }

    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    private void mostrarError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
