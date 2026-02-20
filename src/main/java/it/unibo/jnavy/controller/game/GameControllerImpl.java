package it.unibo.jnavy.controller.game;

import it.unibo.jnavy.controller.utilities.CellCondition;
import it.unibo.jnavy.model.player.Player;
import it.unibo.jnavy.model.serialization.GameState;
import it.unibo.jnavy.model.serialization.SaveManager;
import it.unibo.jnavy.model.serialization.SaveManagerImpl;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherManager;
import it.unibo.jnavy.model.weather.WeatherManagerImpl;

/**
 * Concrete implementation of the {@link GameController} interface.
 */
public class GameControllerImpl implements GameController {

    private final TurnController turnController;
    private final CombatController combatController;
    private final GameStateController gameStateController;

    private final Player human;
    private final Player bot;

    /**
     * Constructor for a new game.
     *
     * @param human the human player.
     * @param bot the bot player.
     */
    public GameControllerImpl(final Player human, final Player bot) {
        this.human = human;
        this.bot = bot;
        final WeatherManager weather = WeatherManagerImpl.getInstance();
        ((WeatherManagerImpl) weather).reset();

        this.turnController = new TurnController(human, bot, weather, 0, true);
        this.combatController = new CombatController(human, bot, weather, this.turnController);
        this.gameStateController = new GameStateController(human, bot, weather);
    }

    /**
     * Constructor for loading a previous game, initializing it with the saved state.
     *
     * @param state the saved game state.
     */
    public GameControllerImpl(final GameState state) {
        this.human = state.getHuman();
        this.bot = state.getBot();
        final WeatherManager weather = WeatherManagerImpl.getInstance();

        this.turnController = new TurnController(this.human, this.bot, weather, state.getTurnCounter(), state.isHumanTurn());
        this.combatController = new CombatController(this.human, this.bot, weather, this.turnController);
        this.gameStateController = new GameStateController(this.human, this.bot, weather);
    }

    @Override
    public boolean saveGame() {
        final GameState currentState = new GameState(
                this.human,
                this.bot,
                this.turnController.getTurnCounter(),
                this.gameStateController.getWeatherCondition(),
                this.turnController.isHumanTurn()
        );

        final SaveManager saveManager = new SaveManagerImpl();
        return saveManager.save(currentState);
    }

    @Override
    public int getGridSize() {
        return gameStateController.getGridSize();
    }

    @Override
    public int getCaptainCooldown() {
        return gameStateController.getCaptainCooldown();
    }

    @Override
    public int getCurrentCaptainCooldown() {
        return gameStateController.getCurrentCaptainCooldown();
    }

    @Override
    public CellCondition getHumanCellState(final Position p) {
        return gameStateController.getHumanCellState(p);
    }

    @Override
    public CellCondition getBotCellState(final Position p) {
        return gameStateController.getBotCellState(p);
    }

    @Override
    public String getWeatherConditionName() {
        return gameStateController.getWeatherCondition().name();
    }

    @Override
    public String getBotDifficulty() {
        return gameStateController.getBotDifficulty();
    }

    @Override
    public String getPlayerCaptainName() {
        return gameStateController.getPlayerCaptainName();
    }

    @Override
    public boolean captainAbilityTargetsEnemyGrid() {
        return gameStateController.captainAbilityTargetsEnemyGrid();
    }

    @Override
    public void processShot(final Position p) {
        combatController.processShot(p);
    }

    @Override
    public boolean processAbility(final Position p) {
        return combatController.processAbility(p);
    }

    @Override
    public Position playBotTurn() {
        return combatController.playBotTurn();
    }

    @Override
    public boolean isHumanTurn() {
        return turnController.isHumanTurn();
    }

    @Override
    public boolean isGameOver() {
        return turnController.isGameOver();
    }

    @Override
    public boolean isBotDefeated() {
        return turnController.isBotDefeated();
    }
}
