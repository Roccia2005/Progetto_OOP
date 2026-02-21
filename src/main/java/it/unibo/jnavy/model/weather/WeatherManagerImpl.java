package it.unibo.jnavy.model.weather;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.ShotResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private WeatherCondition condition;
    private final AtomicInteger turnCounter = new AtomicInteger(0);
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
        this.turnCounter.set(0);
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
        final int currentTurn = this.turnCounter.incrementAndGet();
        if (currentTurn >= WEATHER_DURATION) {
            final int chance = this.random.nextInt(3);
            if (chance < 2) {
                this.condition = WeatherCondition.SUNNY;
            } else {
                this.condition = WeatherCondition.FOG;
            }
            this.turnCounter.set(0);
        }
    }

    @Override
    public void setCondition(final WeatherCondition condition) {
        this.condition = condition;
        this.turnCounter.set(0);
    }
}
