package it.unibo.jnavy.view.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import it.unibo.jnavy.controller.game.GameController;
import it.unibo.jnavy.controller.utilities.CellCondition;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.view.components.EffectsPanel;
import it.unibo.jnavy.view.components.GameOverPanel;
import it.unibo.jnavy.view.components.weather.WeatherNotificationOverlay;
import it.unibo.jnavy.view.utilities.SoundManager;
import it.unibo.jnavy.view.components.grid.GridPanel;
import java.util.stream.Collectors;

import static it.unibo.jnavy.view.utilities.ViewConstants.*;

public class GamePanel extends JPanel {

    private static final String HUMAN_FLEET = "My Fleet";
    private static final String BOT_FLEET = "Enemy Fleet";
    private static final String SOUNDTRACK_PATH = "/sounds/game_soundtrack.wav";
    private static final String VICTORY_SOUND_PATH = "/sounds/win.wav";
    private static final String LOSE_SOUND_PATH = "/sounds/gameover.wav";
    private static final String BOT_TURN_TEXT = "Bot is thinking...";
    private static final String YOUR_TURN_TEXT = "Your turn";
    private static final Color BOT_TURN_TEXT_COLOR = Color.RED;
    private static final Color YOUR_TURN_TEXT_COLOR = Color.WHITE;
    private static final String GAME_SAVED = "Game saved successfully!";
    private static final String SAVE_ERROR = "Error saving the game.";

    private boolean inputBlocked = false;
    private final GameHeaderPanel headerPanel;
    private final GameDashboardPanel dashboardPanel;
    private final GridPanel humanGridPanel;
    private GridPanel botGridPanel;
    private final GameController controller;
    private final SoundManager ambientSound;
    private boolean gameOverHandled = false;

    private final JLayeredPane layeredPane;
    private final JPanel mainContent;
    private final EffectsPanel effectsPanel;
    private final WeatherNotificationOverlay weatherOverlay;
    private final GameOverPanel gameOverPanel;
    private String lastWeatherCondition;

    public GamePanel(GameController controller, Runnable onMenu) {
        this.controller = controller;
        this.lastWeatherCondition = controller.getWeatherConditionName();
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

        this.dashboardPanel = new GameDashboardPanel(
        this.controller.getBotDifficulty(),
        this.controller.getCaptainCooldown(),
        this.controller.getPlayerCaptainName());

        this.headerPanel = new GameHeaderPanel(() -> {
            if (this.controller.saveGame()) {
                ToastNotification.show(this, GAME_SAVED, MENUBLUE);
            } else {
                ToastNotification.show(this, SAVE_ERROR, MENUBLUE);
            }
        });

        this.humanGridPanel = new GridPanel(this.controller.getGridSize(), HUMAN_FLEET, this::handleHumanGridClick);
        this.botGridPanel = new GridPanel(this.controller.getGridSize(), BOT_FLEET, this::handleBotGridClick);
        this.humanGridPanel.setBackground(BACKGROUND_COLOR);
        this.botGridPanel.setBackground(BACKGROUND_COLOR);

        this.updateDashboard();

        gridsContainer.add(this.humanGridPanel);
        gridsContainer.add(this.botGridPanel);

        this.mainContent.add(this.headerPanel, BorderLayout.NORTH);
        this.mainContent.add(gridsContainer, BorderLayout.CENTER);
        this.mainContent.add(this.dashboardPanel, BorderLayout.SOUTH);

        this.gameOverPanel = new GameOverPanel(e -> onMenu.run(), e -> System.exit(0));

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
        this.ambientSound.start();
    }

