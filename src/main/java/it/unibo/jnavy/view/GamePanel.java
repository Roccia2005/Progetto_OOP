package it.unibo.jnavy.view;

import javax.swing.*;
import java.awt.*;

import it.unibo.jnavy.controller.GameController;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherCondition;
import it.unibo.jnavy.view.components.bot.BotDifficultyPanel;
import it.unibo.jnavy.view.components.captain.CaptainAbilityButton;
import it.unibo.jnavy.view.components.captain.CaptainNamePanel;
import it.unibo.jnavy.view.components.weather.WeatherWidget;

import it.unibo.jnavy.view.components.grid.GridPanel;

public class GamePanel extends JPanel {

    private static final String HUMAN_FLEET = "My Fleet";
    private static final String BOT_FLEET = "Enemy Fleet";

    private final GridPanel humanGridPanel;
    private final GridPanel botGridPanel;
    private final BotDifficultyPanel difficultyPanel;
    private final WeatherWidget weatherWidget;
    private final CaptainAbilityButton captainButton;
    private final CaptainNamePanel captainNamePanel;
    private final GameController controller;

    public GamePanel(GameController controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());

        JPanel gridsContainer = new JPanel(new GridLayout(1, 2, 40, 0));
        gridsContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel dashboardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 10));

        this.weatherWidget = new WeatherWidget();
        this.captainButton = new CaptainAbilityButton(this.controller.getCaptainCooldown());
        this.difficultyPanel = new BotDifficultyPanel(this.controller.getBotDifficulty());
        this.captainNamePanel = new CaptainNamePanel(this.controller.getPlayerCaptainName());

        this.captainButton.addActionListener(e -> {
            if (this.captainButton.isEnabled()) {
                this.captainButton.select();
            }
        });

        dashboardPanel.add(this.difficultyPanel);
        dashboardPanel.add(this.weatherWidget);
        dashboardPanel.add(this.captainButton);
        dashboardPanel.add(this.captainNamePanel);

        this.humanGridPanel = new GridPanel(this.controller.getGridSize(), HUMAN_FLEET,
                                            (Position p) -> {
                                                if (captainButton.isActive()) {
                                                    controller.processAbility(p);
                                                    this.captainButton.reset();
                                                    this.updateDashboard();
                                                }
                                             }); 
        this.botGridPanel = new GridPanel(this.controller.getGridSize(), BOT_FLEET, 
                                            (Position p) -> {
                                                if (captainButton.isActive()) {
                                                    controller.processAbility(p);
                                                    this.captainButton.reset();
                                                } else {
                                                    controller.processShot(p);
                                                }
                                                this.updateDashboard();
                                                if (!controller.isHumanTurn() && !controller.isGameOver()) {
                                                    Timer botTimer = new Timer(1000, e -> {
                                                        controller.playBotTurn(); 
                                                        this.updateDashboard();   
                                                    });
                                                botTimer.setRepeats(false); 
                                                botTimer.start();
                                                }
                                            });

        this.updateDashboard();
        gridsContainer.add(this.humanGridPanel);
        gridsContainer.add(this.botGridPanel);

        JLabel titleLabel = new JLabel("J-NAVY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        this.add(titleLabel, BorderLayout.NORTH);
        this.add(gridsContainer, BorderLayout.CENTER);
        this.add(dashboardPanel, BorderLayout.SOUTH);
    }

    private void updateDashboard() {
        int currentCooldown = controller.getCurrentCaptainCooldown();
        captainButton.updateState(currentCooldown);

        humanGridPanel.refresh(pos -> controller.getHumanCellState(pos));
        botGridPanel.refresh(pos -> controller.getBotCellState(pos));

        WeatherCondition currentCondition = this.controller.getWeatherCondition();
        this.weatherWidget.updateWeather(currentCondition);
    }
}