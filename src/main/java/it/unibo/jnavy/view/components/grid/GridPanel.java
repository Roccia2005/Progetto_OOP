package it.unibo.jnavy.view.components.grid;

import static it.unibo.jnavy.view.utilities.ViewConstants.BACKGROUND_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;
import static it.unibo.jnavy.view.utilities.ViewConstants.FOREGROUND_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.MENUBLUE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import it.unibo.jnavy.controller.utilities.CellCondition;
import it.unibo.jnavy.model.utilities.Position;

public class GridPanel extends JPanel {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final int size;
    private final Map<Position, JButton> buttons = new HashMap<>();

    public GridPanel(final int size, final String title, final Consumer<Position> onClick) {
        super(new BorderLayout(0, 10));
        this.size = size;

        final JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font(FONT_FAMILY, Font.BOLD, 16));
        label.setForeground(FOREGROUND_COLOR);
        label.setBackground(BACKGROUND_COLOR);
        this.add(label, BorderLayout.NORTH);

        final JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setOpaque(false);

        final RowLabelsPanel rowLabels = new RowLabelsPanel(this.size);
        rowLabels.setPreferredSize(new Dimension(30, 0));
        boardContainer.add(rowLabels, BorderLayout.WEST);

        boardContainer.add(createGridButtons(onClick), BorderLayout.CENTER);

        final JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setOpaque(false);
        
        final JLabel cornerSpacer = new JLabel(""); 
        cornerSpacer.setPreferredSize(new Dimension(30, 0));
        bottomWrapper.add(cornerSpacer, BorderLayout.WEST);
        bottomWrapper.add(new ColumnLabelsPanel(this.size), BorderLayout.CENTER);

        boardContainer.add(bottomWrapper, BorderLayout.SOUTH);

        this.add(boardContainer, BorderLayout.CENTER);
    }

    public void refresh(final Function<Position, CellCondition> positionToCondition) {
        buttons.forEach((pos, button) -> {
            final CellCondition state = positionToCondition.apply(pos);
            updateButtonAppearance(button, state);
        });
        this.repaint();
    }

    private void updateButtonAppearance(final JButton button, final CellCondition state) {
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

    private void configureButton(final JButton button, final Color color, final boolean enabled) {
        button.setBackground(color);
        button.setEnabled(enabled);
    }

    private JPanel createGridButtons(final Consumer<Position> onClick) {
        final JPanel grid = new JPanel(new GridLayout(this.size, this.size, 2, 2));
        grid.setBackground(BACKGROUND_COLOR);
        grid.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        for (int row = 0; row < this.size; row++) {
            for (int col = 0; col < this.size; col++) {
                final JButton cellButton = new JButton();
                final Position pos = new Position(row, col);

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

    /**
     * Returns the {@link JButton} associated with the given position.
     * Useful for the EffectsPanel to know where to draw the animations.
     */
    public JButton getButtonAt(final Position pos) {
        return this.buttons.get(pos);
    }

    /**
     * Refreshes the appearance of a single cell.
     *
     * @param pos The cell's position.
     * @param state The cell's new condition.
     */
    public void refreshCell(final Position pos, final CellCondition state) {
        final JButton button = this.buttons.get(pos);
        if (button != null) {
            updateButtonAppearance(button, state);
            button.repaint();
        }
    }
}