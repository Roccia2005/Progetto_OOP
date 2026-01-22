package it.unibo.jnavy.model.bots;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

public class BeginnerBot implements BotStrategy{

    private final Random random = new Random();

    @Override
    public Position selectTarget(Grid enemyGrid) {
        List<Position> cellsList = getCellsList(enemyGrid);

        if (cellsList.isEmpty()) {
            throw new IllegalStateException("The bot can't shoot, no valid cells in grid");
        }
        int randomIndex = getRandomIndex(cellsList);

        return cellsList.get(randomIndex);
    }

    public List<Position> getCellsList(Grid grid) {
        List<Position> enemyGridToList = Arrays.stream(grid.getCellsMatrix())
        .flatMap(Arrays::stream)
        .filter(c -> !c.isHit())
        .map(Cell::getPosition)
        .collect(Collectors.toList());
        return enemyGridToList;
    }

    public int getRandomIndex(final List<Position> cellsList) {
        int index = random.nextInt(cellsList.size());
        return index;
    }
}
