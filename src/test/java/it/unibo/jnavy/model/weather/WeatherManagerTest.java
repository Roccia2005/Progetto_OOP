package it.unibo.jnavy.model.weather;

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
        this.weatherManager = new WeatherManagerImpl();
    }

    @Test
    void testInitialCondition() {

    }

    @Test
    void testWeatherChangeOnTurns() {

    }

    @Test
    void testFogDeviation() {

    }
}
