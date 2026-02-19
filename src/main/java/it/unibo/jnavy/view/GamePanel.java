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

    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30);

    private boolean inputBlocked = false;
    private final JLabel statusLabel;
    private final GridPanel humanGridPanel;
    private final GridPanel botGridPanel;
    private final BotDifficultyPanel difficultyPanel;
    private final WeatherWidget weatherWidget;
    private final CaptainAbilityButton captainButton;
    private final CaptainNamePanel captainNamePanel;
    private final GameController controller;

    private AmbientSoundManager ambientSound;

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
        dashboardPanel.setBackground(BACKGROUND_COLOR);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel titleLabel = new JLabel("J-NAVY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        this.statusLabel = new JLabel("Your Turn", SwingConstants.CENTER);
        this.statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        this.statusLabel.setForeground(Color.DARK_GRAY);

        headerPanel.add(titleLabel);
        headerPanel.add(this.statusLabel);
        headerPanel.setBackground(BACKGROUND_COLOR);

        this.humanGridPanel = new GridPanel(this.controller.getGridSize(), HUMAN_FLEET,
                                (Position p) -> {
                                    if (this.inputBlocked || !controller.isHumanTurn()) {
                                        return;
                                    }
                                    if (captainButton.isActive()) {
                                        controller.processAbility(p);
                                        this.captainButton.reset();
                                        this.updateDashboard();
                                    }
                                });
        this.botGridPanel = new GridPanel(this.controller.getGridSize(), BOT_FLEET,
                                (Position p) -> {
                                    if (this.inputBlocked || !controller.isHumanTurn()) {
                                        return;
                                    }

                                    if (captainButton.isActive()) {
                                        controller.processAbility(p);
                                        this.captainButton.reset();
                                    } else {
                                        if (controller.getBotCellState(p).isAlreadyHit()) {
                                            return;
                                        }
                                        controller.processShot(p);
                                    }

                                    this.updateDashboard();

                                    if (!controller.isHumanTurn() && !controller.isGameOver()) {

                                        this.inputBlocked = true;
                                        this.statusLabel.setText("Bot is thinking...");
                                        this.statusLabel.setForeground(Color.RED);

                                        Timer botTimer = new Timer(1000, e -> {
                                            controller.playBotTurn();
                                            this.updateDashboard();

                                            this.inputBlocked = false;
                                            this.statusLabel.setText("Your Turn");
                                            this.statusLabel.setForeground(Color.DARK_GRAY);
                                        });

                                        botTimer.setRepeats(false);
                                        botTimer.start();
                                    } else if (controller.isGameOver()) {
                                        if (this.ambientSound != null) {
                                            this.ambientSound.stop();
                                        }
                                    }
                                });

        this.humanGridPanel.setBackground(BACKGROUND_COLOR);
        this.botGridPanel.setBackground(BACKGROUND_COLOR);
        this.updateDashboard();
        gridsContainer.add(this.humanGridPanel);
        gridsContainer.add(this.botGridPanel);
        gridsContainer.setBackground(BACKGROUND_COLOR);

        this.add(headerPanel, BorderLayout.NORTH);
        this.add(gridsContainer, BorderLayout.CENTER);
        this.add(dashboardPanel, BorderLayout.SOUTH);

        this.ambientSound = new AmbientSoundManager("/sounds/ship_horn.wav", 15000);
        this.ambientSound.start();
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