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
 * A UI component that displays the column coordinates
 * above the game grids. It automatically generates labels based on the grid size.
 */
public final class ColumnLabelsPanel extends JPanel {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@code ColumnLabelsPanel} with the specified number of columns.
     *
     * @param size the total number of columns in the grid, which 
     * determines the range of numerical labels generated.
     */
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