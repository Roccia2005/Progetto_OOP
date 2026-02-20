package it.unibo.jnavy.model.fleet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import it.unibo.jnavy.model.ship.ShipImpl;
import it.unibo.jnavy.model.ship.Ship;

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
        ship.hit();
        assertEquals(0, ship.getHealth(), "Health should not go below 0");
    }
}
