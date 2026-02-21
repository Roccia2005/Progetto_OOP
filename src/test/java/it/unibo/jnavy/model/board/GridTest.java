package it.unibo.jnavy.model.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.grid.GridImpl;
import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.ship.ShipImpl;
import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.HitType;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.ShotResult;

final class GridTest {

    private static final int SHIP_SIZE_2 = 2;
    private static final int SHIP_SIZE_3 = 3;

    private static final int COORD_MINUS_ONE = -1;
    private static final int COORD_ZERO = 0;
    private static final int COORD_ONE = 1;
    private static final int COORD_TWO = 2;
    private static final int COORD_THREE = 3;
    private static final int COORD_FIVE = 5;
    private static final int COORD_SIX = 6;
    private static final int COORD_NINE = 9;
    private static final int COORD_TEN = 10;

    private static final int TOTAL_CELLS = 100;
    private static final int TARGETS_MINUS_ONE = 99;
    private static final int EXPECTED_OCCUPIED = 2;

    private Grid grid;

    @BeforeEach
    void setUp() {
        this.grid = new GridImpl();
    }

    @Test
    void testValidPlacement() {
        final Ship ship = new ShipImpl(SHIP_SIZE_3);
        final Position startPos = new Position(COORD_ZERO, COORD_ZERO);

        assertTrue(grid.isPlacementValid(ship, startPos, CardinalDirection.RIGHT));

        grid.placeShip(ship, startPos, CardinalDirection.RIGHT);

        assertTrue(grid.getCell(new Position(COORD_ZERO, COORD_ZERO)).get().isOccupied());
        assertTrue(grid.getCell(new Position(COORD_ZERO, COORD_ONE)).get().isOccupied());
        assertTrue(grid.getCell(new Position(COORD_ZERO, COORD_TWO)).get().isOccupied());

        assertFalse(grid.getCell(new Position(COORD_ZERO, COORD_THREE)).get().isOccupied());
    }

    @Test
    void testOutOfBoundsPlacement() {
        final Ship ship = new ShipImpl(SHIP_SIZE_3);

        assertFalse(grid.isPlacementValid(ship, new Position(COORD_ZERO, COORD_ZERO), CardinalDirection.LEFT));
        assertFalse(grid.isPlacementValid(ship, new Position(COORD_ZERO, COORD_ZERO), CardinalDirection.UP));
        assertFalse(grid.isPlacementValid(ship, new Position(COORD_NINE, COORD_ZERO), CardinalDirection.DOWN));

        assertThrows(IllegalArgumentException.class, () ->
                grid.placeShip(ship, new Position(COORD_ZERO, COORD_ZERO), CardinalDirection.UP));
    }

    @Test
    void testCollision() {
        final Ship ship1 = new ShipImpl(SHIP_SIZE_3);
        final Ship ship2 = new ShipImpl(SHIP_SIZE_3);

        grid.placeShip(ship1, new Position(COORD_TWO, COORD_TWO), CardinalDirection.DOWN);

        assertFalse(grid.isPlacementValid(ship2, new Position(COORD_THREE, COORD_ONE), CardinalDirection.RIGHT));

        assertThrows(IllegalArgumentException.class, () ->
                grid.placeShip(ship2, new Position(COORD_THREE, COORD_ONE), CardinalDirection.RIGHT));
    }

    @Test
    void testShootingMechanics() {
        final Ship ship = new ShipImpl(SHIP_SIZE_2);
        grid.placeShip(ship, new Position(COORD_FIVE, COORD_FIVE), CardinalDirection.RIGHT);

        final ShotResult resultMiss = grid.receiveShot(new Position(COORD_ZERO, COORD_ZERO));
        assertEquals(HitType.MISS, resultMiss.hitType());

        final ShotResult resultHit = grid.receiveShot(new Position(COORD_FIVE, COORD_FIVE));
        assertEquals(HitType.HIT, resultHit.hitType());

        final ShotResult resultInvalid = grid.receiveShot(new Position(COORD_FIVE, COORD_FIVE));
        assertEquals(HitType.INVALID, resultInvalid.hitType());

        final ShotResult resultSunk = grid.receiveShot(new Position(COORD_FIVE, COORD_SIX));
        assertEquals(HitType.SUNK, resultSunk.hitType());

        assertTrue(resultSunk.sunkShip().isPresent());
        assertEquals(ship, resultSunk.sunkShip().get());
    }

    @Test
    void testRepair() {
        final Ship ship = new ShipImpl(SHIP_SIZE_3);
        final Position pos = new Position(COORD_ONE, COORD_ONE);
        grid.placeShip(ship, pos, CardinalDirection.RIGHT);

        grid.receiveShot(pos);
        final int healthAfterHit = ship.getHealth();

        final boolean repaired = grid.repair(pos);

        assertTrue(repaired);
        assertEquals(healthAfterHit + 1, ship.getHealth());
    }

    @Test
    void testIsDefeated() {
        final Ship ship = new ShipImpl(SHIP_SIZE_2);
        grid.placeShip(ship, new Position(COORD_ZERO, COORD_ZERO), CardinalDirection.RIGHT);

        assertFalse(grid.isDefeated());

        grid.receiveShot(new Position(COORD_ZERO, COORD_ZERO));
        grid.receiveShot(new Position(COORD_ZERO, COORD_ONE));

        assertTrue(grid.isDefeated());
    }

    @Test
    void testRemoveShip() {
        final Ship ship = new ShipImpl(SHIP_SIZE_3);
        final Position pos = new Position(COORD_TWO, COORD_TWO);
        grid.placeShip(ship, pos, CardinalDirection.RIGHT);

        assertTrue(grid.getCell(pos).get().isOccupied());

        grid.removeShip(ship);

        assertFalse(grid.getCell(pos).get().isOccupied());
        assertFalse(grid.getFleet().getShips().contains(ship));
    }

    @Test
    void testTargetAndPositionValidity() {
        assertTrue(grid.isPositionValid(new Position(COORD_ZERO, COORD_ZERO)));
        assertTrue(grid.isPositionValid(new Position(COORD_NINE, COORD_NINE)));
        assertFalse(grid.isPositionValid(new Position(COORD_MINUS_ONE, COORD_ZERO)));
        assertFalse(grid.isPositionValid(new Position(COORD_ZERO, COORD_TEN)));

        final Position pos = new Position(COORD_FIVE, COORD_FIVE);
        assertTrue(grid.isTargetValid(pos));

        grid.receiveShot(pos);
        assertFalse(grid.isTargetValid(pos));
    }

    @Test
    void testAvailableAndOccupiedPositions() {
        final Ship ship = new ShipImpl(SHIP_SIZE_2);
        grid.placeShip(ship, new Position(COORD_ZERO, COORD_ZERO), CardinalDirection.RIGHT);

        assertEquals(EXPECTED_OCCUPIED, grid.getOccupiedPositions().size());
        assertTrue(grid.getOccupiedPositions().contains(new Position(COORD_ZERO, COORD_ZERO)));
        assertTrue(grid.getOccupiedPositions().contains(new Position(COORD_ZERO, COORD_ONE)));

        assertEquals(TOTAL_CELLS, grid.getAvailableTargets().size());

        grid.receiveShot(new Position(COORD_ZERO, COORD_ZERO));
        assertEquals(TARGETS_MINUS_ONE, grid.getAvailableTargets().size());
        assertFalse(grid.getAvailableTargets().contains(new Position(COORD_ZERO, COORD_ZERO)));
    }
}
