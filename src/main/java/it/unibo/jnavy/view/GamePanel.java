package it.unibo.jnavy.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import it.unibo.jnavy.controller.CellCondition;
import it.unibo.jnavy.controller.GameController;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherCondition;
import it.unibo.jnavy.view.components.EffectsPanel;
import it.unibo.jnavy.view.components.GameOverPanel;
import it.unibo.jnavy.view.components.bot.BotDifficultyPanel;
import it.unibo.jnavy.view.components.captain.CaptainAbilityButton;
import it.unibo.jnavy.view.components.captain.CaptainNamePanel;
import it.unibo.jnavy.view.components.weather.WeatherNotificationOverlay;
import it.unibo.jnavy.view.components.weather.WeatherWidget;

import it.unibo.jnavy.view.components.grid.GridPanel;

public class GamePanel extends JPanel {

    private static final String HUMAN_FLEET = "My Fleet";
    private static final String BOT_FLEET = "Enemy Fleet";

    private boolean inputBlocked = false;
    private final JLabel statusLabel;
    private final GridPanel humanGridPanel;
    private final GridPanel botGridPanel;
    private final BotDifficultyPanel difficultyPanel;
    private final WeatherWidget weatherWidget;
    private final CaptainAbilityButton captainButton;
    private final CaptainNamePanel captainNamePanel;
    private final GameController controller;

    private final JLayeredPane layeredPane;
    private final JPanel mainContent;
    private final EffectsPanel effectsPanel;
    private final WeatherNotificationOverlay weatherOverlay;
    private final GameOverPanel gameOverPanel;

    public GamePanel(GameController controller, Runnable onMenu) {
        this.controller = controller;
        this.setLayout(new BorderLayout());

        this.layeredPane = new JLayeredPane();
        this.add(layeredPane, BorderLayout.CENTER);

        this.mainContent = new JPanel(new BorderLayout());
        this.mainContent.setOpaque(false);

        JPanel gridsContainer = new JPanel(new GridLayout(1, 2, 40, 0));
        gridsContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gridsContainer.setOpaque(false);

        JPanel dashboardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 10));
        dashboardPanel.setOpaque(false);

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

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel titleLabel = new JLabel("J-NAVY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        this.statusLabel = new JLabel("Your Turn", SwingConstants.CENTER);
        this.statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        this.statusLabel.setForeground(Color.DARK_GRAY);

        headerPanel.add(titleLabel);
        headerPanel.add(this.statusLabel);

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
                                    }
                                });

        gridsContainer.add(this.humanGridPanel);
        gridsContainer.add(this.botGridPanel);

        this.mainContent.add(headerPanel, BorderLayout.NORTH);
        this.mainContent.add(gridsContainer, BorderLayout.CENTER);
        this.mainContent.add(dashboardPanel, BorderLayout.SOUTH);

        this.effectsPanel = new EffectsPanel();
        this.weatherOverlay = new WeatherNotificationOverlay();
        this.gameOverPanel = new GameOverPanel(
                e -> onMenu.run(),
                e -> System.exit(0)
        );

        layeredPane.add(this.mainContent, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(this.effectsPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(this.weatherOverlay, JLayeredPane.MODAL_LAYER);
        layeredPane.add(this.gameOverPanel, JLayeredPane.DRAG_LAYER);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle bounds = new Rectangle(0, 0, GamePanel.this.getWidth(), GamePanel.this.getHeight());
                mainContent.setBounds(bounds);
                effectsPanel.setBounds(bounds);
                weatherOverlay.setBounds(bounds);
                gameOverPanel.setBounds(bounds);
                layeredPane.revalidate();
            }
        });
        this.updateDashboard();
    }

    private void handlePlayerShot(Position p) {
        controller.processShot(p);
        this.updateDashboard();
    }

    private void updateDashboard() {
        int currentCooldown = controller.getCurrentCaptainCooldown();
        captainButton.updateState(currentCooldown);

        humanGridPanel.refresh(pos -> controller.getHumanCellState(pos));
        botGridPanel.refresh(pos -> controller.getBotCellState(pos));

        WeatherCondition currentCondition = this.controller.getWeatherCondition();
        this.weatherWidget.updateWeather(currentCondition);

        if (controller.isGameOver()) {
            boolean playerWon = controller.getBotCellState(new Position(0, 0)) != CellCondition.FOG;
            gameOverPanel.showResult(playerWon);
        }
    }
}