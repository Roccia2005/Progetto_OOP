package it.unibo.jnavy.model.weather;

import it.unibo.jnavy.model.grid.Grid;
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

    public WeatherManagerImpl() {
        this.condition = WeatherCondition.SUNNY;
        this.turnCounter = 0;
        this.random = new Random();
    }

    @Override
    public WeatherCondition getCurrentWeather() {
        return this.condition;
    }

    @Override
    public Position applyWeatherEffects(Position target, final Grid grid) {
        if (this.condition == WeatherCondition.SUNNY) {
            return target;
        }
        Position nextPossiblePosition;
        int gridSize = grid.getSize();
        do {
            int offsetX = this.random.nextInt(3) - 1;
            int offsetY = this.random.nextInt(3) - 1;
            nextPossiblePosition = new Position(target.x() + offsetX, target.y() + offsetY);
        } while (nextPossiblePosition.x() < 0 || nextPossiblePosition.x() >= gridSize ||
                nextPossiblePosition.y() < 0 || nextPossiblePosition.y() >= gridSize);
        return nextPossiblePosition;
    }

    @Override
    public void onTurnEnd() {
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
