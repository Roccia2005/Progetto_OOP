package it.unibo.jnavy.model.weather;

import it.unibo.jnavy.model.ShotResult;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

import java.util.Random;

/**
 * Concrete implementation of the {@link WeatherManager} using the Singleton Pattern.
 * This class handles the logic for random weather transitions based on turn counters.
 * It determines:
 *    - When the weather changes (based on a fixed duration).
 *    - The alternating cycle between {@link WeatherCondition#SUNNY} and {@link WeatherCondition#FOG}.
 *    - The calculation of coordinate deviation when shooting in bad weather.
 */
public class WeatherManagerImpl implements WeatherManager {

    private static WeatherManagerImpl instance;

    private static final int WEATHER_DURATION = 5;
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
        Position nextPossiblePosition;
        int gridSize = grid.getSize();
        do {
            int offsetX = this.random.nextInt(3) - 1;
            int offsetY = this.random.nextInt(3) - 1;
            nextPossiblePosition = new Position(target.x() + offsetX, target.y() + offsetY);
        } while (nextPossiblePosition.x() < 0 || nextPossiblePosition.x() >= gridSize ||
                nextPossiblePosition.y() < 0 || nextPossiblePosition.y() >= gridSize);
        return grid.receiveShot(nextPossiblePosition);
    }

    @Override
    public void processTurnEnd() {
        this.turnCounter++;
        if (this.turnCounter >= WEATHER_DURATION) {
            if (this.condition == WeatherCondition.SUNNY) {
                this.condition = WeatherCondition.FOG;
                // for debugging
                System.out.println("METEO CAMBIATO: " + this.condition);
            } else {
                this.condition = WeatherCondition.SUNNY;
                // for debugging
                System.out.println("METEO CAMBIATO: " + this.condition);
            }
            this.turnCounter = 0;
        }
    }
}
