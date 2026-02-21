package it.unibo.jnavy.model.bots;

import java.util.List;
import java.util.Random;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

public abstract class AbstractBotStrategy implements BotStrategy {

    protected final Random random = new Random();

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    protected Position getRandomValidPosition(final Grid enemyGrid) {
        final List<Position> cellsList = getValidCellsList(enemyGrid);

        if (cellsList.isEmpty()) {
            throw new IllegalStateException("The bot can't shoot, no valid cells in grid");
        }
        return cellsList.get(getRandomIndex(cellsList));
    }

    protected List<Position> getValidCellsList(final Grid grid) {
        return grid.getAvailableTargets();
    }

    protected int getRandomIndex(final List<Position> cellsList) {
        return this.random.nextInt(cellsList.size());
    }

    protected abstract String getStrategyName();

    @Override
    public String getStrategy() {
        return getStrategyName();
    }
}
