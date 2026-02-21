package it.unibo.jnavy.model.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.cell.CellImpl;
import it.unibo.jnavy.model.ship.ShipImpl;
import it.unibo.jnavy.model.utilities.HitType;
import it.unibo.jnavy.model.utilities.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Test class for {@link CellImpl}.
 * It verifies the correct behavior of cell status updates and strict shooting rules.
 */
class CellTest {

    private static final int POS_ZERO = 0;
    private static final int POS_ONE = 1;
    private static final int SHIP_SIZE_2 = 2;
    private static final int SHIP_SIZE_3 = 3;

    private Cell cell;

    @BeforeEach
    void setUp() {
        this.cell = new CellImpl(new Position(POS_ZERO, POS_ZERO));
    }

    @Test
    void testDoubleShotStrict() {
        assertEquals(HitType.MISS, cell.receiveShot(), "First shot on empty cell should be MISS");
        assertTrue(cell.isHit(), "Cell should be marked as hit after receiving a shot");

        assertThrows(IllegalStateException.class, () -> {
            cell.receiveShot();
        }, "Shooting on an already hit cell must throw an IllegalStateException");
    }

    @Test
    void testHitOnShip() {
        cell.setShip(new ShipImpl(SHIP_SIZE_3));

        assertEquals(HitType.HIT, cell.receiveShot(), "Shot on occupied cell should return HIT");
        assertTrue(cell.isHit(), "Cell should be marked as hit");

        assertThrows(IllegalStateException.class, cell::receiveShot,
                "Shooting again on a hit ship segment must throw exception");
    }

    @Test
    void testInitialStateAndGetters() {
        assertEquals(new Position(POS_ZERO, POS_ZERO), cell.getPosition(), "Position should match the one set in constructor");
        assertFalse(cell.isOccupied(), "A new cell should not be occupied");
        assertFalse(cell.isHit(), "A new cell should not be hit");
        assertTrue(cell.getShip().isEmpty(), "A new cell should not contain a ship");
        assertTrue(cell.getScanResult().isEmpty(), "A new cell should have no scan result");
        assertFalse(cell.isDetectable(), "A cell without a ship should not be detectable");
    }

    @Test
    void testShipPlacementAndDetectability() {
        final ShipImpl ship = new ShipImpl(SHIP_SIZE_3);
        cell.setShip(ship);

        assertTrue(cell.isOccupied(), "Cell should be occupied after setting a ship");
        assertTrue(cell.getShip().isPresent(), "Cell should return the placed ship");
        assertEquals(ship, cell.getShip().get(), "The ship returned should be the one placed");
        assertTrue(cell.isDetectable(), "A cell with an intact ship should be detectable");
    }

    @Test
    void testSunkShip() {
        final ShipImpl ship = new ShipImpl(SHIP_SIZE_2);
        final Cell cell2 = new CellImpl(new Position(POS_ZERO, POS_ONE));

        cell.setShip(ship);
        cell2.setShip(ship);

        assertEquals(HitType.HIT, cell.receiveShot(), "First hit should return HIT");
        assertFalse(cell.isDetectable(), "A hit cell should no longer be detectable");

        assertEquals(HitType.SUNK, cell2.receiveShot(), "Second hit on a size 2 ship should return SUNK");
        assertFalse(cell2.isDetectable(), "A sunk cell should not be detectable");
    }

    @Test
    void testRepairLogic() {
        final ShipImpl ship = new ShipImpl(SHIP_SIZE_2);
        cell.setShip(ship);

        final Cell waterCell = new CellImpl(new Position(POS_ONE, POS_ONE));
        waterCell.receiveShot();
        assertFalse(waterCell.repair(), "Cannot repair a water cell");

        cell.receiveShot();
        assertTrue(cell.isHit(), "Cell should be marked as hit");
        assertTrue(cell.repair(), "Should successfully repair a damaged ship cell");
        assertFalse(cell.isHit(), "Cell hit status should be cleared after repair");

        cell.receiveShot();
        ship.hit();
        assertFalse(cell.repair(), "Cannot repair a cell if the ship is completely sunk");
    }

    @Test
    void testScanResult() {
        cell.setScanResult(true);
        assertTrue(cell.getScanResult().isPresent(), "Scan result should be present");
        assertTrue(cell.getScanResult().get(), "Scan result should be true");

        cell.setScanResult(false);
        assertFalse(cell.getScanResult().get(), "Scan result should be false");
    }
}
