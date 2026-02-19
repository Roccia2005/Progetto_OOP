package it.unibo.jnavy.model.bots;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

public class SniperBot extends AbstractBotStrategy{

    private final List<Position> knownTargets;
    private static final double ERROR_PERCENTAGE = 0.18;
    private final Random random = new Random();

    public SniperBot(final Grid humanGrid) {
        super();
        this.knownTargets = new ArrayList<>();
        populateKnownTargets(humanGrid);
    }

    private void populateKnownTargets(final Grid grid) {
        for (Position p : grid.getAvailableTargets()) {
            Optional<Cell> c = grid.getCell(p);
            if (c.isPresent() && c.get().isOccupied()) {
                this.knownTargets.add(p);
            }
        }
    }

    @Override
    public Position selectTarget(final Grid enemyGrid) {
        this.knownTargets.removeIf(p -> !enemyGrid.isTargetValid(p));
        boolean miss = this.random.nextDouble() < ERROR_PERCENTAGE;

        if (!miss && !this.knownTargets.isEmpty()) {
            return this.knownTargets.get(0);
        } else {
            return super.getRandomValidPosition(enemyGrid);
        }
    }

}
