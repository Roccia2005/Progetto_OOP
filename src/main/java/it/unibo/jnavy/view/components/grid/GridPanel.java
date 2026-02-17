package it.unibo.jnavy.view.components.grid;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import it.unibo.jnavy.controller.GameController;
import it.unibo.jnavy.model.utilities.Position;

public class GridPanel extends JPanel {

    private final Map<Position, JButton> buttons = new HashMap<>();
    private final GameController controller;

    public GridPanel(GameController controller, String title, Consumer<Position> onClick) {
        super(new BorderLayout(0, 10));
        this.controller = controller;

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        this.add(label, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(this.controller.getGridSize(), this.controller.getGridSize()));

        for (int row = 0; row < this.controller.getGridSize(); row++) {
            for (int col = 0; col < this.controller.getGridSize(); col++) {
                JButton cellButton = new JButton();
                Position pos = new Position(row, col);
                cellButton.setMargin(new Insets(0, 0, 0, 0));
                cellButton.setFocusPainted(false);
                if (onClick != null) cellButton.addActionListener(e->onClick.accept(pos));
                this.buttons.put(pos, cellButton);
                grid.add(cellButton);
            }
        }
        
        this.add(grid, BorderLayout.CENTER);
    }
}