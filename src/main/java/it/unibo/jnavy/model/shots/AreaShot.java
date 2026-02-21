package it.unibo.jnavy.model.shots;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.ShotResult;
import it.unibo.jnavy.model.weather.WeatherCondition;
import it.unibo.jnavy.model.weather.WeatherManagerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents an area attack strategy.
 * This strategy hits a 2x2 square area starting from the target position.
 */
public class AreaShot implements HitStrategy {

    private final boolean ignoreWeather;

    public AreaShot(final boolean ignoreWeather) {
        this.ignoreWeather = ignoreWeather;
    }

    @Override
    public List<ShotResult> execute(final Position target, final Grid grid) {
        final List<ShotResult> results = new ArrayList<>();
        Position effectiveTarget = target;
        if (!ignoreWeather) {
            if (WeatherManagerImpl.getInstance().getCurrentWeather() == WeatherCondition.FOG) {
                final int offsetX = new Random().nextInt(3) - 1;
                final int offsetY = new Random().nextInt(3) - 1;
                final Position candidate = new Position(target.x() + offsetX, target.y() + offsetY);

                if (grid.isPositionValid(candidate)) {
                    effectiveTarget = candidate;
                }
            }
        }

        final int vetX = (effectiveTarget.x() == grid.getSize() - 1) ? -1 : 1;
        final int vetY = (effectiveTarget.y() == grid.getSize() - 1) ? -1 : 1;

        final List<Position> targets = List.of(
                effectiveTarget,
                new Position(effectiveTarget.x() + vetX, effectiveTarget.y()),
                new Position(effectiveTarget.x(), effectiveTarget.y() + vetY),
                new Position(effectiveTarget.x() + vetX, effectiveTarget.y() + vetY)
        );

        for (final Position pos : targets) {
            if (grid.isTargetValid(pos)) {
                results.add(grid.receiveShot(pos));
            }
        }
        return results;
    }
}
