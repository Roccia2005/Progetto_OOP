package it.unibo.jnavy.model.bots;

import java.util.Arrays;
import java.util.List;
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
        List<Position> enemyGridToList = Arrays.stream(grid.getCellMatrix()) //manca controllo nel caso getCellMatrix restituisca null!
        .flatMap(Arrays::stream)
        .filter(c -> !c.isHit())
        .map(Cell::getPosition)
        .collect(Collectors.toList());
        return enemyGridToList;
    }

    protected int getRandomIndex(final List<Position> cellsList) {
        int index = random.nextInt(cellsList.size());
        return index;
    }
}
