package it.unibo.jnavy.view.setup;

import it.unibo.jnavy.controller.setup.SetupController;
import it.unibo.jnavy.controller.utilities.CellState;
import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.view.game.ToastNotification;

import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;

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

public class SetupView extends JPanel {

    private static final int GRID_SIZE = 10;
    private static final Color THEME_BACKGROUND = new Color(20, 20, 30);
    private static final Color THEME_TEXT = new Color(240, 240, 255);
    private static final Color COLOR_WATER = new Color(41, 86, 246);
    private static final Color COLOR_ERROR = new Color(200, 50, 50);
    private static final Color COLOR_SHIP = Color.BLACK;
    private static final Color COLOR_BORDER = Color.GRAY;
    private static final Color COLOR_BORDER_WATER = new Color(0, 80, 120);
    private static final int FONT_DEFAULT_SIZE = 22;
    private static final int FONT_ICON_SIZE = 120;
    private static final int BORDER_THICKNESS = 2;
    private static final String NULL_SHIP_ERROR = "Place a ship first!";
    private static final String PLACEMENT_ERROR = "Invalid placement!";

    private final SetupController controller;
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

    public SetupView(final SetupController controller, final Runnable gameStartCall, final Runnable backCall) {
        this.controller = controller;
        this.gameStartCall = gameStartCall;
        this.backCall = backCall;
        this.initUI();
    }

    private void initUI() {
        this.setPreferredSize(new Dimension(1000, 700));
        this.setLayout(new BorderLayout());
        this.setBackground(THEME_BACKGROUND);

        final JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(THEME_BACKGROUND);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

        backButton = createBigButton("Back", 15);
        backButton.setForeground(THEME_TEXT);
        backButton.addActionListener(e -> {
            if (backCall != null) {
                backCall.run();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);

        final JLabel titleLabel = new JLabel("DEPLOY YOUR FLEET!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Impact", Font.PLAIN, 30));
        titleLabel.setForeground(THEME_TEXT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 100));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        this.add(topPanel, BorderLayout.NORTH);

        final JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        gridPanel.setBackground(THEME_BACKGROUND);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                final JButton button = new JButton();
                final Position pos = new Position(i, j);

                button.setBackground(COLOR_WATER);
                button.setOpaque(true);
                button.setBorderPainted(true);

                button.addActionListener(e -> placeShipAt(pos));

                gridButtons.put(pos, button);
                gridPanel.add(button);
            }
        }
        this.add(gridPanel, BorderLayout.CENTER);

        final JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setBackground(THEME_BACKGROUND);
        sidePanel.setPreferredSize(new Dimension(250, 0));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 20));

        final String sizeText = controller.isSetupFinished() ? "Ready" : String.valueOf(controller.getNextShipSize());
        infoLabel = new JLabel("Size: " + sizeText);
        infoLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        infoLabel.setForeground(THEME_TEXT);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        sidePanel.add(infoLabel, BorderLayout.NORTH);

        final JPanel buttonsContainer = new JPanel(new GridLayout(4, 1, 0, 15));
        buttonsContainer.setBackground(THEME_BACKGROUND);

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
                } catch (IllegalStateException ex) {
                    Toolkit.getDefaultToolkit().beep();
                    ToastNotification.show(this, NULL_SHIP_ERROR, COLOR_ERROR);
                }
            }
        });

        randomButton = createBigButton("Randomize", FONT_DEFAULT_SIZE);
        randomButton.addActionListener(e -> {
            controller.randomizeHumanShips();
            updateView();
        });

        clearButton = createBigButton("Clear Fleet", FONT_DEFAULT_SIZE);
        clearButton.setForeground(THEME_TEXT);
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
        b.setBackground(THEME_BACKGROUND);
        b.setForeground(THEME_TEXT);
        b.setFont(new Font(FONT_FAMILY, Font.BOLD, fontSize));
        b.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return b;
    }

    private void placeShipAt(final Position pos) {
        if (controller.isSetupFinished()) {
            return;
        }
        if (controller.setShip(pos, currentDirection)) {
            updateView();
        } else {
            Toolkit.getDefaultToolkit().beep();
            ToastNotification.show(this, PLACEMENT_ERROR, COLOR_ERROR);
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
                    button.setBackground(COLOR_WATER);
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
            nextShipButton.setForeground(THEME_TEXT);

            rotateButton.setEnabled(true);
            randomButton.setEnabled(true);
            clearButton.setEnabled(true);
        }
        this.repaint();
    }
}
