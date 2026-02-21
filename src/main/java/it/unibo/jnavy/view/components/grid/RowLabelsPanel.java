package it.unibo.jnavy.view.components.grid;

import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;
import static it.unibo.jnavy.view.utilities.ViewConstants.FOREGROUND_COLOR;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Component that displays the row coordinates (A, B, C...) for the grid.
 */
public class RowLabelsPanel extends JPanel {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    public RowLabelsPanel(final int size) {
        super(new GridLayout(size, 1, 2, 2)); 
        this.setOpaque(false);
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        for (int i = 0; i < size; i++) {
            final char letter = (char) ('A' + i);
            final JLabel label = new JLabel(String.valueOf(letter), SwingConstants.CENTER);
            label.setForeground(FOREGROUND_COLOR);
            label.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
            this.add(label);
        }
    }
}