    private void updateDashboard() {
        String currentCondition = this.controller.getWeatherConditionName();
        this.dashboardPanel.updateDashboard(controller.getCurrentCaptainCooldown(), currentCondition);
        humanGridPanel.refresh(pos -> controller.getHumanCellState(pos));
        botGridPanel.refresh(pos -> controller.getBotCellState(pos));

        if (!this.lastWeatherCondition.isEmpty() && !this.lastWeatherCondition.equals(currentCondition)) {
            this.weatherOverlay.showWeatherAlert(currentCondition);
        }
        this.lastWeatherCondition = currentCondition;

        if (controller.isGameOver() && !this.gameOverHandled) {
            this.gameOverHandled = true;

            Timer delayTimer = new Timer(900, e -> {
                this.showEndGameScreen(controller.isBotDefeated());
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        }
    }

    private void triggerBotTurn() {
        this.headerPanel.setStatus(BOT_TURN_TEXT, BOT_TURN_TEXT_COLOR);

        Timer botTimer = new Timer(1000, e -> {
            Position target = controller.playBotTurn();
            if (target == null) return;

            JButton targetButton = humanGridPanel.getButtonAt(target);
            var state = controller.getHumanCellState(target);
            boolean isHit = state == CellCondition.HIT_SHIP || state == CellCondition.SUNK_SHIP;
            this.effectsPanel.startShot(List.of(targetButton), isHit,
                    () -> humanGridPanel.refresh(pos -> controller.getHumanCellState(pos)),
                    () -> {
                this.updateDashboard();
                this.inputBlocked = false;
                this.headerPanel.setStatus(YOUR_TURN_TEXT, YOUR_TURN_TEXT_COLOR);
            });
        });
        botTimer.setRepeats(false);
        botTimer.start();
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

    private void handleHumanGridClick(Position p) {
        if (this.inputBlocked || !controller.isHumanTurn()) return;
        
        if (this.dashboardPanel.isCaptainAbilityActive() && !controller.captainAbilityTargetsEnemyGrid()) {
            controller.processAbility(p);
            this.dashboardPanel.resetCaptainAbility();
            this.updateDashboard();
        }
    }

    private void handleBotGridClick(Position p) {
        if (this.inputBlocked || !controller.isHumanTurn() || controller.isGameOver()) return;

        CellCondition clickedState = controller.getBotCellState(p);
        boolean isAlreadyRevealed = (clickedState == CellCondition.HIT_SHIP || clickedState == CellCondition.SUNK_SHIP || clickedState == CellCondition.HIT_WATER);

        if (isAlreadyRevealed && !this.dashboardPanel.isCaptainAbilityActive()) return;

        this.inputBlocked = true;
        boolean isAbility = this.dashboardPanel.isCaptainAbilityActive();

        List<Position> previousHits = TargetCalculator.getAllRevealedPositions(controller);

        if (isAbility) {
            controller.processAbility(p);
            this.dashboardPanel.resetCaptainAbility();
            if (controller.getPlayerCaptainName().equalsIgnoreCase("SonarOfficer")) {
                this.updateDashboard();
                if (!controller.isHumanTurn() && !controller.isGameOver()) {
                    triggerBotTurn();
                } else {
                    this.inputBlocked = false;
                }
                return;
            }
        } else {
            controller.processShot(p);
        }

        List<Position> newHits = TargetCalculator.getAllRevealedPositions(controller);
        newHits.removeAll(previousHits);

        List<Position> targets = TargetCalculator.determineAnimationTargets(
            p, newHits, isAbility, controller.getPlayerCaptainName(), controller.getGridSize()
        );
        
        boolean anyHit = targets.stream().anyMatch(pos -> {
            var state = controller.getBotCellState(pos);
            return state == CellCondition.HIT_SHIP || state == CellCondition.SUNK_SHIP;
        });

        List<Component> targetButtons = targets.stream().map(botGridPanel::getButtonAt).collect(Collectors.toList());

        this.effectsPanel.startShot(targetButtons, anyHit,
                () -> botGridPanel.refresh(pos -> controller.getBotCellState(pos)),
                () -> {
                    this.updateDashboard();
                    if (!controller.isHumanTurn() && !controller.isGameOver()) {
                        triggerBotTurn();
                    } else {
                        this.inputBlocked = false;
                    }
                }
        );
    }
}