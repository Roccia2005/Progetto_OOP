package it.unibo.jnavy.model.bots;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

public class BeginnerBot extends AbstractBotStrategy{

    @Override
    public Position selectTarget(Grid enemyGrid) {
        return super.getRandomValidPosition(enemyGrid);
    }

    @Override
    protected String getStrategyName() {
        return "Beginner";
    }
}
