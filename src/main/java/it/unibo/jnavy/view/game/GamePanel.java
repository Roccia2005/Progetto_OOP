package it.unibo.jnavy.view.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import it.unibo.jnavy.controller.game.GameController;
import it.unibo.jnavy.controller.utilities.CellCondition;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherCondition;
import it.unibo.jnavy.view.components.EffectsPanel;
import it.unibo.jnavy.view.components.GameOverPanel;
import it.unibo.jnavy.view.components.bot.BotDifficultyPanel;
import it.unibo.jnavy.view.components.captain.CaptainAbilityButton;
import it.unibo.jnavy.view.components.captain.CaptainNamePanel;
import it.unibo.jnavy.view.components.weather.WeatherNotificationOverlay;
import it.unibo.jnavy.view.components.weather.WeatherWidget;
import it.unibo.jnavy.view.utilities.SoundManager;
import it.unibo.jnavy.view.components.grid.GridPanel;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;
import java.util.stream.Collectors;

import static it.unibo.jnavy.view.utilities.ViewConstants.*;

public class GamePanel extends JPanel {

    private static final String HUMAN_FLEET = "My Fleet";
    private static final String BOT_FLEET = "Enemy Fleet";
    private static final String SOUNDTRACK_PATH = "/sounds/game_soundtrack.wav";
    private static final String VICTORY_SOUND_PATH = "/sounds/win.wav";
    private static final String LOSE_SOUND_PATH = "/sounds/gameover.wav";

    private boolean inputBlocked = false;
    private final JLabel statusLabel;
    private final GridPanel humanGridPanel;
    private GridPanel botGridPanel;
    private final BotDifficultyPanel difficultyPanel;
    private final WeatherWidget weatherWidget;
    private final CaptainAbilityButton captainButton;
    private final CaptainNamePanel captainNamePanel;
    private final GameController controller;
    private final SoundManager ambientSound;
    private boolean gameOverHandled = false;

    private final JLayeredPane layeredPane;
    private final JPanel mainContent;
    private final EffectsPanel effectsPanel;
    private final WeatherNotificationOverlay weatherOverlay;
    private final GameOverPanel gameOverPanel;
    private WeatherCondition lastWeatherCondition;

