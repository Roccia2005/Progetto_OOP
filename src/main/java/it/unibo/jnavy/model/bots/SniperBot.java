package it.unibo.jnavy.model.bots;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

/**
 * A specialized bot strategy that knows the exact positions of enemy ships but has
 * a predefined error percentage that causes it to miss occasionally.
 */
public final class SniperBot extends AbstractBotStrategy {

    /**
     * Serial version UID for serialization.
     */
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private static final double ERROR_PERCENTAGE = 0.18;

    private final List<Position> knownTargets;
    private final Random random = new Random();


    /**
     * Constructs a SniperBot with a list of known ship positions.
     *
     * @param targetPositions the list of {@link Position} objects where ships are located
     */
    public SniperBot(final List<Position> targetPositions) {
        this.knownTargets = new ArrayList<>(targetPositions);
    }

    /**
     * Selects a target by either picking a known ship position or a random one
     * based on the error percentage.
     *
     * @param enemyGrid the grid representing the opponent's territory
     * @return the selected {@link Position}
     */
    @Override
    public Position selectTarget(final Grid enemyGrid) {
        this.knownTargets.removeIf(p -> !enemyGrid.isTargetValid(p));
        final boolean miss = this.random.nextDouble() < ERROR_PERCENTAGE;

        if (!miss && !this.knownTargets.isEmpty()) {
            final int randomIndex = this.random.nextInt(this.knownTargets.size());
            return this.knownTargets.get(randomIndex);
        } else {
            return super.getRandomValidPosition(enemyGrid);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return the string "Sniper"
     */
    @Override
    protected String getStrategyName() {
        return "Sniper";
    }

}
