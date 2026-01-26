package it.unibo.jnavy.model.weather;

import it.unibo.jnavy.model.ShotResult;
import it.unibo.jnavy.model.grid.Grid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import it.unibo.jnavy.model.grid.GridImpl;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Test class for {@link WeatherManagerImpl}.
 */
public class WeatherManagerTest {

    private WeatherManager weatherManager;

    @BeforeEach
    void setUp() {
        this.weatherManager = WeatherManagerImpl.getInstance();

        ((WeatherManagerImpl) this.weatherManager).reset();
    }

    @Test
    void testInitialCondition() {
        assertEquals(WeatherCondition.SUNNY, this.weatherManager.getCurrentWeather());
    }

    @Test
    void testWeatherChangeOnTurns() {
        assertEquals(WeatherCondition.SUNNY, this.weatherManager.getCurrentWeather());
        for (int i = 0; i < 5; i++) {
            this.weatherManager.processTurnEnd();
        }
        assertEquals(WeatherCondition.FOG, this.weatherManager.getCurrentWeather());
        for (int i = 0; i < 5; i++) {
            this.weatherManager.processTurnEnd();
        }
        assertEquals(WeatherCondition.SUNNY, this.weatherManager.getCurrentWeather());
    }

    @Test
    void testSunnyPrecision() {
        assertEquals(WeatherCondition.SUNNY, this.weatherManager.getCurrentWeather());

        Grid grid = new GridImpl();
        Position target = new Position(0, 0);

        ShotResult shotResult = this.weatherManager.applyWeatherEffects(target, grid);

        assertEquals(target, shotResult.position());
    }

    @Test
    void testFogDeviation() {
        assertEquals(WeatherCondition.SUNNY, this.weatherManager.getCurrentWeather());

        for (int i = 0; i < 5; i++) {
            this.weatherManager.processTurnEnd();
        }
        assertEquals(WeatherCondition.FOG, this.weatherManager.getCurrentWeather());

        Grid grid = new GridImpl();
        Position target = new Position(5, 5);
        ShotResult shotResult = this.weatherManager.applyWeatherEffects(target, grid);
        Position actualPos = shotResult.position();

        int diffX = Math.abs(target.x() - actualPos.x());
        int diffY = Math.abs(target.y() - actualPos.y());

        assertTrue(diffX <= 1 && diffY <= 1);

    }

    @Test
    void fogCornerCase() {
        assertEquals(WeatherCondition.SUNNY, this.weatherManager.getCurrentWeather());
        for (int i = 0; i < 5; i++) {
            this.weatherManager.processTurnEnd();
        }
        assertEquals(WeatherCondition.FOG, this.weatherManager.getCurrentWeather());
        Grid grid = new GridImpl();
        Position corner = new Position(0, 0);
        ShotResult shotResult = this.weatherManager.applyWeatherEffects(corner, grid);
        Position hitPosition = shotResult.position();
        assertNotNull(hitPosition);
        assertTrue(hitPosition.x() >= 0 && hitPosition.x() <= 1);
        assertTrue(hitPosition.y() >= 0 && hitPosition.y() <= 1);
    }
}
