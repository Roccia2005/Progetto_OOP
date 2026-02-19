package it.unibo.jnavy.view.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import it.unibo.jnavy.controller.game.GameController;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherCondition;
import it.unibo.jnavy.view.components.EffectsPanel;
import it.unibo.jnavy.view.components.GameOverPanel;
import it.unibo.jnavy.view.AmbientSoundManager;
import it.unibo.jnavy.view.components.bot.BotDifficultyPanel;
import it.unibo.jnavy.view.components.captain.CaptainAbilityButton;
import it.unibo.jnavy.view.components.captain.CaptainNamePanel;
import it.unibo.jnavy.view.components.weather.WeatherNotificationOverlay;
import it.unibo.jnavy.view.components.weather.WeatherWidget;
import it.unibo.jnavy.view.components.grid.GridPanel;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

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
    private final AmbientSoundManager ambientSound;
    private boolean gameOverHandled = false;

    private final JLayeredPane layeredPane;
    private final JPanel mainContent;
    private final EffectsPanel effectsPanel;
    private final WeatherNotificationOverlay weatherOverlay;
    private final GameOverPanel gameOverPanel;

    public GamePanel(GameController controller, Runnable onMenu) {
        this.controller = controller;
        this.setBackground(BACKGROUND_COLOR);
        this.setLayout(new BorderLayout());

        this.ambientSound = new AmbientSoundManager("/sounds/game_soundtrack.wav", 144000);
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
        dashboardPanel.setBackground(BACKGROUND_COLOR);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel titleLabel = new JLabel("J-NAVY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        this.statusLabel = new JLabel("Your Turn", SwingConstants.CENTER);
        this.statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        this.statusLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel);
        headerPanel.add(this.statusLabel);
        headerPanel.setBackground(BACKGROUND_COLOR);

        this.humanGridPanel = new GridPanel(this.controller.getGridSize(), HUMAN_FLEET,
                                (Position p) -> {
                                    if (this.inputBlocked || !controller.isHumanTurn()) {
                                        return;
                                    }
                                    if (captainButton.isActive() && !controller.captainAbilityTargetsEnemyGrid()) {
                                        controller.processAbility(p);
                                        this.captainButton.reset();
                                        this.updateDashboard();
                                    }
                                });
        this.botGridPanel = new GridPanel(this.controller.getGridSize(), BOT_FLEET,
                                (Position p) -> {
                                    if (this.inputBlocked || !controller.isHumanTurn()) return;

                                    if (captainButton.isActive()) {
                                        if (!controller.captainAbilityTargetsEnemyGrid()) return;
                                        if (controller.getBotCellState(p).isAlreadyHit()) return;
                                        controller.processAbility(p);
                                        this.captainButton.reset();
                                    } else {
                                        if (controller.getBotCellState(p).isAlreadyHit()) return;
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
                                            this.statusLabel.setForeground(Color.WHITE);
                                        });

                                        botTimer.setRepeats(false);
                                        botTimer.start();
//                                    } else if (controller.isGameOver()) {
//                                        if (this.ambientSound != null) {
//                                            this.ambientSound.stop();
//                                        }
                                   }
                                });

        this.humanGridPanel.setBackground(BACKGROUND_COLOR);
        this.botGridPanel.setBackground(BACKGROUND_COLOR);
        this.updateDashboard();
        gridsContainer.add(this.humanGridPanel);
        gridsContainer.add(this.botGridPanel);
        gridsContainer.setBackground(BACKGROUND_COLOR);

        this.ambientSound.start();
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

    private void updateDashboard() {
        int currentCooldown = controller.getCurrentCaptainCooldown();
        captainButton.updateState(currentCooldown);

        humanGridPanel.refresh(pos -> controller.getHumanCellState(pos));
        botGridPanel.refresh(pos -> controller.getBotCellState(pos));

        WeatherCondition currentCondition = this.controller.getWeatherCondition();
        this.weatherWidget.updateWeather(currentCondition);

        if (controller.isGameOver() && !this.gameOverHandled) {
            this.gameOverHandled = true;

            Timer delayTimer = new Timer(900, e -> {
                this.showEndGameScreen(controller.isBotDefeated());
            });
            delayTimer.setRepeats(false); // Only trigger once!
            delayTimer.start();
        }
    }

    private void playOneShotSound(String filePath) {
        new Thread(() -> {
            try {
                URL soundUrl = getClass().getResource(filePath);
                if (soundUrl != null) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundUrl);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                } else {
                    System.err.println("SOUND ERROR: File not found -> " + filePath);
                }
            } catch (Exception e) {
                System.err.println("SOUND ERROR: Format not supported -> " + e.getMessage());
            }
        }).start();
    }

    public void showEndGameScreen(boolean isVictory) {
        if (this.ambientSound != null) {
            this.ambientSound.stop();
        }

        if (isVictory) {
            playOneShotSound("/sounds/win.wav");
        } else {
            playOneShotSound("/sounds/gameover.wav");
        }

        this.gameOverPanel.showResult(isVictory);
    }
}