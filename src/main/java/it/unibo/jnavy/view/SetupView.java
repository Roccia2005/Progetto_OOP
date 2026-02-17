package it.unibo.jnavy.view;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import it.unibo.jnavy.controller.SetupController;
import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.Position;

public class SetupView extends JPanel {
    private final SetupController controller;
    private final Runnable gameStartCall;

    private static final int GRID_SIZE = 10;
    private static final Color THEME_BACKGROUND = new Color(20, 20, 30);
    private static final Color THEME_TEXT = new Color(240, 240, 255);
    private static final Color COLOR_WATER = new Color(41, 86, 246);
    private static final Color COLOR_SHIP = Color.BLACK;
    private static final Color COLOR_BORDER = Color.GRAY;
    private static final int FONT_DEFAULT_SIZE = 22;
    private static final int FONT_ICON_SIZE = 120;
    private static final int BORDER_THICKNESS = 2;

    private final Map<Position, JButton> gridButtons = new HashMap<>();
    private CardinalDirection currentDirection = CardinalDirection.RIGHT;

    private JLabel infoLabel;
    private JButton rotateButton;
    private JButton nextShipButton;
    private JButton randomBotton;

    public SetupView(final SetupController controller, final Runnable gameStartCall) {
        this.controller = controller;
        this.gameStartCall = gameStartCall;
        this.initUI();
    }

    private void initUI() {
        this.setPreferredSize(new Dimension(1000, 700));
        this.setLayout(new BorderLayout());
        this.setBackground(THEME_BACKGROUND);

        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
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

        //pannello Laterale
        final JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setBackground(THEME_BACKGROUND);
        sidePanel.setPreferredSize(new Dimension(250, 0));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 20));

        //info Label
        String sizeText = controller.isSetupFinished() ? "Ready" : String.valueOf(controller.getNextShipSize());
        infoLabel = new JLabel("Size: " + sizeText);
        infoLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        infoLabel.setForeground(THEME_TEXT);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        sidePanel.add(infoLabel, BorderLayout.NORTH);

        //container bottoni
        final JPanel buttonsContainer = new JPanel(new GridLayout(3, 1, 0, 15));
        buttonsContainer.setBackground(THEME_BACKGROUND);

        //bottone 1 rotate
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

        // bottone 2 confirm/start game
        nextShipButton = createBigButton("Confirm", FONT_DEFAULT_SIZE);
        nextShipButton.addActionListener(e -> {
            try {
                controller.nextShip();
                updateView();
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, "Place a ship first!", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        // bottone 3 randomize
        randomBotton = createBigButton("Randomize", FONT_DEFAULT_SIZE);
        randomBotton.addActionListener(e -> {
            controller.randomizeHumanShips();
            updateView();
            if (this.controller.isSetupFinished()) {
                enableStartGameMode(); // Passa alla modalità "Avvio Partita"
            }
        });

        buttonsContainer.add(rotateButton);
        buttonsContainer.add(nextShipButton);
        buttonsContainer.add(randomBotton);

        sidePanel.add(buttonsContainer, BorderLayout.CENTER);
        this.add(sidePanel, BorderLayout.EAST);

        updateView();
    }

    /**
     * Metodo chiave: Gestisce la transizione visiva e logica quando il setup è finito.
     * Disabilita i controlli di editing e trasforma il tasto Confirm in Start Game.
     */
    private void enableStartGameMode() {
        infoLabel.setText("Ready!");

        // Disabilitiamo i bottoni non più utili
        rotateButton.setEnabled(false);
        randomBotton.setEnabled(false);

        // Trasformiamo il tasto Confirm nel tasto Start
        nextShipButton.setText("Start Game!");
        nextShipButton.setForeground(THEME_TEXT);

        // Rimuoviamo i vecchi listener
        for (var al : nextShipButton.getActionListeners()) {
            nextShipButton.removeActionListener(al);
        }

        // Aggiungiamo il nuovo listener per il cambio schermata
        nextShipButton.addActionListener(e -> startGame());
    }

    private void startGame() {
        if (gameStartCall != null) {
            gameStartCall.run();
        }
    }

    private JButton createBigButton(String text, int fontSize) {
        JButton b = new JButton(text);
        b.setBackground(THEME_BACKGROUND);
        b.setForeground(THEME_TEXT);
        b.setFont(new Font("SansSerif", Font.BOLD, fontSize));
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
            //decidere se tenere il suono o il messaggio di avviso
            //Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid placement!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateView() {
        var grid = controller.getHumanPlayer().getGrid();

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Position pos = new Position(i, j);
                JButton button = gridButtons.get(pos);
                button.setText("");

                var shipOpt = grid.getCell(pos).flatMap(Cell::getShip);

                if (shipOpt.isPresent()) {
                    var ship = shipOpt.get();
                    button.setBackground(COLOR_SHIP);

                    int top = isSameShip(grid, ship, new Position(i - 1, j)) ? 0 : BORDER_THICKNESS;
                    int left = isSameShip(grid, ship, new Position(i, j - 1)) ? 0 : BORDER_THICKNESS;
                    int bottom = isSameShip(grid, ship, new Position(i + 1, j)) ? 0 : BORDER_THICKNESS;
                    int right = isSameShip(grid, ship, new Position(i, j + 1)) ? 0 : BORDER_THICKNESS;

                    button.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, COLOR_BORDER));
                } else {
                    button.setBackground(COLOR_WATER);
                    button.setBorder(BorderFactory.createLineBorder(new Color(0, 80, 120), 1));
                }
            }
        }
        // 2. Aggiorna le etichette e lo stato dei bottoni
        if (controller.isSetupFinished()) {
            enableStartGameMode();
        } else {
            infoLabel.setText("Size: " + controller.getNextShipSize());
        }
    }

    private boolean isSameShip(Grid grid, Ship currentShip, Position neighborPos) {
        if (!grid.isPositionValid(neighborPos)) {
            return false;
        }
        var neighborShipOpt = grid.getCell(neighborPos).flatMap(Cell::getShip);
        return neighborShipOpt.map(ship -> ship.equals(currentShip)).orElse(false);
    }
}