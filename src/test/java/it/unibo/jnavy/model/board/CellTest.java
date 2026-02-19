package it.unibo.jnavy.model.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.cell.CellImpl;
import it.unibo.jnavy.model.ship.ShipImpl;
import it.unibo.jnavy.model.utilities.HitType;
import it.unibo.jnavy.model.utilities.Position;

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
        assertThrows(IllegalStateException.class, () -> cell.receiveShot(),
            "Shooting again on a hit ship segment must throw exception");
    }
}