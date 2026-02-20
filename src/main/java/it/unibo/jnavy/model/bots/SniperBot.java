package it.unibo.jnavy.model.bots;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

public class SniperBot extends AbstractBotStrategy {

    private final List<Position> knownTargets;
    private static final double ERROR_PERCENTAGE = 0.18;
    private final Random random = new Random();

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    public SniperBot(final List<Position> targetPositions) {
        this.knownTargets = new ArrayList<>(targetPositions);
    }

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

    @Override
    protected String getStrategyName() {
        return "Sniper";
    }

}
