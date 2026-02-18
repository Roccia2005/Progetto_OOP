package it.unibo.jnavy.model;

import java.util.List;

import it.unibo.jnavy.model.bots.BotStrategy;
import it.unibo.jnavy.model.fleet.Fleet;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.grid.GridImpl;
import it.unibo.jnavy.model.shots.HitStrategy;
import it.unibo.jnavy.model.shots.StandardShot;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Represents a computer-controlled player (Bot) in the game.
 * <p>
 * The Bot manages its own {@link Fleet} and utilizes a {@link BotStrategy}
 * to determine its moves and target selection logic during the match.
 * It also listens to turn events to handle time-dependent logic.
 */
public class Bot implements Player {

    private Grid grid;
    private BotStrategy strategy;

    /**
     * Constructs a new Bot with a specific strategy.
     * <p>
     * The fleet is automatically initialized using a default implementation.
     * The strategy determines the difficulty level (e.g., Beginner, Pro, Sniper).
     *
     * @param strategy The {@link BotStrategy} that defines the bot's behavior and difficulty.
     */
    public Bot(final BotStrategy strategy) {
        this.grid = new GridImpl();
        this.strategy = strategy;
    }

    @Override
    public Grid getGrid() {
        return this.grid;
    }

    @Override
    public List<ShotResult> createShot(final Position target, final Grid grid) {
        HitStrategy shot = new StandardShot();
            //andrà sostituita la grid in WeatherGrid
        return shot.execute(target, grid);
    }

    /**
     * Updates the Bot's current strategy.
     * <p>
     * This allows changing the difficulty level or behavior of the Bot dynamically
     * (e.g., from a settings menu).
     *
     * @param strategy The new {@link BotStrategy} to apply.
     */
    public void setStrategy(final BotStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Determines the next target position to attack.
     * <p>
     * This method delegates the decision-making process to the current {@link BotStrategy},
     * which analyzes the enemy's grid to select the best coordinate.
     *
     * @param enemyGrid The opponent's {@link Grid}, used to analyze potential targets.
     * @return The selected target {@link Position}.
     */
    public Position decideTarget(Grid enemyGrid) {
        return this.strategy.selectTarget(enemyGrid);
    }

    /**
     * Passes the result of the shot to the strategy so it can learn/update its state.
     * Crucial for advanced bots like ProBot and SniperBot.
     * @param target the position targeted
     * @param result the outcome of the shot
     */
    public void receiveFeedback(final Position target, final HitType result) {
        this.strategy.lastShotFeedback(target, result);
    }

    public String getStrategyName() {
        return this.strategy.getStrategy();
    }
}
