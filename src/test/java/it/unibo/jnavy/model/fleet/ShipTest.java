package it.unibo.jnavy.model.fleet;

import org.junit.jupiter.api.Test;

import it.unibo.jnavy.model.ship.ShipImpl;
import it.unibo.jnavy.model.ship.Ship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Test class for {@link ShipImpl}.
 * It verifies initialization constraints and damage/sinking mechanics.
 */
class ShipTest {

    private static final int MIN_VALID_SIZE = 2;
    private static final int MID_VALID_SIZE_3 = 3;
    private static final int MID_VALID_SIZE_4 = 4;
    private static final int MAX_VALID_SIZE = 5;
    private static final int INVALID_TOO_SMALL_SIZE = 1;
    private static final int INVALID_TOO_LARGE_SIZE = 6;
    private static final int HEALTH_ZERO = 0;
    private static final int HEALTH_ONE = 1;
    private static final int HEALTH_TWO = 2;
    private static final String CREATE_SHIP = "Creating a ship of size ";

    @Test
    void testValidShipCreation() {
        assertDoesNotThrow(() -> new ShipImpl(MIN_VALID_SIZE));
        assertDoesNotThrow(() -> new ShipImpl(MID_VALID_SIZE_3));
        assertDoesNotThrow(() -> new ShipImpl(MAX_VALID_SIZE));
    }

    @Test
    void testInvalidShipCreation() {
        assertThrows(IllegalArgumentException.class, () -> new ShipImpl(INVALID_TOO_SMALL_SIZE),
                CREATE_SHIP + INVALID_TOO_SMALL_SIZE + " should throw an IllegalArgumentException");

        assertThrows(IllegalArgumentException.class, () -> new ShipImpl(INVALID_TOO_LARGE_SIZE),
                CREATE_SHIP + INVALID_TOO_LARGE_SIZE + " should throw an IllegalArgumentException");
    }

    @Test
    void testDamageAndSinking() {
        final Ship ship = new ShipImpl(MIN_VALID_SIZE);

        assertEquals(MIN_VALID_SIZE, ship.getHealth(), "Initial health should match ship size");
        assertFalse(ship.isSunk(), "Ship should not be sunk initially");

        boolean sunk = ship.hit();
        assertFalse(sunk, "Ship should not sink after receiving only one hit");
        assertEquals(HEALTH_ONE, ship.getHealth(), "Health should decrease by 1");

        sunk = ship.hit();
        assertTrue(sunk, "Ship should sink when health reaches 0");
        assertEquals(HEALTH_ZERO, ship.getHealth(), "Health should be 0");
        assertTrue(ship.isSunk(), "Ship status should be sunk");

        assertThrows(IllegalStateException.class, ship::hit,
                "Hitting a ship that is already sunk should throw an IllegalStateException");
    }

    @Test
    void testConstructorValidation() {

        assertDoesNotThrow(() -> new ShipImpl(MIN_VALID_SIZE),
                CREATE_SHIP + MIN_VALID_SIZE + " should be valid");
        assertDoesNotThrow(() -> new ShipImpl(MAX_VALID_SIZE),
                CREATE_SHIP + MAX_VALID_SIZE + " should be valid");

        assertThrows(IllegalArgumentException.class, () -> new ShipImpl(INVALID_TOO_SMALL_SIZE),
                "Creating a ship smaller than MIN_SIZE should throw IllegalArgumentException");

        assertThrows(IllegalArgumentException.class, () -> new ShipImpl(INVALID_TOO_LARGE_SIZE),
                "Creating a ship larger than MAX_SIZE should throw IllegalArgumentException");
    }

    @Test
    void testGetSize() {
        final Ship ship = new ShipImpl(MID_VALID_SIZE_4);
        assertEquals(MID_VALID_SIZE_4, ship.getSize(), "Ship size should match the one provided in constructor");
    }

    @Test
    void testRepairLogic() {
        final Ship ship = new ShipImpl(MID_VALID_SIZE_3);

        assertFalse(ship.repair(), "Should not be able to repair a ship at full health");
        assertEquals(MID_VALID_SIZE_3, ship.getHealth(), "Health should remain at max");

        ship.hit();
        assertEquals(HEALTH_TWO, ship.getHealth(), "Health should drop after hit");

        assertTrue(ship.repair(), "Should be able to repair a damaged ship");
        assertEquals(MID_VALID_SIZE_3, ship.getHealth(), "Health should be restored to max");

        assertFalse(ship.repair(), "Should not repair beyond maximum size");

        ship.hit();
        ship.hit();
        ship.hit();

        assertTrue(ship.isSunk(), "Ship should be sunk");
        assertFalse(ship.repair(), "Should not be able to repair a completely sunk ship");
        assertEquals(HEALTH_ZERO, ship.getHealth(), "Health should remain at 0");
    }
}
