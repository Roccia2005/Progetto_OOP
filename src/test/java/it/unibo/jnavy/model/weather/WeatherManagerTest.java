package it.unibo.jnavy.model.weather;

import it.unibo.jnavy.model.grid.Grid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import it.unibo.jnavy.model.grid.GridImpl;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.ShotResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link WeatherManagerImpl}.
 */
final class WeatherManagerTest {

    private static final int DURATION = 6;
    private WeatherManager weatherManager;

    /**
     * Sets up the test environment before each test.
     * Retries the WeatherManager singleton instance and resets its internal state.
     * This ensures that every test starts with a clean state
     * (SUNNY weather, turn counter at 0).
     */
    @BeforeEach
    void setUp() {
        this.weatherManager = WeatherManagerImpl.getInstance();
        // Manual reset is necessary because the instance is static (Singleton)
        ((WeatherManagerImpl) this.weatherManager).reset();
    }

    /**
     * Verifies the initial state of the WeatherManager.
     * The weather condition must always be {@link WeatherCondition#SUNNY}.
     */
    @Test
    void testInitialCondition() {
        assertEquals(WeatherCondition.SUNNY, this.weatherManager.getCurrentWeather());
    }

    /**
     * Verifies that the weather condition does not change before the duration of the weather.
     */
    @Test
    void testNoChangeBeforeDuration() {
        // Verify initial state
        assertEquals(WeatherCondition.SUNNY, this.weatherManager.getCurrentWeather());
        for (int i = 0; i < DURATION - 1; i++) {
            this.weatherManager.processTurnEnd();
            assertEquals(WeatherCondition.SUNNY, this.weatherManager.getCurrentWeather());
        }
    }

    /**
     * Verifies the probabilistic nature of weather transitions.
     * After enough cycles, the weather should eventually switch to FOG.
     */
    @Test
    void testWeatherTransitions() {
        boolean sawSunny = false;
        boolean sawFog = false;
        for (int i = 0; i < 100; i++) {
            for (int t = 0; t < DURATION; t++) {
                this.weatherManager.processTurnEnd();
            }
            if (this.weatherManager.getCurrentWeather() == WeatherCondition.SUNNY) {
                sawSunny = true;
            }
            if (this.weatherManager.getCurrentWeather() == WeatherCondition.FOG) {
                sawFog = true;
            }
            if (sawSunny && sawFog) {
                break;
            }
        }
        assertTrue(sawSunny, "Should have seen SUNNY weather");
        assertTrue(sawFog, "Should have seen FOG weather after some transitions");
    }

    /**
     * Verifies shot precision under SUNNY conditions.
     * With these weather conditions, the hit position must
     * match the targeted position exactly.
     */
    @Test
    void testSunnyPrecision() {
        assertEquals(WeatherCondition.SUNNY, this.weatherManager.getCurrentWeather());

        final Grid grid = new GridImpl();
        final Position target = new Position(0, 0);

        final ShotResult shotResult = this.weatherManager.applyWeatherEffects(target, grid);

        assertEquals(target, shotResult.position());
    }

    /**
     * Verifies the deviation effect under FOG conditions.
     * With these weather conditions, the shot may land in a random cell
     * within a 3x3 area centered on the target.
     * This test ensures that the final position is at most 1 cell away (horizontally or vertically)
     * from the original target.
     */
    @Test
    void testFogDeviation() {
        this.weatherManager.setCondition(WeatherCondition.FOG);
        final Grid grid = new GridImpl();
        final Position target = new Position(5, 5);

        for (int i = 0; i < 20; i++) {
            final ShotResult shotResult = this.weatherManager.applyWeatherEffects(target, grid);
            final Position actualPos = shotResult.position();
            final int diffX = Math.abs(target.x() - actualPos.x());
            final int diffY = Math.abs(target.y() - actualPos.y());
            assertTrue(diffX <= 1 && diffY <= 1);
        }
    }

    /**
     * Verifies behavior at grid boundaries (Edge Case) under FOG conditions.
     * When shooting at the corner (0,0), the deviation logic must ensure that:
     * 1. No negative coordinates are generated (out of bounds).
     * 2. The shot remains within the valid neighborhood: (0,0), (0,1), (1,0), or (1,1).
     */
    @Test
    void testFogBoundarySafety() {
        this.weatherManager.setCondition(WeatherCondition.FOG);
        final Grid grid = new GridImpl();
        final Position corner = new Position(0, 0);
        final ShotResult shotResult = this.weatherManager.applyWeatherEffects(corner, grid);
        final Position hitPosition = shotResult.position();
        assertNotNull(hitPosition);

        // Check that the shot is within the valid neighborhood (0,0), (0,1), (1,0), or (1,1)
        assertTrue(hitPosition.x() >= 0 && hitPosition.x() <= 1);
        assertTrue(hitPosition.y() >= 0 && hitPosition.y() <= 1);
    }

    /**
     * Verifies that the weather system does not redirect shots to cells that
     * have already been hit if other valid options are available.
     */
    @Test
    void testFogAvoidHitCells() {
        this.weatherManager.setCondition(WeatherCondition.FOG);
        final Grid grid = new GridImpl();
        final Position target = new Position(2, 2);

        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {

                if (x == 1 && y == 1) { continue; }

                grid.receiveShot(new Position(x, y));
            }
        }
        final ShotResult shotResult = this.weatherManager.applyWeatherEffects(target, grid);
        assertEquals(new Position(1, 1), shotResult.position());
    }

    @Test
    void testReset() {
        this.weatherManager.setCondition(WeatherCondition.FOG);
        ((WeatherManagerImpl) this.weatherManager).reset();
        assertEquals(WeatherCondition.SUNNY, this.weatherManager.getCurrentWeather());
    }
}
