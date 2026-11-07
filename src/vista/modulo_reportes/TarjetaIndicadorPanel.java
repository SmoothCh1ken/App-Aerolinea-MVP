package vista.modulo_reportes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/** Tarjeta del tablero: un título pequeño y el valor del indicador en grande. */
public class TarjetaIndicadorPanel extends JPanel {

    private final JLabel lblValor = new JLabel("-", SwingConstants.CENTER);

    public TarjetaIndicadorPanel(String titulo) {
        super(new BorderLayout(0, 6));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(12, 10, 12, 10)));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitulo.setForeground(Color.DARK_GRAY);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValor.setForeground(new Color(25, 115, 210));

        add(lblTitulo, BorderLayout.NORTH);
        add(lblValor, BorderLayout.CENTER);
    }

    public void setValor(String valor) {
        lblValor.setText(valor);
    }
}
