package it.unibo.jnavy.model.shots;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.ShotResult;
import it.unibo.jnavy.model.weather.WeatherManagerImpl;

import java.util.List;

/**
 * Represents a standard attack that hits a single cell.
 * This is the default shot strategy.
 */
public class StandardShot implements HitStrategy {

    @Override
    public List<ShotResult> execute(final Position target, final Grid grid) {
        ShotResult result = WeatherManagerImpl.getInstance().applyWeatherEffects(target, grid);
        return List.of(result);
    }
}
