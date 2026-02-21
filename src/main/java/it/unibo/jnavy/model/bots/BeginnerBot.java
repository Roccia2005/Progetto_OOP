package it.unibo.jnavy.model.bots;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

public final class BeginnerBot extends AbstractBotStrategy {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    @Override
    public Position selectTarget(final Grid enemyGrid) {
        return super.getRandomValidPosition(enemyGrid);
    }

    @Override
    protected String getStrategyName() {
        return "Beginner";
    }
}
