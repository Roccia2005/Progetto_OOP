package it.unibo.jnavy.model.weather;

import it.unibo.jnavy.model.grid.Grid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import it.unibo.jnavy.model.grid.GridImpl;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.ShotResult;

/**
 * Test class for {@link WeatherManagerImpl}.
 */
public class WeatherManagerTest {

    private WeatherManager weatherManager;

    /**
     * Sets up the test environment before each test.
     * Retrives the WeatherManager singleton instance and resets its internal state.
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
     * Verifies that the weather condition changes every 5 turns.
     */
    @Test
    void testWeatherChangeOnTurns() {
        // Verify initial state
        assertEquals(WeatherCondition.SUNNY, this.weatherManager.getCurrentWeather());

        // Advance 5 turns (Weather duration)
        for (int i = 0; i < 5; i++) {
            this.weatherManager.processTurnEnd();
        }
        // Weather should be FOG
        assertEquals(WeatherCondition.FOG, this.weatherManager.getCurrentWeather());

        //Advance another 5 turns
        for (int i = 0; i < 5; i++) {
            this.weatherManager.processTurnEnd();
        }
        // Weather should return to SUNNY
        assertEquals(WeatherCondition.SUNNY, this.weatherManager.getCurrentWeather());
    }

    /**
     * Verifies shot precision under SUNNY conditions.
     * With these weather conditions, the hit position must
     * match the targeted position exactly.
     */
    @Test
    void testSunnyPrecision() {
        assertEquals(WeatherCondition.SUNNY, this.weatherManager.getCurrentWeather());

        Grid grid = new GridImpl();
        Position target = new Position(0, 0);

        ShotResult shotResult = this.weatherManager.applyWeatherEffects(target, grid);

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
        assertEquals(WeatherCondition.SUNNY, this.weatherManager.getCurrentWeather());

        for (int i = 0; i < 5; i++) {
            this.weatherManager.processTurnEnd();
        }
        assertEquals(WeatherCondition.FOG, this.weatherManager.getCurrentWeather());

        Grid grid = new GridImpl();
        Position target = new Position(5, 5);
        ShotResult shotResult = this.weatherManager.applyWeatherEffects(target, grid);

        // Calculate distance (Delta)
        Position actualPos = shotResult.position();
        int diffX = Math.abs(target.x() - actualPos.x());
        int diffY = Math.abs(target.y() - actualPos.y());

        assertTrue(diffX <= 1 && diffY <= 1);

    }

    /**
     * Verifies behavior at grid boundaries (Edge Case) under FOG conditions.
     * When shooting at the corner (0,0), the deviation logic must ensure that:
     * 1. No negative coordinates are generated (out of bounds).
     * 2. The shot remains within the valid neighborhood: (0,0), (0,1), (1,0), or (1,1).
     */
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

        // Check that the shot is within the valid neighborhood (0,0), (0,1), (1,0), or (1,1)
        assertTrue(hitPosition.x() >= 0 && hitPosition.x() <= 1);
        assertTrue(hitPosition.y() >= 0 && hitPosition.y() <= 1);
    }

    /**
     * Verifies that under FOG conditions, a shot is never redirected to a cell
     * that has already been hit.
     * This test simulates a scenario where the target (5, 5) is surrounded
     * by 8 already-hit cells in its 3x3 neighborhood, leaving only one valid option
     * (4, 4). The WeatherManager must identify and pick the only available
     * cell instead of causing an invalid shot.
     */
    @Test
    void testWeatherFogDoesNotShootAlreadyHitCells() {
        for (int i = 0; i < 5; i++) {
            this.weatherManager.processTurnEnd();
        }
        assertEquals(WeatherCondition.FOG, this.weatherManager.getCurrentWeather());

        Grid grid = new GridImpl();
        Position target = new Position(5, 5);

        for (int x = 4; x <= 6; x++) {
            for (int y = 4; y <= 6; y++) {
                Position p = new Position(x, y);
                if (!(x == 4 && y == 4)) {
                    grid.receiveShot(p);
                }
            }
        }
        ShotResult shotResult = this.weatherManager.applyWeatherEffects(target, grid);
        assertEquals(new Position(4, 4), shotResult.position());
        assertTrue(grid.getCell(new Position(4, 4)).get().isHit());
    }
}
