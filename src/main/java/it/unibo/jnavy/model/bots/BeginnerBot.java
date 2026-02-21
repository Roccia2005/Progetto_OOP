package it.unibo.jnavy.model.bots;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Implementation of a simple bot strategy that selects targets randomly.
 * This bot does not track hits or misses and relies entirely on random selection
 * from available valid positions.
 */
public final class BeginnerBot extends AbstractBotStrategy {

    /**
     * Serial version UID for serialization.
     */
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    /**
     * Selects a target by picking a random valid position from the enemy grid.
     *
     * @param enemyGrid the grid representing the opponent's territory
     * @return a randomly selected valid {@link Position}
     */
    @Override
    public Position selectTarget(final Grid enemyGrid) {
        return super.getRandomValidPosition(enemyGrid);
    }

    /**
     * {@inheritDoc}
     * @return the string "Beginner"
     */
    @Override
    protected String getStrategyName() {
        return "Beginner";
    }
}
