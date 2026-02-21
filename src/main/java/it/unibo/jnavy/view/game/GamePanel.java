package it.unibo.jnavy.view.game;

import static it.unibo.jnavy.view.utilities.ViewConstants.BACKGROUND_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.MENUBLUE;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
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

import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * The core UI container for the active gameplay phase.
 * It integrates the player and bot grids, the dashboard, the header, 
 * and handles the visual animations and sound effects during the battle.
 */
public final class GamePanel extends JPanel {

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
    private static final int PADDING = 20;

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private boolean inputBlocked;
    private final GameHeaderPanel headerPanel;
    private final GameDashboardPanel dashboardPanel;
    private final GridPanel humanGridPanel;
    private final GridPanel botGridPanel;
    private final GameController controller;
    private final SoundManager ambientSound;
    private boolean gameOverHandled;

    private final JLayeredPane layeredPane;
    private final JPanel mainContent;
    private final EffectsPanel effectsPanel;
    private final WeatherNotificationOverlay weatherOverlay;
    private final GameOverPanel gameOverPanel;
    private String lastWeatherCondition;

    /**
     * Constructs a new {@code GamePanel}.
     *
     * @param controller the active {@link GameController} governing the game logic.
     * @param onMenu a callback function executed when the player navigates back 
     *      to the main menu from the game over screen.
     */
    public GamePanel(final GameController controller, final Runnable onMenu) {
        this.controller = controller;
        this.lastWeatherCondition = controller.getWeatherConditionName();
        this.effectsPanel = new EffectsPanel();
        this.weatherOverlay = new WeatherNotificationOverlay();
        this.layeredPane = new JLayeredPane();
        this.mainContent = new JPanel(new BorderLayout());
        this.mainContent.setOpaque(false);

        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        this.ambientSound = new SoundManager(SOUNDTRACK_PATH);
        add(layeredPane, BorderLayout.CENTER);

        this.mainContent.setOpaque(false);

        final JPanel gridsContainer = new JPanel(new GridLayout(1, 2, 40, 0));
        gridsContainer.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
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

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                final Rectangle bounds = new Rectangle(0, 0, GamePanel.this.getWidth(), GamePanel.this.getHeight());
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
        final String currentCondition = this.controller.getWeatherConditionName();
        this.dashboardPanel.updateDashboard(controller.getCurrentCaptainCooldown(), currentCondition);
        humanGridPanel.refresh(controller::getHumanCellState);
        botGridPanel.refresh(controller::getBotCellState);

        if (!this.lastWeatherCondition.isEmpty() && !this.lastWeatherCondition.equals(currentCondition)) {
            this.weatherOverlay.showWeatherAlert(currentCondition);
        }
        this.lastWeatherCondition = currentCondition;

        if (controller.isGameOver() && !this.gameOverHandled) {
            this.gameOverHandled = true;

            final Timer delayTimer = new Timer(900, e -> this.showEndGameScreen(controller.isBotDefeated()));
            delayTimer.setRepeats(false);
            delayTimer.start();
        }
    }

    private void triggerBotTurn() {
        this.headerPanel.setStatus(BOT_TURN_TEXT, BOT_TURN_TEXT_COLOR);

        final Timer botTimer = new Timer(1000, e -> {
            final Position target = controller.playBotTurn();
            if (target == null) {
                return;
            }

            final Component targetButton = humanGridPanel.getButtonAt(target);
            final CellCondition state = controller.getHumanCellState(target);
            final boolean isHit = state == CellCondition.HIT_SHIP || state == CellCondition.SUNK_SHIP;

            this.effectsPanel.startShot(List.of(targetButton), isHit,
                    () -> humanGridPanel.refresh(controller::getHumanCellState),
                    () -> {
                this.updateDashboard();
                this.inputBlocked = false;
                this.headerPanel.setStatus(YOUR_TURN_TEXT, YOUR_TURN_TEXT_COLOR);
            });
        });
        botTimer.setRepeats(false);
        botTimer.start();
    }

    /**
     * Halts the game interactions, stops ambient sounds, plays the outcome audio, 
     * and displays the final result overlay.
     *
     * @param isVictory {@code true} if the player won, {@code false} if the bot won.
     */
    public void showEndGameScreen(final boolean isVictory) {
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

    private void handleHumanGridClick(final Position p) {
        if (this.inputBlocked || !controller.isHumanTurn()) {
            return;
        }

        if (this.dashboardPanel.isCaptainAbilityActive() && !controller.captainAbilityTargetsEnemyGrid()) {
            controller.processAbility(p);
            this.dashboardPanel.resetCaptainAbility();
            this.updateDashboard();
        }
    }

    private void handleBotGridClick(final Position p) {
        if (this.inputBlocked || !controller.isHumanTurn() || controller.isGameOver()) {
            return;
        }

        final CellCondition clickedState = controller.getBotCellState(p);
        final boolean isAlreadyRevealed = clickedState == CellCondition.HIT_SHIP ||
                                          clickedState == CellCondition.SUNK_SHIP ||
                                          clickedState == CellCondition.HIT_WATER;

        if (isAlreadyRevealed && !this.dashboardPanel.isCaptainAbilityActive()) {
            return;
        }

        this.inputBlocked = true;
        final boolean isAbility = this.dashboardPanel.isCaptainAbilityActive();

        final List<Position> previousHits = TargetCalculator.getAllRevealedPositions(controller);

        if (isAbility) {
            controller.processAbility(p);
            this.dashboardPanel.resetCaptainAbility();
            if ("SonarOfficer".equalsIgnoreCase(controller.getPlayerCaptainName())) {
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

        final List<Position> newHits = TargetCalculator.getAllRevealedPositions(controller);
        newHits.removeAll(previousHits);

        final List<Position> targets = TargetCalculator.determineAnimationTargets(
        p, newHits, isAbility, controller.getPlayerCaptainName(), controller.getGridSize()
        );

        final boolean anyHit = targets.stream().anyMatch(pos -> {
            final var state = controller.getBotCellState(pos);
            return state == CellCondition.HIT_SHIP || state == CellCondition.SUNK_SHIP;
        });

        final List<Component> targetButtons = targets.stream().map(botGridPanel::getButtonAt).collect(Collectors.toList());

        this.effectsPanel.startShot(targetButtons, anyHit,
                () -> botGridPanel.refresh(controller::getBotCellState),
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
