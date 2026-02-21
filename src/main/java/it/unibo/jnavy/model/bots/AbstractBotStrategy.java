package it.unibo.jnavy.model.bots;

import java.util.List;
import java.util.Random;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

/**
 * This abstract class provides an implementation of the {@link BotStrategy} interface
 * to minimize the effort required to implement specific bot behaviors.
 * It includes utility methods for handling random movements and grid interactions.
 */
public abstract class AbstractBotStrategy implements BotStrategy {

    /**
     * Serial version UID for serialization.
     */
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final Random random = new Random();

    /**
     * Selects a random valid position from the available targets on the enemy grid.
     *
     * @param enemyGrid the grid representing the opponent's territory
     * @return a randomly selected {@link Position} that is valid for a shot
     * @throws IllegalStateException if there are no valid cells left to target in the grid
     */
    protected Position getRandomValidPosition(final Grid enemyGrid) {
        final List<Position> cellsList = getValidCellsList(enemyGrid);

        if (cellsList.isEmpty()) {
            throw new IllegalStateException("The bot can't shoot, no valid cells in grid");
        }
        return cellsList.get(getRandomIndex(cellsList));
    }

    /**
     * Retrieves the list of positions that are currently valid targets on the given grid.
     *
     * @param grid the grid to analyze
     * @return a list of {@link Position} objects representing available targets
     */
    protected List<Position> getValidCellsList(final Grid grid) {
        return grid.getAvailableTargets();
    }

    /**
     * Generates a random index based on the size of the provided list.
     *
     * @param cellsList the list of positions
     * @return a random integer between 0 (inclusive) and the size of the list (exclusive)
     */
    protected int getRandomIndex(final List<Position> cellsList) {
        return this.random.nextInt(cellsList.size());
    }

    /**
     * Gets the specific name of the strategy implemented by the subclass.
     *
     * @return the name of the strategy as a String
     */
    protected abstract String getStrategyName();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStrategy() {
        return getStrategyName();
    }
}
