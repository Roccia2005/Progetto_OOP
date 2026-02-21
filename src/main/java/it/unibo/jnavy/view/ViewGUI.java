package it.unibo.jnavy.view;

import javax.swing.*;
import java.awt.*;

import it.unibo.jnavy.controller.game.GameController;
import it.unibo.jnavy.controller.selection.SelectionController;
import it.unibo.jnavy.controller.setup.SetupController;
import it.unibo.jnavy.view.game.GamePanel;
import it.unibo.jnavy.view.game.ToastNotification;
import it.unibo.jnavy.view.selection.BotSelectionPanel;
import it.unibo.jnavy.view.selection.CapSelectionPanel;
import it.unibo.jnavy.view.selection.BotSelectionPanel.BotLevel;
import it.unibo.jnavy.view.selection.CapSelectionPanel.CaptainAbility;
import it.unibo.jnavy.view.setup.SetupView;
import it.unibo.jnavy.view.start.StartView;

/**
 * Swing-based implementation of the {@link View} interface.
 * It manages a {@link CardLayout} to switch between different game panels
 * such as the menu, selection screens, and the game board.
 */
public class ViewGUI extends JFrame implements View {
    private static final String START_CARD = "START";
    private static final String BOT_CARD = "BOT_SELECTION";
    private static final String CAPTAIN_CARD = "CAPTAIN_SELECTION";
    private static final String SETUP_CARD = "SETUP";
    private static final String GAME_CARD = "GAME";
    private static final String LOAD_ERROR = "No valid save file found!";
    private static final int SIZE_HEIGHT = 700;
    private static final int SIZE_WIDTH = 1000;
    private static final Color ERROR_COLOR = Color.RED;

    /**
     * Serial version UID for serialization.
     */
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private StartView startView;
    private final SelectionController selectionController;

    /**
     * Constructs the main GUI window.
     * @param selectionController the controller responsible for menu and selection transitions
     */
    public ViewGUI(final SelectionController selectionController) {
        this.setTitle("J-Navy");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(SIZE_WIDTH, SIZE_HEIGHT);
        this.setLocationRelativeTo(null);
        this.setResizable(true);

        this.selectionController = selectionController;
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(this.cardLayout);
        this.add(this.mainPanel);

        initStartPhase();
        initSelectionPhase();

        this.cardLayout.show(this.mainPanel, START_CARD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        this.setVisible(true);
        this.startView.startMusic();
    }

    /**
     * Initializes the start screen phase.
     */
    private void initStartPhase() {
        this.startView = new StartView(
                () -> {
                    this.startView.stopMusic();
                    this.selectionController.newGame();
                },
                () -> this.selectionController.loadGame()
        );
        this.mainPanel.add(startView, START_CARD);
    }

    /**
     * Initializes the bot and captain selection phases.
     */
    private void initSelectionPhase() {
        final BotSelectionPanel botPanel = new BotSelectionPanel((BotLevel level) -> {
            this.selectionController.botSelection(level);
        }, () -> this.selectionController.showStartScreen());
        final CapSelectionPanel capPanel = new CapSelectionPanel((CaptainAbility ability) -> {
            this.selectionController.capSelection(ability);
        }, () -> this.selectionController.showBotSelection());

        this.mainPanel.add(botPanel, BOT_CARD);
        this.mainPanel.add(capPanel, CAPTAIN_CARD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showStartScreen() {
        this.cardLayout.show(this.mainPanel, START_CARD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showBotSelection() {
        this.cardLayout.show(this.mainPanel, BOT_CARD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showCaptainSelection() {
        this.cardLayout.show(this.mainPanel, CAPTAIN_CARD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showSetupPhase(final SetupController setupController) {
        final SetupView setupView = new SetupView(
                setupController,
                () -> this.selectionController.setupComplete(setupController),
                () -> this.selectionController.showCaptainSelection()
        );
        this.mainPanel.add(setupView, SETUP_CARD);
        this.cardLayout.show(this.mainPanel, SETUP_CARD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showGamePhase(final GameController gameController) {
        final GamePanel gamePanel = new GamePanel(gameController, () -> {
            this.selectionController.backToMenu();
        });
        this.mainPanel.add(gamePanel, GAME_CARD);
        this.cardLayout.show(this.mainPanel, GAME_CARD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showError(final String message) {
        Toolkit.getDefaultToolkit().beep();
        ToastNotification.show(this, LOAD_ERROR, ERROR_COLOR);
    }
}
