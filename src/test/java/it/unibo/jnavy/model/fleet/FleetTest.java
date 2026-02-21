package it.unibo.jnavy.model.fleet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.jnavy.model.ship.ShipImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Test class for {@link FleetImpl}.
 * It verifies the fleet composition rules.
 */
class FleetTest {

    private static final int SHIP_SIZE_5 = 5;
    private static final int SHIP_SIZE_4 = 4;
    private static final int SHIP_SIZE_3 = 3;
    private static final int SHIP_SIZE_2 = 2;
    private static final int INVALID_SHIP_SIZE_6 = 6;
    private static final int ONE_SHIP = 1;
    private static final int ZERO_SHIPS = 0;

    private Fleet fleet;

    @BeforeEach
    void setUp() {
        this.fleet = new FleetImpl();
    }

    @Test
    void testCompleteFleetTopology() {
        assertDoesNotThrow(() -> {
            fleet.addShip(new ShipImpl(SHIP_SIZE_5));
            fleet.addShip(new ShipImpl(SHIP_SIZE_4));
            fleet.addShip(new ShipImpl(SHIP_SIZE_3));
            fleet.addShip(new ShipImpl(SHIP_SIZE_3));
            fleet.addShip(new ShipImpl(SHIP_SIZE_2));
        });

        assertTrue(fleet.isTopologyValid(), "The fleet containing proper ships must be valid");
    }

    @Test
    void testInvalidTopologyExcessShips() {
        fleet.addShip(new ShipImpl(SHIP_SIZE_2));
        assertThrows(IllegalStateException.class, () -> fleet.addShip(new ShipImpl(SHIP_SIZE_2)),
                "Adding more ships of a specific type than allowed should throw IllegalStateException");
    }

    @Test
    void testFleetFullLimit() {
        fleet.addShip(new ShipImpl(SHIP_SIZE_5));
        fleet.addShip(new ShipImpl(SHIP_SIZE_4));
        fleet.addShip(new ShipImpl(SHIP_SIZE_3));
        fleet.addShip(new ShipImpl(SHIP_SIZE_3));
        fleet.addShip(new ShipImpl(SHIP_SIZE_2));

        assertThrows(IllegalStateException.class, () -> fleet.addShip(new ShipImpl(SHIP_SIZE_4)),
                "Adding a ship to a full fleet must throw IllegalStateException");
    }

    @Test
    void testIncompleteFleet() {
        fleet.addShip(new ShipImpl(SHIP_SIZE_5));
        fleet.addShip(new ShipImpl(SHIP_SIZE_4));

        assertFalse(fleet.isTopologyValid(), "A partial fleet should not be considered valid");
    }

    @Test
    void testAddInvalidShipSize() {
        assertThrows(IllegalArgumentException.class, () -> fleet.addShip(new ShipImpl(INVALID_SHIP_SIZE_6)),
                "Adding a ship with an invalid size should throw an IllegalArgumentException");
    }

    @Test
    void testIsDefeated() {
        assertFalse(fleet.isDefeated(), "An empty fleet should not be considered defeated");

        final ShipImpl ship1 = new ShipImpl(SHIP_SIZE_2);
        fleet.addShip(ship1);

        assertFalse(fleet.isDefeated(), "A fleet with an intact ship should not be defeated");

        ship1.hit();
        ship1.hit();

        assertTrue(fleet.isDefeated(), "The fleet should be defeated when all its ships are sunk");
    }

    @Test
    void testGetShipsReturnsCopy() {
        final ShipImpl ship = new ShipImpl(SHIP_SIZE_3);
        fleet.addShip(ship);

        final var ships = fleet.getShips();
        assertEquals(ONE_SHIP, ships.size(), "Fleet should contain exactly 1 ship");

        ships.clear();

        assertEquals(ONE_SHIP, fleet.getShips().size(), "Modifying the returned list should not affect the actual fleet");
    }

    @Test
    void testRemoveShip() {
        final ShipImpl ship = new ShipImpl(SHIP_SIZE_4);
        fleet.addShip(ship);

        assertTrue(fleet.getShips().contains(ship), "Fleet should contain the added ship");

        fleet.removeShip(ship);

        assertFalse(fleet.getShips().contains(ship), "Fleet should not contain the ship after removal");
        assertEquals(ZERO_SHIPS, fleet.getShips().size(), "Fleet should be empty after removing the only ship");
    }
}
