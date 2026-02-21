package it.unibo.jnavy.view.setup;

import it.unibo.jnavy.controller.setup.SetupController;
import it.unibo.jnavy.controller.utilities.CellState;
import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.view.game.ToastNotification;

import static it.unibo.jnavy.view.utilities.ViewConstants.BACKGROUND_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.BORDER_THICKNESS;
import static it.unibo.jnavy.view.utilities.ViewConstants.ERROR_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;
import static it.unibo.jnavy.view.utilities.ViewConstants.MENUBLUE;
import static it.unibo.jnavy.view.utilities.ViewConstants.SETHEIGHT;
import static it.unibo.jnavy.view.utilities.ViewConstants.SETWIDTH;
import static it.unibo.jnavy.view.utilities.ViewConstants.TEXT_COLOR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Represents the graphical user interface for the fleet setup phase.
 * It allows the user to manually place, rotate, randomize, or clear their fleet before starting the game.
 */
public final class SetupView extends JPanel {

    private static final int GRID_SIZE = 10;

    private static final Color COLOR_SHIP = Color.BLACK;
    private static final Color COLOR_BORDER = Color.GRAY;
    private static final Color COLOR_BORDER_WATER = new Color(0, 80, 120);

    private static final int SIDE_PANEL_WIDTH = 250;
    private static final int PADDING_15 = 15;
    private static final int PADDING_20 = 20;
    private static final int TITLE_MARGIN_RIGHT = 100;
    private static final int TITLE_FONT_SIZE = 30;
    private static final int INFO_FONT_SIZE = 24;
    private static final int BACK_BTN_FONT_SIZE = 15;

    private static final int FONT_DEFAULT_SIZE = 22;
    private static final int FONT_ICON_SIZE = 120;
    private static final String NULL_SHIP_ERROR = "Place a ship first!";
    private static final String PLACEMENT_ERROR = "Invalid placement!";

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final transient SetupController controller;
    private final Runnable gameStartCall;
    private final Runnable backCall;
    private final Map<Position, JButton> gridButtons = new HashMap<>();
    private CardinalDirection currentDirection = CardinalDirection.RIGHT;

    private JLabel infoLabel;
    private JButton rotateButton;
    private JButton nextShipButton;
    private JButton randomButton;
    private JButton clearButton;
    private JButton backButton;

    /**
     * Constructs the SetupView for the fleet deployment phase.
     *
     * @param controller    the controller managing the setup logic
     * @param gameStartCall the callback to execute when the game starts
     * @param backCall      the callback to execute to go back to the previous screen
     */
    public SetupView(final SetupController controller, final Runnable gameStartCall, final Runnable backCall) {
        this.controller = controller;
        this.gameStartCall = gameStartCall;
        this.backCall = backCall;
        this.initUI();
    }

    private void initUI() {
        this.setPreferredSize(new Dimension(SETWIDTH, SETHEIGHT));
        this.setLayout(new BorderLayout());
        this.setBackground(BACKGROUND_COLOR);

        final JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(PADDING_15, PADDING_15, 0, PADDING_15));

