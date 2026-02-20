package it.unibo.jnavy.view.components.grid;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import it.unibo.jnavy.controller.utilities.CellCondition;
import it.unibo.jnavy.model.utilities.Position;

import static it.unibo.jnavy.view.utilities.ViewConstants.*;

public class GridPanel extends JPanel {

    private final int size;
    private final Map<Position, JButton> buttons = new HashMap<>();

    public GridPanel(int size, String title, Consumer<Position> onClick) {
        super(new BorderLayout(0, 10));
        this.size = size;

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font(FONT_FAMILY, Font.BOLD, 16));
        label.setForeground(FOREGROUND_COLOR);
        label.setBackground(BACKGROUND_COLOR);
        this.add(label, BorderLayout.NORTH);

        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setOpaque(false);

        RowLabelsPanel rowLabels = new RowLabelsPanel(this.size);
        rowLabels.setPreferredSize(new Dimension(30, 0));
        boardContainer.add(rowLabels, BorderLayout.WEST);

        boardContainer.add(createGridButtons(onClick), BorderLayout.CENTER);

        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setOpaque(false);
        
        JLabel cornerSpacer = new JLabel(""); 
        cornerSpacer.setPreferredSize(new Dimension(30, 0));
        bottomWrapper.add(cornerSpacer, BorderLayout.WEST);
        bottomWrapper.add(new ColumnLabelsPanel(this.size), BorderLayout.CENTER);

        boardContainer.add(bottomWrapper, BorderLayout.SOUTH);

        this.add(boardContainer, BorderLayout.CENTER);
    }

    public void refresh(Function<Position, CellCondition> positionToCondition) {
        buttons.forEach((pos, button) -> {
            CellCondition state = positionToCondition.apply(pos);
            updateButtonAppearance(button, state);
        });
        this.repaint();
    }

    private void updateButtonAppearance(JButton button, CellCondition state) {
        button.setText("");
        switch (state) {
            case FOG -> configureButton(button, Color.LIGHT_GRAY, true);
            case WATER -> configureButton(button, Color.LIGHT_GRAY, false);
            case SHIP -> configureButton(button, Color.DARK_GRAY, false);
            case HIT_WATER -> configureButton(button, MENUBLUE, false);
            case HIT_SHIP -> configureButton(button, Color.ORANGE, true);
            case SUNK_SHIP -> configureButton(button, Color.RED, false);
            case REVEALED_WATER -> configureButton(button, Color.CYAN, true);
            case REVEALED_SHIP -> configureButton(button, Color.YELLOW, true);
        }
    }

    private void configureButton(JButton button, Color color, boolean enabled) {
        button.setBackground(color);
        button.setEnabled(enabled);
    }

    private JPanel createGridButtons(Consumer<Position> onClick) {
        JPanel grid = new JPanel(new GridLayout(this.size, this.size, 2, 2));
        grid.setBackground(BACKGROUND_COLOR);
        grid.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        for (int row = 0; row < this.size; row++) {
            for (int col = 0; col < this.size; col++) {
                JButton cellButton = new JButton();
                Position pos = new Position(row, col);

                if (onClick != null) {
                    cellButton.addActionListener(e -> onClick.accept(pos));
                }
                
                cellButton.setMargin(new Insets(0, 0, 0, 0));
                cellButton.setFocusPainted(false);
                cellButton.setBorderPainted(false);
                cellButton.setOpaque(true);

                this.buttons.put(pos, cellButton);
                grid.add(cellButton);
            }
        }
        return grid;
    }
}