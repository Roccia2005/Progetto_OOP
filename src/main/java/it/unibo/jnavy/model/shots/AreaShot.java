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

        // Define the offsets for a 2x2 area
        final List<Position> targets = List.of(
                target,
                new Position(target.x() + 1, target.y()),       // Right
                new Position(target.x(), target.y() + 1),       // Down
                new Position(target.x() + 1, target.y() + 1) // Diagonal
        );

        for (final Position pos : targets) {
            final ShotResult shotResult = grid.receiveShot(pos);

            results.add(shotResult);
        }
        return results;
    }
}
