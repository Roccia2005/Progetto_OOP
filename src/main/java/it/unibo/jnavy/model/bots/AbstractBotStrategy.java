package it.unibo.jnavy.model.bots;

import java.util.List;
import java.util.Random;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

public abstract class AbstractBotStrategy implements BotStrategy{
    protected final Random random = new Random();

    protected Position getRandomValidPosition(Grid enemyGrid) {
        List<Position> cellsList = getValidCellsList(enemyGrid);

        if (cellsList.isEmpty()) {
            throw new IllegalStateException("The bot can't shoot, no valid cells in grid");
        }
        return cellsList.get(getRandomIndex(cellsList));
    }

    protected List<Position> getValidCellsList(Grid grid) {
        return grid.getPositions();
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
