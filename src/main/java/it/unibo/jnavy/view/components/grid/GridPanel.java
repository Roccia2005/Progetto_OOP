package it.unibo.jnavy.view.components.grid;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import it.unibo.jnavy.model.utilities.Position;

public class GridPanel extends JPanel {

    private final Map<Position, JButton> buttons = new HashMap<>();

    public GridPanel(String title, int size, boolean isClickable) {
        super(new BorderLayout(0, 10));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        this.add(label, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(size, size));

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                JButton cellButton = new JButton();
                cellButton.setMargin(new Insets(0, 0, 0, 0));
                cellButton.setFocusPainted(false);
                if (isClickable) {
                    //TODO add actionlistener for click
                }
                this.buttons.put(new Position(row, col), cellButton);
                grid.add(cellButton);
            }
        }
        
        this.add(grid, BorderLayout.CENTER);
    }
}