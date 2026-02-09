package it.unibo.jnavy.model.fleet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.jnavy.model.ship.ShipImpl;

/**
 * Test class for {@link FleetImpl}.
 * It verifies the fleet composition rules:
 * - Total ships: 5
 * - Composition: 1x Size 5, 1x Size 4, 2x Size 3, 1x Size 2.
 */
class FleetTest {

    private Fleet fleet;

    @BeforeEach
    void setUp() {
        this.fleet = new FleetImpl();
    }

    @Test
    void testCompleteFleetTopology() {
        // Attempts to add a complete and valid set of ships
        // Expected composition: 1x5, 1x4, 2x3, 1x2
        assertDoesNotThrow(() -> {
            fleet.addShip(new ShipImpl(5));
            fleet.addShip(new ShipImpl(4));
            fleet.addShip(new ShipImpl(3));
            fleet.addShip(new ShipImpl(3));
            fleet.addShip(new ShipImpl(2));
        });

        // Verifies that the fleet is considered valid and complete
        assertTrue(fleet.isTopologyValid(), 
            "The fleet containing (1x5, 1x4, 2x3, 1x2) must be valid");
    }

    @Test
    void testInvalidTopologyExcessShips() {
        // Add the single allowed ship of size 2
        fleet.addShip(new ShipImpl(2));
        
        // Attempt to add a second ship of size 2 (NOT ALLOWED)
        // Should throw exception because the rule is "Max 1 ship of size 2"
        assertThrows(IllegalStateException.class, () -> {
            fleet.addShip(new ShipImpl(2));
        }, "Adding more ships of a specific type than allowed should throw IllegalStateException");
    }

    @Test
    void testFleetFullLimit() {
        // Fill the fleet to its maximum capacity (5 ships)
        fleet.addShip(new ShipImpl(5));
        fleet.addShip(new ShipImpl(4));
        fleet.addShip(new ShipImpl(3));
        fleet.addShip(new ShipImpl(3));
        fleet.addShip(new ShipImpl(2));

        // Attempt to add a 6th ship
        // Should fail regardless of size because the fleet is full
        assertThrows(IllegalStateException.class, () -> {
            fleet.addShip(new ShipImpl(4));
        }, "Adding a ship to a full fleet (5 ships) must throw IllegalStateException");
    }
    
    @Test
    void testIncompleteFleet() {
        // Add only a partial fleet
        fleet.addShip(new ShipImpl(5));
        fleet.addShip(new ShipImpl(4));
        
        // The topology should be invalid because ships are missing
        assertFalse(fleet.isTopologyValid(), 
            "A partial fleet should not be considered valid");
    }
}