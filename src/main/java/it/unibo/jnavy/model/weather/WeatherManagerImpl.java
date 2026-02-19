package it.unibo.jnavy.model.weather;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.ShotResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Concrete implementation of the {@link WeatherManager} using the Singleton Pattern.
 * This class handles the logic for random weather transitions based on turn counters.
 * It determines:
 *    - When the weather changes (based on a fixed duration).
 *    - The alternating cycle between {@link WeatherCondition#SUNNY} and {@link WeatherCondition#FOG}.
 *    - The calculation of coordinate deviation when shooting in bad weather.
 */
public final class WeatherManagerImpl implements WeatherManager {

    private static WeatherManagerImpl instance;

    private static final int WEATHER_DURATION = 6;
    private WeatherCondition condition;
    private int turnCounter;
    private Random random;

    /**
     * Initializes the weather manager.
     * Starts with {@link WeatherCondition#SUNNY} and a turn counter of 0.
     */
    private WeatherManagerImpl() {
        this.reset();
    }

    /**
     * Returns the single instance of the WeatherManager.
     * If it doesn't exist, it creates it.
     *
     * @return The singleton instance.
     */
    public static synchronized WeatherManagerImpl getInstance() {
        if (instance == null) {
            instance = new WeatherManagerImpl();
        }
        return instance;
    }

    /**
     * Resets the weather manager to its initial state.
     */
    public void reset() {
        this.condition = WeatherCondition.SUNNY;
        this.turnCounter = 0;
        this.random = new Random();
    }

    @Override
    public WeatherCondition getCurrentWeather() {
        return this.condition;
    }

    @Override
    public ShotResult applyWeatherEffects(final Position target, final Grid grid) {
        if (this.condition == WeatherCondition.SUNNY) {
            return grid.receiveShot(target);
        }

        final List<Position> validPosition = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                final Position p = new Position(target.x() + dx, target.y() + dy);
                if (grid.isTargetValid(p)) {
                    validPosition.add(p);
                }
            }
        }
        if (validPosition.isEmpty()) {
            return grid.receiveShot(target);
        }
        final Position chosenPosition = validPosition.get(this.random.nextInt(validPosition.size()));
        return grid.receiveShot(chosenPosition);
    }

    @Override
    public void processTurnEnd() {
        this.turnCounter++;
        if (this.turnCounter >= WEATHER_DURATION) {
            final int chance = this.random.nextInt(3);
            if (chance < 2) {
                this.condition = WeatherCondition.SUNNY;
            } else {
                this.condition = WeatherCondition.FOG;
            }
            this.turnCounter = 0;
        }
    }
}
