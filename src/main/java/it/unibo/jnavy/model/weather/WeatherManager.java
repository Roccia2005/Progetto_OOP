package it.unibo.jnavy.model.weather;

/**
 * The manager responsible for handling the dynamic weather system.
 * It extends {@link TurnObserver} to react to game turns automatically.
 * Its main responsibilities are:
 *    - Updating the weather condition periodically.
 *    - Providing the current weather state to the View and Game Logic.
 *    - Calculating if a shot fails due to weather effects (e.g., Fog).
 */
public interface WeatherManager {
}
