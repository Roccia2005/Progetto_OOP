package it.unibo.jnavy.model.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.cell.CellImpl;
import it.unibo.jnavy.model.ship.ShipImpl;
import it.unibo.jnavy.model.utilities.HitType;
import it.unibo.jnavy.model.utilities.Position;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CellImpl}.
 * It verifies the correct behavior of cell status updates and strict shooting rules.
 */
class CellTest {

    private Cell cell;

    @BeforeEach
    void setUp() {
        // Initialize a new empty cell at position (0,0) before each test
        this.cell = new CellImpl(new Position(0, 0));
    }

    @Test
    void testDoubleShotStrict() {
        // 1. First shot (Water / MISS)
        assertEquals(HitType.MISS, cell.receiveShot(), "First shot on empty cell should be MISS");

        // Check if the cell status is updated
        assertTrue(cell.isHit(), "Cell should be marked as hit after receiving a shot");

        // 2. Second shot -> MUST throw exception (Strict Rule)
        // We expect the system to prevent shooting at the same location twice
        assertThrows(IllegalStateException.class, () -> {
            cell.receiveShot();
        }, "Shooting on an already hit cell must throw an IllegalStateException");
    }

    @Test
    void testHitOnShip() {
        // Place a ship in the cell
        cell.setShip(new ShipImpl(3));

        // 1. First shot -> HIT
        assertEquals(HitType.HIT, cell.receiveShot(), "Shot on occupied cell should return HIT");
        assertTrue(cell.isHit(), "Cell should be marked as hit");

        // 2. Second shot -> Exception
        assertThrows(IllegalStateException.class, cell::receiveShot,
            "Shooting again on a hit ship segment must throw exception");
    }

    @Test
    void testInitialStateAndGetters() {
        assertEquals(new Position(0, 0), cell.getPosition(), "Position should match the one set in constructor");
        assertFalse(cell.isOccupied(), "A new cell should not be occupied");
        assertFalse(cell.isHit(), "A new cell should not be hit");
        assertTrue(cell.getShip().isEmpty(), "A new cell should not contain a ship");
        assertTrue(cell.getScanResult().isEmpty(), "A new cell should have no scan result");
        assertFalse(cell.isDetectable(), "A cell without a ship should not be detectable");
    }

    @Test
    void testShipPlacementAndDetectability() {
        final ShipImpl ship = new ShipImpl(3);
        cell.setShip(ship);

        assertTrue(cell.isOccupied(), "Cell should be occupied after setting a ship");
        assertTrue(cell.getShip().isPresent(), "Cell should return the placed ship");
        assertEquals(ship, cell.getShip().get(), "The ship returned should be the one placed");
        assertTrue(cell.isDetectable(), "A cell with an intact ship should be detectable");
    }

    @Test
    void testSunkShip() {
        // Create a ship of size 2 and two cells that share it
        final ShipImpl ship = new ShipImpl(2);
        final Cell cell2 = new CellImpl(new Position(0, 1));

        cell.setShip(ship);
        cell2.setShip(ship);

        // First hit -> HIT
        assertEquals(HitType.HIT, cell.receiveShot(), "First hit should return HIT");
        assertFalse(cell.isDetectable(), "A hit cell should no longer be detectable");

        // Second hit -> SUNK
        assertEquals(HitType.SUNK, cell2.receiveShot(), "Second hit on a size 2 ship should return SUNK");
        assertFalse(cell2.isDetectable(), "A sunk cell should not be detectable");
    }

    @Test
    void testRepairLogic() {
        final ShipImpl ship = new ShipImpl(2);
        cell.setShip(ship);

        // 1. Cannot repair water
        final Cell waterCell = new CellImpl(new Position(1, 1));
        waterCell.receiveShot();
        assertFalse(waterCell.repair(), "Cannot repair a water cell");

        // 2. Successful repair
        cell.receiveShot(); // Health becomes 1
        assertTrue(cell.isHit(), "Cell should be marked as hit");
        assertTrue(cell.repair(), "Should successfully repair a damaged ship cell");
        assertFalse(cell.isHit(), "Cell hit status should be cleared after repair");

        // 3. Cannot repair a sunk ship
        cell.receiveShot(); // Health becomes 1
        ship.hit(); // Health becomes 0 (SUNK)
        assertFalse(cell.repair(), "Cannot repair a cell if the ship is completely sunk");
    }

    @Test
    void testScanResult() {
        // Verify we can store and retrieve scan results
        cell.setScanResult(true);
        assertTrue(cell.getScanResult().isPresent(), "Scan result should be present");
        assertTrue(cell.getScanResult().get(), "Scan result should be true");

        cell.setScanResult(false);
        assertFalse(cell.getScanResult().get(), "Scan result should be false");
    }
}
