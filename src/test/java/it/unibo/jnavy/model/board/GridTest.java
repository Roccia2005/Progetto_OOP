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

/**
 * Test class for {@link GridImpl}.
 */
final class GridTest {

    private static final int SHIP_SIZE = 3;
    private Grid grid;

    @BeforeEach
    void setup() {
        this.grid = new GridImpl();
    }

    @Test
    void testValidPlacement() {
        final Ship ship = new ShipImpl(SHIP_SIZE);
        final Position startPos = new Position(0, 0);

        // Test positioning towards RIGHT (0.0 -> 0.1 -> 0.2)
        assertTrue(grid.isPlacementValid(ship, startPos, CardinalDirection.RIGHT),
            "Placement should be valid within bounds");

        grid.placeShip(ship, startPos, CardinalDirection.RIGHT);

        // I check that the cells are occupied
        assertTrue(grid.getCell(new Position(0, 0)).get().isOccupied());
        assertTrue(grid.getCell(new Position(0, 1)).get().isOccupied());
        assertTrue(grid.getCell(new Position(0, 2)).get().isOccupied());

        // I'm checking that a cell outside the ship is free.
        assertFalse(grid.getCell(new Position(0, 3)).get().isOccupied());
    }

    @Test
    void testOutOfBoundsPlacement() {
        final Ship ship = new ShipImpl(SHIP_SIZE);

        // 1. Test outboard to LEFT starting from (0,0)
        assertFalse(grid.isPlacementValid(ship, new Position(0, 0), CardinalDirection.LEFT),
            "Should not place ship out of bounds (LEFT)");

        // 2. Test outboard to HIGH starting from (0,0)
        assertFalse(grid.isPlacementValid(ship, new Position(0, 0), CardinalDirection.UP),
            "Should not place ship out of bounds (UP)");

        // 3. Test outboard to DOWN starting from (0,0)
        assertFalse(grid.isPlacementValid(ship, new Position(9, 0), CardinalDirection.DOWN),
            "Should not place ship out of bounds (DOWN)");

        // Make sure it throws an exception if I try to place anyway
        assertThrows(IllegalArgumentException.class, () ->
            grid.placeShip(ship, new Position(0, 0), CardinalDirection.UP));
    }

    @Test
    void testCollision() {
        final Ship ship1 = new ShipImpl(SHIP_SIZE);
        final Ship ship2 = new ShipImpl(SHIP_SIZE);

        // I place the first ship in (2,2) towards DOWN (2,2), (3,2), (4,2)
        grid.placeShip(ship1, new Position(2, 2), CardinalDirection.DOWN);

        // I try to place a ship that crosses in (3,2)
        // Departure (3,1) to RIGHT -> (3,1), (3,2)*COLLISION*, (3,3)
        assertFalse(grid.isPlacementValid(ship2, new Position(3, 1), CardinalDirection.RIGHT),
            "Should detect collision with existing ship");

        assertThrows(IllegalArgumentException.class, () ->
            grid.placeShip(ship2, new Position(3, 1), CardinalDirection.RIGHT));
    }

    @Test
    void testShootingMechanics() {
        final Ship ship = new ShipImpl(2);
        grid.placeShip(ship, new Position(5, 5), CardinalDirection.RIGHT);

        // 1. I shoot at the water (MISS)
        final ShotResult resultMiss = grid.receiveShot(new Position(0, 0));
        assertEquals(HitType.MISS, resultMiss.hitType());

        // 2. I hit the ship (HIT)
        final ShotResult resultHit = grid.receiveShot(new Position(5, 5));
        assertEquals(HitType.HIT, resultHit.hitType());

        // 3. I hit the same spot
        final ShotResult resultInvalid = grid.receiveShot(new Position(5, 5));
        assertEquals(HitType.INVALID, resultInvalid.hitType(), "Shooting an already hit cell should return INVALID");

        // 4. I'm sinking the ship (SUNK)
        final ShotResult resultSunk = grid.receiveShot(new Position(5, 6));
        assertEquals(HitType.SUNK, resultSunk.hitType());

        // Check that the result contains the sunken ship
        assertTrue(resultSunk.sunkShip().isPresent());
        assertEquals(ship, resultSunk.sunkShip().get());
    }

    @Test
    void testRepair() {
        final Ship ship = new ShipImpl(3);
        final Position pos = new Position(1, 1);
        grid.placeShip(ship, pos, CardinalDirection.RIGHT);

        // I damage the ship
        grid.receiveShot(pos);
        final int healthAfterHit = ship.getHealth();

        // Repair
        final boolean repaired = grid.repair(pos);

        assertTrue(repaired, "Repair should return true on damaged ship cell");
        assertEquals(healthAfterHit + 1, ship.getHealth(), "Ship health should increase by 1");
    }

    @Test
    void testIsDefeated() {
        final Ship ship = new ShipImpl(2);
        grid.placeShip(ship, new Position(0, 0), CardinalDirection.RIGHT);

        assertFalse(grid.isDefeated(), "Grid should not be defeated initially");

        // Sink the only ship
        grid.receiveShot(new Position(0, 0));
        grid.receiveShot(new Position(0, 1));

        assertTrue(grid.isDefeated(), "Grid should be defeated when all ships are sunk");
    }

    @Test
    void testRemoveShip() {
        final Ship ship = new ShipImpl(3);
        final Position pos = new Position(2, 2);
        grid.placeShip(ship, pos, CardinalDirection.RIGHT);

        // Check that the cell is occupied
        assertTrue(grid.getCell(pos).get().isOccupied());

        // Remove the ship
        grid.removeShip(ship);

        // Check that the cell is free again
        assertFalse(grid.getCell(pos).get().isOccupied(), "Cell should be empty after ship removal");
        assertFalse(grid.getFleet().getShips().contains(ship), "Ship should be removed from the fleet");
    }

    @Test
    void testTargetAndPositionValidity() {
        // Test isPositionValid
        assertTrue(grid.isPositionValid(new Position(0, 0)));
        assertTrue(grid.isPositionValid(new Position(9, 9)));
        assertFalse(grid.isPositionValid(new Position(-1, 0)));
        assertFalse(grid.isPositionValid(new Position(0, 10)));

        // Test isTargetValid
        final Position pos = new Position(5, 5);
        assertTrue(grid.isTargetValid(pos));

        // Shoot the cell, so it should no longer be a valid target
        grid.receiveShot(pos);
        assertFalse(grid.isTargetValid(pos), "Already hit cell should not be a valid target");
    }

    @Test
    void testAvailableAndOccupiedPositions() {
        final Ship ship = new ShipImpl(2);
        grid.placeShip(ship, new Position(0, 0), CardinalDirection.RIGHT);

        // Occupied positions (should be 2)
        assertEquals(2, grid.getOccupiedPositions().size());
        assertTrue(grid.getOccupiedPositions().contains(new Position(0, 0)));
        assertTrue(grid.getOccupiedPositions().contains(new Position(0, 1)));

        // Available targets at the beginning are 100
        assertEquals(100, grid.getAvailableTargets().size());

        // After a hit at (0,0), available targets drop to 99
        grid.receiveShot(new Position(0, 0));
        assertEquals(99, grid.getAvailableTargets().size());
        assertFalse(grid.getAvailableTargets().contains(new Position(0, 0)));
    }
}
