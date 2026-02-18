package it.unibo.jnavy.view;

import javax.swing.*;
import java.awt.*;

import it.unibo.jnavy.controller.GameController;
import it.unibo.jnavy.controller.GameControllerImpl;
import it.unibo.jnavy.controller.SetupController;
import it.unibo.jnavy.controller.SetupControllerImpl;
import it.unibo.jnavy.model.Bot;
import it.unibo.jnavy.model.Human;
import it.unibo.jnavy.model.bots.*;
import it.unibo.jnavy.model.captains.*;
import it.unibo.jnavy.view.BotSelectionPanel.BotLevel;
import it.unibo.jnavy.view.CapSelectionPanel.CaptainAbility;

public class ViewGUI extends JFrame implements View {

    private static final String BOT_CARD = "BOT_SELECTION";
    private static final String CAPTAIN_CARD = "CAPTAIN_SELECTION";
    private static final String SETUP_CARD = "SETUP";
    private static final String GAME_CARD = "GAME";
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private BotStrategy selectedBotStrategy;
    private Captain selectedCaptain;
    private boolean isSniperSelected = false;

    public ViewGUI() {
        this.setTitle("J-Navy");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 700);
        this.setLocationRelativeTo(null);
        this.setResizable(true);

        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(this.cardLayout);
        this.add(this.mainPanel);

        initSelectionPhase();
        this.cardLayout.show(this.mainPanel, BOT_CARD);
    }

    @Override
    public void start() {
        this.setVisible(true);
    }

    private void initSelectionPhase() {
        BotSelectionPanel botPanel = new BotSelectionPanel((BotLevel level) -> {
            switch(level) {
                case BEGINNER -> this.selectedBotStrategy = new BeginnerBot();
                case PRO -> this.selectedBotStrategy = new ProBot();
                case SNIPER -> {
                    //placeholder con flag
                    this.selectedBotStrategy = new ProBot();
                    this.isSniperSelected = true;
                }
            }
            this.cardLayout.show(this.mainPanel, CAPTAIN_CARD);
        });

        CapSelectionPanel capPanel = new CapSelectionPanel((CaptainAbility ability) -> {
            switch(ability) {
                case ENGINEER -> this.selectedCaptain = new Engineer();
                case GUNNER -> this.selectedCaptain = new Gunner();
                case SONAROFFICER -> this.selectedCaptain = new SonarOfficer();
            }
            startSetupPhase();
        });

        this.mainPanel.add(botPanel, BOT_CARD);
        this.mainPanel.add(capPanel, CAPTAIN_CARD);
    }

    private void startSetupPhase() {
        SetupController setupController = new SetupControllerImpl(this.selectedCaptain, this.selectedBotStrategy);
        SetupView setupView = new SetupView(setupController, () -> {
            startGamePhase(setupController);
        });

        this.mainPanel.add(setupView, SETUP_CARD);
        this.cardLayout.show(this.mainPanel, SETUP_CARD);
    }

    private void startGamePhase(final SetupController completedSetup) {
        var humanPlayer = (Human)completedSetup.getHumanPlayer();
        var botPlayer = (Bot)completedSetup.getBotPlayer();

        if (isSniperSelected) {
            botPlayer.setStrategy(new SniperBot(humanPlayer.getGrid()));
        }

        GameController gameController = new GameControllerImpl(humanPlayer, botPlayer);
        GamePanel gamePanel = new GamePanel(gameController);
        this.mainPanel.add(gamePanel, GAME_CARD);
        this.cardLayout.show(this.mainPanel, GAME_CARD);
    }
}
