package it.unibo.jnavy.model.fleet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.jnavy.model.ship.ShipImpl;

import static org.junit.jupiter.api.Assertions.*;

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
        assertThrows(IllegalStateException.class, () -> fleet.addShip(new ShipImpl(2)), "Adding more ships of a specific type than allowed should throw IllegalStateException");
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
        assertThrows(IllegalStateException.class, () -> fleet.addShip(new ShipImpl(4)), "Adding a ship to a full fleet (5 ships) must throw IllegalStateException");
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

    @Test
    void testAddInvalidShipSize() {
        // Attempt to add a ship with a size not defined in FLEET_COMPOSITION
        // Should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> fleet.addShip(new ShipImpl(6)),
                "Adding a ship with an invalid size (e.g., 6) should throw an IllegalArgumentException");
    }

    @Test
    void testIsDefeated() {
        // An empty fleet is not defeated by definition
        assertFalse(fleet.isDefeated(), "An empty fleet should not be considered defeated");

        final ShipImpl ship1 = new ShipImpl(2);
        fleet.addShip(ship1);

        // Fleet has an intact ship, so it's not defeated
        assertFalse(fleet.isDefeated(), "A fleet with an intact ship should not be defeated");

        // Sink the ship
        ship1.hit();
        ship1.hit();

        // Now the fleet should be defeated
        assertTrue(fleet.isDefeated(), "The fleet should be defeated when all its ships are sunk");
    }

    @Test
    void testGetShipsReturnsCopy() {
        final ShipImpl ship = new ShipImpl(3);
        fleet.addShip(ship);

        // Retrieve the list of ships
        final var ships = fleet.getShips();
        assertEquals(1, ships.size(), "Fleet should contain exactly 1 ship");

        // Try to maliciously modify the returned list
        ships.clear();

        // Check that the original fleet was NOT modified (Encapsulation check)
        assertEquals(1, fleet.getShips().size(), "Modifying the returned list should not affect the actual fleet");
    }

    @Test
    void testRemoveShip() {
        final ShipImpl ship = new ShipImpl(4);
        fleet.addShip(ship);

        // Ensure it was added
        assertTrue(fleet.getShips().contains(ship), "Fleet should contain the added ship");

        // Remove the ship
        fleet.removeShip(ship);

        // Ensure it was properly removed
        assertFalse(fleet.getShips().contains(ship), "Fleet should not contain the ship after removal");
        assertEquals(0, fleet.getShips().size(), "Fleet should be empty after removing the only ship");
    }
}
