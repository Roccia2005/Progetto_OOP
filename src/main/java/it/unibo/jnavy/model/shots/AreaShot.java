package it.unibo.jnavy.model.shots;

import it.unibo.jnavy.model.ShotResult;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an area attack strategy.
 * This strategy hits a 2x2 square area starting from the target position.
 */
public class AreaShot implements HitStrategy {

    @Override
    public List<ShotResult> execute(final Position target, final Grid grid) {
        final List<ShotResult> results = new ArrayList<>();
        final int vetX = target.x() >= grid.getSize()/2 ? -1 : 1;
        final int vetY = target.y() >= grid.getSize()/2 ? -1 : 1;

        final List<Position> targets = List.of(
                target,
                new Position(target.x() + vetX, target.y()),
                new Position(target.x(), target.y() + vetY),
                new Position(target.x() + vetX, target.y() + vetY)
        );

        for (final Position pos : targets) {
            final ShotResult shotResult = grid.receiveShot(pos);
            results.add(shotResult);
        }
        return results;
    }
}