package it.unibo.jnavy.view.components.grid;

import javax.swing.*;
import java.awt.*;

import static it.unibo.jnavy.view.utilities.ViewConstants.*;

/**
 * Component that displays the row coordinates (A, B, C...) for the grid.
 */
public class RowLabelsPanel extends JPanel {

    public RowLabelsPanel(int size) {
        super(new GridLayout(size, 1, 2, 2)); 
        this.setOpaque(false);
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        for (int i = 0; i < size; i++) {
            char letter = (char) ('A' + i);
            JLabel label = new JLabel(String.valueOf(letter), SwingConstants.CENTER);
            label.setForeground(FOREGROUND_COLOR);
            label.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
            this.add(label);
        }
    }
}