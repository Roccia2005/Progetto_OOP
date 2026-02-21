package it.unibo.jnavy.model.bots;

import it.unibo.jnavy.model.utilities.HitType;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.grid.Grid;

import java.io.Serializable;

/**
 * Defines the contract for the Bot's decision-making algorithms.
 */
public interface BotStrategy extends Serializable {

    /**
     * Analyzes the enemy grid and determines the coordinates for the next shot.
     *
     * @param enemyGrid The opponent's {@link Grid}, used to check valid moves.
     * @return The selected target {@link Position}.
     */
    Position selectTarget(Grid enemyGrid);

    /**
     * Receives feedback on the outcome of the last shot.
     * This allows smart strategies (like Pro) to "learn" from the result.
     * Used by simplified "Reinforced Learning" bot implementations.
     * For example: if the result is {@link HitType#HIT}, the bot will try adjacent cells next.
     *
     * @param target The position that was targeted.
     * @param result The outcome of the shot (HIT, MISS, SUNK, etc.).
     */
    default void lastShotFeedback(final Position target, final HitType result) { };

    /**
     * Return the display name of this shooting strategy,
     * providing a human-readable identifier for the difficulty level.
     *
     * @return a {@code String} representing the name of the strategy.
     */
    String getStrategy();
}
