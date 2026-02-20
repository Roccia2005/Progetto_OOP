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
 * Component that displays the column coordinates (1, 2, 3...) for the grid.
 */
public class ColumnLabelsPanel extends JPanel {

    public ColumnLabelsPanel(final int size) {
        super(new GridLayout(1, size, 2, 2));
        this.setOpaque(false);
        this.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        for (int i = 1; i <= size; i++) {
            final JLabel label = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            label.setForeground(FOREGROUND_COLOR);
            label.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
            this.add(label);
        }
    }
}