        backButton = createBigButton("Back", BACK_BTN_FONT_SIZE);
        backButton.setForeground(TEXT_COLOR);
        backButton.addActionListener(e -> {
            if (backCall != null) {
                backCall.run();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);

        final JLabel titleLabel = new JLabel("DEPLOY YOUR FLEET!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Impact", Font.PLAIN, TITLE_FONT_SIZE));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, TITLE_MARGIN_RIGHT));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        this.add(topPanel, BorderLayout.NORTH);

        final JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        gridPanel.setBackground(BACKGROUND_COLOR);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(PADDING_20, PADDING_20, PADDING_20, PADDING_20));

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                final JButton button = new JButton();
                final Position pos = new Position(i, j);

                button.setBackground(MENUBLUE);
                button.setOpaque(true);
                button.setBorderPainted(true);

                button.addActionListener(e -> placeShipAt(pos));

                gridButtons.put(pos, button);
                gridPanel.add(button);
            }
        }
        this.add(gridPanel, BorderLayout.CENTER);

        final JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setBackground(BACKGROUND_COLOR);
        sidePanel.setPreferredSize(new Dimension(SIDE_PANEL_WIDTH, 0));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(PADDING_20, 0, PADDING_20, PADDING_20));

        final String sizeText = controller.isSetupFinished() ? "Ready" : String.valueOf(controller.getNextShipSize());
        infoLabel = new JLabel("Size: " + sizeText);
        infoLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, INFO_FONT_SIZE));
        infoLabel.setForeground(TEXT_COLOR);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, PADDING_20, 0));
        sidePanel.add(infoLabel, BorderLayout.NORTH);

        final JPanel buttonsContainer = new JPanel(new GridLayout(4, 1, 0, PADDING_15));
        buttonsContainer.setBackground(BACKGROUND_COLOR);

        rotateButton = createBigButton("→", FONT_ICON_SIZE);
        rotateButton.setToolTipText("Rotate Ship (Horizontal/Vertical)");
        rotateButton.addActionListener(e -> {
            if (currentDirection == CardinalDirection.RIGHT) {
                currentDirection = CardinalDirection.DOWN;
                rotateButton.setText("↓");
            } else {
                currentDirection = CardinalDirection.RIGHT;
                rotateButton.setText("→");
            }
            rotateButton.repaint();
        });

        nextShipButton = createBigButton("Confirm", FONT_DEFAULT_SIZE);
        nextShipButton.addActionListener(e -> {
            if (controller.isSetupFinished()) {
                startGame();
            } else {
                try {
                    controller.nextShip();
                    updateView();
                } catch (final IllegalStateException ex) {
                    Toolkit.getDefaultToolkit().beep();
                    ToastNotification.show(this, NULL_SHIP_ERROR, ERROR_COLOR);
                }
            }
        });

        randomButton = createBigButton("Randomize", FONT_DEFAULT_SIZE);
        randomButton.addActionListener(e -> {
            controller.randomizeHumanShips();
            updateView();
        });

        clearButton = createBigButton("Clear Fleet", FONT_DEFAULT_SIZE);
        clearButton.setForeground(TEXT_COLOR);
        clearButton.addActionListener(e -> {
            controller.clearFleet();
            updateView();
        });

        buttonsContainer.add(rotateButton);
        buttonsContainer.add(nextShipButton);
        buttonsContainer.add(randomButton);
        buttonsContainer.add(clearButton);

        sidePanel.add(buttonsContainer, BorderLayout.CENTER);
        this.add(sidePanel, BorderLayout.EAST);

        updateView();
    }

    private void startGame() {
        rotateButton.setEnabled(false);
        randomButton.setEnabled(false);
        clearButton.setEnabled(false);
        nextShipButton.setEnabled(false);
        backButton.setEnabled(false);

        if (gameStartCall != null) {
            gameStartCall.run();
        }
    }

    private JButton createBigButton(final String text, final int fontSize) {
        final JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(BACKGROUND_COLOR);
        b.setForeground(TEXT_COLOR);
        b.setFont(new Font(FONT_FAMILY, Font.BOLD, fontSize));
        b.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return b;
    }

    private void placeShipAt(final Position pos) {
        if (controller.isSetupFinished()) {
            return;
        }
        if (controller.tryPlaceShip(pos, currentDirection)) {
            updateView();
        } else {
            Toolkit.getDefaultToolkit().beep();
            ToastNotification.show(this, PLACEMENT_ERROR, ERROR_COLOR);
        }
    }

    private void updateView() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                final Position pos = new Position(i, j);
                final JButton button = gridButtons.get(pos);
                final CellState state = controller.getCellState(pos);

                if (state.hasShip()) {
                    button.setBackground(COLOR_SHIP);
                    final int top = state.connectedTop() ? 0 : BORDER_THICKNESS;
                    final int left = state.connectedLeft() ? 0 : BORDER_THICKNESS;
                    final int bottom = state.connectedBottom() ? 0 : BORDER_THICKNESS;
                    final int right = state.connectedRight() ? 0 : BORDER_THICKNESS;
                    button.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, COLOR_BORDER));
                } else {
                    button.setBackground(MENUBLUE);
                    button.setBorder(BorderFactory.createLineBorder(COLOR_BORDER_WATER, 1));
                }
            }
        }

        if (controller.isSetupFinished()) {
            infoLabel.setText("Ready!");

            nextShipButton.setText("Start Game!");

            rotateButton.setEnabled(false);
            randomButton.setEnabled(false);
            clearButton.setEnabled(true);
        } else {
            infoLabel.setText("Size: " + controller.getNextShipSize());

            nextShipButton.setText("Confirm");
            nextShipButton.setForeground(TEXT_COLOR);

            rotateButton.setEnabled(true);
            randomButton.setEnabled(true);
            clearButton.setEnabled(true);
        }
        this.repaint();
    }
}
