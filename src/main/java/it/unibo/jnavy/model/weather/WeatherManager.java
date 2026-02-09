package it.unibo.jnavy.model.weather;

import it.unibo.jnavy.model.ShotResult;
import it.unibo.jnavy.model.TurnObserver;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

/**
 * The manager responsible for handling the dynamic weather system.
 * It extends {@link TurnObserver} to react to game turns automatically.
 * Its main responsibilities are:
 *    - Updating the weather condition periodically.
 *    - Providing the current weather state to the View and Game Logic.
 *    - Calculating if a shot fails due to weather effects (e.g., Fog).
 */
public interface WeatherManager extends TurnObserver {

    /**
     * Retrieves the active weather condition.
     *
     * @return The current {@link WeatherCondition} affecting the battle.
     */
    WeatherCondition getCurrentWeather();

    /**
     * Calculates the actual target position based on weather interference.
     * If the weather is clear (SUNNY), the target remains unchanged.
     * If visibility is low (FOG), the target might shift to a random adjacent cell.
     *
     * @param target The original position aimed by the player.
     * @param grid The grid where the shot effects are applied.
     * @return The result of the shot
     */
    ShotResult applyWeatherEffects(final Position target, final Grid grid);
}
