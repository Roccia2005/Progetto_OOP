package it.unibo.jnavy.model.captains;

import it.unibo.jnavy.model.ShotResult;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

import java.util.List;

/**
 * Represents a standard attack that hits a single cell.
 * This is the default shot strategy.
 */
public class StandardShot implements HitStrategy {

    @Override
    public List<ShotResult> execute(final Position target, final Grid grid) {
        ShotResult result = grid.receiveShot(target);
    }
}
