package it.unibo.jnavy.model.weather;

import it.unibo.jnavy.model.utilities.Position;

import java.util.Random;

/**
 * Concrete implementation of the {@link WeatherManager}.
 * This class handles the logic for random weather transitions based on turn counters.
 * It uses a pseudo-random number generator to determine:
 *    - When the weather changes.
 *    - Which new condition is selected.
 *    - Whether a shot misses due to bad weather conditions.
 */
public class WeatherManagerImpl implements WeatherManager {

    private static final int WEATHER_DURATION = 5;
    private WeatherCondition condition;
    private int turnCounter;
    private Random random;

    WeatherManagerImpl() {
        this.condition = WeatherCondition.SUNNY;
        this.turnCounter = 0;
        this.random = new Random();
    }

    @Override
    public WeatherCondition getCurrentWeather() {
        return this.condition;
    }

    @Override
    public Position applyWeatherEffects(Position target) {
        return null;
    }

    @Override
    public void onTurnEnd() {

    }
}
