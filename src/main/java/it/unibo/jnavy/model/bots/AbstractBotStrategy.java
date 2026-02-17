package it.unibo.jnavy.model.bots;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

public abstract class AbstractBotStrategy implements BotStrategy{
    protected final Random random = new Random();

    protected Position getRandomValidPosition(Grid enemyGrid) {
        List<Position> cellsList = getValidCellsList(enemyGrid);

        if (cellsList.isEmpty()) {
            throw new IllegalStateException("The bot can't shoot, no valid cells in grid");
        }
        int randomIndex = getRandomIndex(cellsList);

        return cellsList.get(randomIndex);
    }

    protected List<Position> getValidCellsList(Grid grid) {
        return grid.getPositions();
    }

    protected int getRandomIndex(final List<Position> cellsList) {
        int index = random.nextInt(cellsList.size());
        return index;
    }
}