    public GamePanel(GameController controller, Runnable onMenu) {
        this.controller = controller;
        this.lastWeatherCondition = controller.getWeatherCondition();
        this.effectsPanel = new EffectsPanel();
        this.weatherOverlay = new WeatherNotificationOverlay();
        this.layeredPane = new JLayeredPane();
        this.mainContent = new JPanel(new BorderLayout());
        this.mainContent.setOpaque(false);

        this.setBackground(BACKGROUND_COLOR);
        this.setLayout(new BorderLayout());

        this.ambientSound = new SoundManager(SOUNDTRACK_PATH);
        this.add(layeredPane, BorderLayout.CENTER);

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

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setBackground(BACKGROUND_COLOR);

        JPanel centerTitlePanel = new JPanel(new GridLayout(2, 1));
        centerTitlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("J-NAVY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        this.statusLabel = new JLabel("Your Turn", SwingConstants.CENTER);
        this.statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        this.statusLabel.setForeground(Color.WHITE);

        centerTitlePanel.add(titleLabel);
        centerTitlePanel.add(this.statusLabel);

        JPanel savePanel = getSavePanel();

        JPanel ghostPanel = new JPanel();
        ghostPanel.setOpaque(false);
        ghostPanel.setPreferredSize(savePanel.getPreferredSize());

        headerPanel.add(ghostPanel, BorderLayout.WEST);
        headerPanel.add(centerTitlePanel, BorderLayout.CENTER);
        headerPanel.add(savePanel, BorderLayout.EAST);

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
                                    if (this.inputBlocked || !controller.isHumanTurn() || controller.isGameOver()) return;

                                    CellCondition clickedState = controller.getBotCellState(p);
                                    boolean isAlreadyRevealed = (clickedState == CellCondition.HIT_SHIP ||
                                                                 clickedState == CellCondition.SUNK_SHIP ||
                                                                 clickedState == CellCondition.HIT_WATER);

                                    if (isAlreadyRevealed && !captainButton.isActive()) return;

                                    this.inputBlocked = true;
                                    boolean isAbility = captainButton.isActive();
                                    boolean isGunner = controller.getPlayerCaptainName().toLowerCase().contains("gunner");

                                    List<Position> previousHits = new ArrayList<>();
                                    int size = controller.getGridSize();
                                    for (int r = 0; r < size; r++) {
                                        for (int c = 0; c < size; c++) {
                                            Position pos = new Position(r, c);
                                            CellCondition state = controller.getBotCellState(pos);
                                            if (state == CellCondition.HIT_SHIP || state == CellCondition.SUNK_SHIP || state == CellCondition.HIT_WATER) {
                                                previousHits.add(pos);
                                            }
                                        }
                                    }

                                    if (isAbility) {
                                        controller.processAbility(p);
                                        captainButton.reset();
                                    } else {
                                        controller.processShot(p);
                                    }

                                    List<Position> newHits = new ArrayList<>();
                                    for (int r = 0; r < size; r++) {
                                        for (int c = 0; c < size; c++) {
                                            Position pos = new Position(r, c);
                                            CellCondition state = controller.getBotCellState(pos);
                                            boolean isHitNow = (state == CellCondition.HIT_SHIP || state == CellCondition.SUNK_SHIP || state == CellCondition.HIT_WATER);
                                            if (isHitNow && !previousHits.contains(pos)) {
                                                newHits.add(pos);
                                            }
                                        }
                                    }

                                    List<Position> tempTargets = new ArrayList<>();
                                    if (newHits.isEmpty()) {
                                        if (isAbility && isGunner) {
                                            tempTargets = getAreaPositions(p);
                                        } else {
                                            tempTargets.add(p);
                                        }
                                    } else {
                                        if (isAbility && isGunner) {
                                            // Find the 2x2 area that contains ALL new hits and is closest to the aimed point
                                            Position bestAnchor = newHits.get(0);
                                            int minDistance = Integer.MAX_VALUE;

                                            for (int r = 0; r < size; r++) {
                                                for (int c = 0; c < size; c++) {
                                                    boolean containsAll = true;
                                                    for (Position n : newHits) {
                                                        if (n.x() < r || n.x() > r + 1 || n.y() < c || n.y() > c + 1) {
                                                            containsAll = false;
                                                            break;
                                                        }
                                                    }
                                                    if (containsAll) {
                                                        int dist = Math.abs(r - p.x()) + Math.abs(c - p.y());
                                                        if (dist < minDistance) {
                                                            minDistance = dist;
                                                            bestAnchor = new Position(r, c);
                                                        }
                                                    }
                                                }
                                            }
                                            tempTargets = getAreaPositions(bestAnchor);
                                        } else {
                                            tempTargets.add(newHits.get(0));
                                        }
                                    }

                                    final List<Position> targets = tempTargets;

                                    boolean anyHit = targets.stream().anyMatch(pos -> {
                                        var state = controller.getBotCellState(pos);
                                        return state == CellCondition.HIT_SHIP || state == CellCondition.SUNK_SHIP;
                                    });

                                    List<Component> targetButtons = targets.stream()
                                            .map(botGridPanel::getButtonAt)
                                            .collect(Collectors.toList());

                                    this.effectsPanel.startShot(targetButtons, anyHit,
                                            () -> {
                                                targets.forEach(pos -> botGridPanel.refreshCell(pos, controller.getBotCellState(pos)));
                                            },
                                            () -> {
                                                this.updateDashboard();
                                                if (!controller.isHumanTurn() && !controller.isGameOver()) {
                                                    triggerBotTurn();
                                                } else {
                                                    this.inputBlocked = false;
                                                }
                                            }
                                    );
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

    private void triggerBotTurn() {
        this.statusLabel.setText("Bot is thinking...");
        this.statusLabel.setForeground(Color.RED);

        Timer botTimer = new Timer(1000, e -> {
            Position target = controller.playBotTurn();
            if (target == null) return;

            JButton targetButton = humanGridPanel.getButtonAt(target);
            var state = controller.getHumanCellState(target);
            boolean isHit = state == CellCondition.HIT_SHIP || state == CellCondition.SUNK_SHIP;
            this.effectsPanel.startShot(List.of(targetButton), isHit,
                    () -> humanGridPanel.refreshCell(target, state),
                    () -> {
                this.updateDashboard();
                this.inputBlocked = false;
                this.statusLabel.setText("Your Turn");
                this.statusLabel.setForeground(Color.WHITE);
            });
        });
        botTimer.setRepeats(false);
        botTimer.start();
    }

    @NonNull
    private JPanel getSavePanel() {
        JButton saveButton = new JButton("Save Game");
        saveButton.setFocusPainted(false);
        saveButton.setContentAreaFilled(false);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        saveButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        saveButton.addActionListener(e -> {
            if (this.controller.saveGame()) {
                showAutoClosingMessage("Game saved successfully!");
            } else {
                showAutoClosingMessage("Error saving the game.");
            }
        });

        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        savePanel.setOpaque(false);
        savePanel.add(saveButton);
        return savePanel;
    }

    private void updateDashboard() {
        int currentCooldown = controller.getCurrentCaptainCooldown();
        captainButton.updateState(currentCooldown);

        humanGridPanel.refresh(pos -> controller.getHumanCellState(pos));
        botGridPanel.refresh(pos -> controller.getBotCellState(pos));

        WeatherCondition currentCondition = this.controller.getWeatherCondition();
        if (this.lastWeatherCondition != null && this.lastWeatherCondition != currentCondition) {
            this.weatherOverlay.showWeatherAlert(currentCondition.toString());
        }
        this.lastWeatherCondition = currentCondition;

        this.weatherWidget.updateWeather(currentCondition);

        if (controller.isGameOver() && !this.gameOverHandled) {
            this.gameOverHandled = true;

            Timer delayTimer = new Timer(900, e -> {
                this.showEndGameScreen(controller.isBotDefeated());
            });
            delayTimer.setRepeats(false);
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
            SoundManager.playOneShotSound(VICTORY_SOUND_PATH);
        } else {
            SoundManager.playOneShotSound(LOSE_SOUND_PATH);
        }

        this.gameOverPanel.showResult(isVictory);
    }

    private List<Position> getAreaPositions(Position p) {
        List<Position> area = new ArrayList<>();
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                Position pos = new Position(p.x() + r, p.y() + c);
                if (pos.x() < controller.getGridSize() && pos.y() < controller.getGridSize()) {
                    area.add(pos);
                }
            }
        }
        return area;
    }

    private void showAutoClosingMessage(String message) {
        final JWindow toast = new JWindow(SwingUtilities.getWindowAncestor(this));

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        label.setBackground(new Color(41, 86, 246));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        toast.add(label);
        toast.pack();

        Point location = this.getLocationOnScreen();
        int x = location.x + (this.getWidth() - toast.getWidth()) / 2;
        int y = location.y + (this.getHeight() - toast.getHeight()) / 2;
        toast.setLocation(x, y);

        toast.setVisible(true);

        // Il messaggio scompare da solo dopo 1.5 secondi
        Timer timer = new Timer(1500, e -> {
            toast.setVisible(false);
            toast.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }
}