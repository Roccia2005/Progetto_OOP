package it.unibo.jnavy.model.fleet;

import org.junit.jupiter.api.Test;

import it.unibo.jnavy.model.ship.ShipImpl;
import it.unibo.jnavy.model.ship.Ship;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ShipImpl}.
 * It verifies initialization constraints (valid sizes) and damage/sinking mechanics.
 */
class ShipTest {

    @Test
    void testValidShipCreation() {
        // Verifies that ships can be created with valid sizes (between 2 and 5)
        new ShipImpl(2);
        new ShipImpl(3);
        new ShipImpl(5);
    }

    @Test
    void testInvalidShipCreation() {
        // Verifies that creating a ship smaller than the minimum size (2) throws an exception
        assertThrows(IllegalArgumentException.class, () -> new ShipImpl(1),
            "Creating a ship of size 1 should throw an IllegalArgumentException");
        // Verifies that creating a ship larger than the maximum size (5) throws an exception
        assertThrows(IllegalArgumentException.class, () -> new ShipImpl(6),
            "Creating a ship of size 6 should throw an IllegalArgumentException");
    }

    @Test
    void testDamageAndSinking() {
        // Initialize a ship of size 2
        final Ship ship = new ShipImpl(2);

        // Check initial state
        assertEquals(2, ship.getHealth(), "Initial health should match ship size");
        assertFalse(ship.isSunk(), "Ship should not be sunk initially");

        // 1. First Hit
        boolean sunk = ship.hit();
        assertFalse(sunk, "Ship should not sink after receiving only one hit (remaining health: 1)");
        assertEquals(1, ship.getHealth(), "Health should decrease by 1");

        // 2. Second Hit (Fatal)
        sunk = ship.hit();
        assertTrue(sunk, "Ship should sink when health reaches 0");
        assertEquals(0, ship.getHealth(), "Health should be 0");
        assertTrue(ship.isSunk(), "Ship status should be sunk");

        // 3. Hit on an already sunk ship
        // We now expect an IllegalStateException because the ship is already sunk
        assertThrows(IllegalStateException.class, ship::hit,
                "Hitting a ship that is already sunk should throw an IllegalStateException");
    }

    @Test
    void testConstructorValidation() {
        // Valid sizes (2 to 5)
        assertDoesNotThrow(() -> new ShipImpl(2), "Creating a ship of size 2 should be valid");
        assertDoesNotThrow(() -> new ShipImpl(5), "Creating a ship of size 5 should be valid");

        // Invalid sizes (too small)
        assertThrows(IllegalArgumentException.class, () -> new ShipImpl(1),
                "Creating a ship smaller than MIN_SIZE (2) should throw IllegalArgumentException");

        // Invalid sizes (too large)
        assertThrows(IllegalArgumentException.class, () -> new ShipImpl(6),
                "Creating a ship larger than MAX_SIZE (5) should throw IllegalArgumentException");
    }

    @Test
    void testGetSize() {
        final Ship ship = new ShipImpl(4);
        assertEquals(4, ship.getSize(), "Ship size should match the one provided in constructor");
    }

    @Test
    void testRepairLogic() {
        final Ship ship = new ShipImpl(3);

        // 1. Cannot repair a fully healthy ship
        assertFalse(ship.repair(), "Should not be able to repair a ship at full health");
        assertEquals(3, ship.getHealth(), "Health should remain at max");

        // 2. Successful repair after a hit
        ship.hit();
        assertEquals(2, ship.getHealth(), "Health should drop to 2 after hit");

        assertTrue(ship.repair(), "Should be able to repair a damaged ship");
        assertEquals(3, ship.getHealth(), "Health should be restored to max (3)");

        // 3. Cannot repair beyond max health
        assertFalse(ship.repair(), "Should not repair beyond maximum size");

        // 4. Cannot repair a sunk ship
        ship.hit();
        ship.hit();
        ship.hit(); // Health is now 0, sunk

        assertTrue(ship.isSunk(), "Ship should be sunk");
        assertFalse(ship.repair(), "Should not be able to repair a completely sunk ship");
        assertEquals(0, ship.getHealth(), "Health should remain at 0");
    }
}
