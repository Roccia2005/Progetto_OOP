package it.unibo.jnavy.model.bots;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

public class BeginnerBot implements BotStrategy{

    @Override
    public Position selectTarget(Grid enemyGrid) {
        List<Position> cellsList = getCellsList(enemyGrid);

        Random random = new Random();
        int randomIndex = random.nextInt(cellsList.size());

        return cellsList.get(randomIndex);
    }

    public List<Position> getCellsList(Grid grid) {
        List<Position> enemyGridToList = Arrays.stream(grid.getCellsMatrix()).flatMap(Arrays::stream).map(c -> c.getPosition()).collect(Collectors.toList());
        return enemyGridToList;
    }
}
