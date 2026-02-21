package it.unibo.jnavy.view;

import javax.swing.*;
import java.awt.*;

import it.unibo.jnavy.controller.game.GameController;
import it.unibo.jnavy.controller.game.GameControllerImpl;
import it.unibo.jnavy.controller.setup.SetupController;
import it.unibo.jnavy.controller.setup.SetupControllerImpl;
import it.unibo.jnavy.model.bots.BeginnerBot;
import it.unibo.jnavy.model.bots.BotStrategy;
import it.unibo.jnavy.model.bots.ProBot;
import it.unibo.jnavy.model.bots.SniperBot;
import it.unibo.jnavy.model.captains.Captain;
import it.unibo.jnavy.model.captains.Engineer;
import it.unibo.jnavy.model.captains.Gunner;
import it.unibo.jnavy.model.captains.SonarOfficer;
import it.unibo.jnavy.model.player.Bot;
import it.unibo.jnavy.model.player.Human;
import it.unibo.jnavy.view.game.GamePanel;
import it.unibo.jnavy.view.game.ToastNotification;
import it.unibo.jnavy.view.selection.BotSelectionPanel;
import it.unibo.jnavy.view.selection.CapSelectionPanel;
import it.unibo.jnavy.view.selection.BotSelectionPanel.BotLevel;
import it.unibo.jnavy.view.selection.CapSelectionPanel.CaptainAbility;
import it.unibo.jnavy.view.setup.SetupView;
import it.unibo.jnavy.view.start.StartView;
import it.unibo.jnavy.model.serialization.SaveManager;
import it.unibo.jnavy.model.serialization.SaveManagerImpl;

public class ViewGUI extends JFrame implements View {
    private static final String START_CARD = "START";
    private static final String BOT_CARD = "BOT_SELECTION";
    private static final String CAPTAIN_CARD = "CAPTAIN_SELECTION";
    private static final String SETUP_CARD = "SETUP";
    private static final String GAME_CARD = "GAME";
    private static final String ERROR_MESSAGE = "No valid save file found!";
    private static final Color ERROR_COLOR = Color.RED;
    private static final int SIZE_HEIGHT = 700;
    private static final int SIZE_WIDTH = 1000;

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private StartView startView;
    private BotStrategy selectedBotStrategy;
    private Captain selectedCaptain;
    private boolean isSniperSelected;

    public ViewGUI() {
        this.setTitle("J-Navy");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(SIZE_HEIGHT, SIZE_WIDTH);
        this.setLocationRelativeTo(null);
        this.setResizable(true);

        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(this.cardLayout);
        this.add(this.mainPanel);

        initStartPhase();
        initSelectionPhase();

        this.cardLayout.show(this.mainPanel, START_CARD);
    }

    @Override
    public void start() {
        this.setVisible(true);
        this.startView.startMusic();
    }

    private void initStartPhase() {
        this.startView = new StartView(
                () -> {
                    final SaveManager saveManager = new SaveManagerImpl();
                    saveManager.deleteSave();
                    this.startView.stopMusic();
                    this.cardLayout.show(this.mainPanel, BOT_CARD);
                },
                this::loadGame
        );
        this.mainPanel.add(startView, START_CARD);
    }

    private void initSelectionPhase() {
        final BotSelectionPanel botPanel = new BotSelectionPanel((BotLevel level) -> {
            switch (level) {
                case BEGINNER -> this.selectedBotStrategy = new BeginnerBot();
                case PRO -> this.selectedBotStrategy = new ProBot();
                case SNIPER -> {
                    this.selectedBotStrategy = new ProBot();
                    this.isSniperSelected = true;
                }
            }
            this.cardLayout.show(this.mainPanel, CAPTAIN_CARD);
        }, () -> this.cardLayout.show(this.mainPanel, START_CARD));

        final CapSelectionPanel capPanel = new CapSelectionPanel((CaptainAbility ability) -> {
            switch (ability) {
                case ENGINEER -> this.selectedCaptain = new Engineer();
                case GUNNER -> this.selectedCaptain = new Gunner();
                case SONAROFFICER -> this.selectedCaptain = new SonarOfficer();
            }
            startSetupPhase();
        }, () -> this.cardLayout.show(this.mainPanel, BOT_CARD));

        this.mainPanel.add(botPanel, BOT_CARD);
        this.mainPanel.add(capPanel, CAPTAIN_CARD);
    }

    private void startSetupPhase() {
        final SetupController setupController = new SetupControllerImpl(this.selectedCaptain, this.selectedBotStrategy);
        final SetupView setupView = new SetupView(
                setupController,
                () -> startGamePhase(setupController),
                () -> {
                    this.selectedCaptain = null;
                    this.cardLayout.show(this.mainPanel, CAPTAIN_CARD);
                }
        );

        this.mainPanel.add(setupView, SETUP_CARD);
        this.cardLayout.show(this.mainPanel, SETUP_CARD);
    }

    private void startGamePhase(final SetupController completedSetup) {
        final var humanPlayer = (Human) completedSetup.getHumanPlayer();
        final var botPlayer = (Bot) completedSetup.getBotPlayer();

        if (isSniperSelected) {
            botPlayer.setStrategy(new SniperBot(humanPlayer.getGrid().getOccupiedPositions()));
        }

        final GameController gameController = new GameControllerImpl(humanPlayer, botPlayer);
        launchGameWithController(gameController);
    }

    private void loadGame() {
        final SaveManager saveManager = new SaveManagerImpl();

        saveManager.load().ifPresentOrElse(
                loadedState -> {
                    final GameController loadedController = new GameControllerImpl(loadedState);
                    launchGameWithController(loadedController);
                },
                () -> {
                    Toolkit.getDefaultToolkit().beep();
                    ToastNotification.show(this, ERROR_MESSAGE, ERROR_COLOR);
                    initStartPhase();
                }
        );
    }

    private void launchGameWithController(final GameController gameController) {
        final GamePanel gamePanel = new GamePanel(gameController, () -> {
            this.selectedBotStrategy = null;
            this.selectedCaptain = null;
            this.isSniperSelected = false;
            this.cardLayout.show(this.mainPanel, START_CARD);
        });

        this.mainPanel.add(gamePanel, GAME_CARD);
        this.cardLayout.show(this.mainPanel, GAME_CARD);
    }
}
