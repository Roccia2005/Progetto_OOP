package it.unibo.jnavy.view.components.grid;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import it.unibo.jnavy.controller.utilities.CellCondition;
import it.unibo.jnavy.model.utilities.Position;

public class GridPanel extends JPanel {

    private static final Color MENUBLUE = new Color(41, 86, 246);
    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30);
    //private final ImageIcon fogIcon;
    private final int size;
    private final Map<Position, JButton> buttons = new HashMap<>();

    public GridPanel(int size, String title, Consumer<Position> onClick) {
        super(new BorderLayout(0, 10));
        this.size = size;
        //this.fogIcon = new ImageIcon(getClass().getResource("/images/fog.png"));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        label.setBackground(BACKGROUND_COLOR);
        this.add(label, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(this.size, this.size, 2, 2));
        grid.setBackground(Color.BLACK);
        grid.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        for (int row = 0; row < this.size; row++) {
            for (int col = 0; col < this.size; col++) {
                JButton cellButton = new JButton();
                Position pos = new Position(row, col);

                if (onClick != null) cellButton.addActionListener(e->onClick.accept(pos));
                this.buttons.put(pos, cellButton);

                cellButton.setMargin(new Insets(0, 0, 0, 0));
                cellButton.setFocusPainted(false);
                cellButton.setBorderPainted(false);
                cellButton.setOpaque(true);

                grid.add(cellButton);
            }
        }
        this.add(grid, BorderLayout.CENTER);
    }

    public void refresh(Function<Position, CellCondition> stateProvider) {
        for (int row = 0; row < this.size; row++) {
            for (int col = 0; col < this.size; col++) {

                Position pos = new Position(row, col);
                JButton button = this.buttons.get(pos);

                CellCondition state = stateProvider.apply(pos);

                button.setText("");

                switch (state) {
                    case FOG:
                        //button.setIcon(fogIcon);
                        //button.setBackground(null);
                        button.setBackground(Color.LIGHT_GRAY);
                        button.setEnabled(true);
                        break;
                    case WATER:
                        button.setBackground(Color.LIGHT_GRAY);
                        button.setEnabled(true);
                        break;
                    case SHIP:
                        button.setBackground(Color.DARK_GRAY);
                        button.setEnabled(true);
                        break;
                    case HIT_WATER:
                        button.setBackground(MENUBLUE);
                        button.setEnabled(false);
                        break;
                    case HIT_SHIP:
                        button.setBackground(Color.ORANGE);
                        button.setEnabled(true);
                        break;
                    case SUNK_SHIP:
                        button.setBackground(Color.RED);
                        button.setEnabled(false);
                        break;
                    case REVEALED_WATER:
                        button.setBackground(Color.CYAN);
                        button.setEnabled(true);
                        break;
                    case REVEALED_SHIP:
                        button.setBackground(Color.YELLOW);
                        button.setEnabled(true);
                        break;
                }
            }
        }
        this.repaint();
    }
}