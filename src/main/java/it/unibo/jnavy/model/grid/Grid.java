package it.unibo.jnavy.model.grid;

import java.util.List;
import java.util.Optional;

import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.fleet.Fleet;
import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.ShotResult;

/**
 * Represents the game board (10x10).
 * Manages the placement of ships and the processing of shots.
 */
public interface Grid {

    /**
     * Places a ship on the grid at the specified position and direction.
     * @param ship the ship to place.
     * @param startPos the starting position (bow of the ship).
     * @param dir the orientation of the ship (HORIZONTAL or VERTICAL).
     * @throws IllegalArgumentException if the placement is invalid (out of bounds or collision).
     */
    void placeShip(Ship ship, Position startPos, CardinalDirection dir);

    /**
     * Validates if a ship can be placed at the given coordinates.
     * Checks for boundary limits and collisions with existing ships.
     * @param ship the ship to check.
     * @param startPos the starting position.
     * @param dir the orientation.
     * @return true if the placement is valid, false otherwise.
     */
    boolean isPlacementValid(Ship ship, Position startPos, CardinalDirection dir);

    /**
     * Processes a shot fired at the given position.
     * @param p the target position.
     * @return the result of the shot (e.g., HIT, MISS, SUNK).
     */
    ShotResult receiveShot(Position p);

    /**
     * Checks if the entire fleet on this grid has been defeated.
     * @return true if all ships are sunk.
     */
    boolean isDefeated();

    /**
     * Attempts to repair a ship part at the specified position.
     * Use this method to revert the status of a damaged cell.
     *
     * @param p the target position on the grid.
     * @return true if a ship was successfully repaired at the given position,
     * false otherwise.
     */
    boolean repair(Position p);

    /**
     * Retrieves the Cell object at the specified coordinates safely.
     * @param p the coordinates of the cell to retrieve.
     * @return an {@link Optional} containing the {@link Cell} if the position is within grid bounds,
     * or {@link Optional#empty()} if the position is invalid.
     */
    Optional<Cell> getCell(Position p);

    /**
     * @return The size of the grid.
     */
    int getSize();

    /**
     * @return The fleet of the grid.
     */
    Fleet getFleet();

    List<Position> getAvailableTargets();

    boolean isTargetValid(Position target);

    /**
     * @param p position to verify
     * @return true if the position is within grid's bounds
     */
    boolean isPositionValid(Position p);

    /**
     * Removes a specific ship from the grid.
     * @param ship the ship to remove.
    */
    void removeShip(Ship ship);

    /**
     * @return the positions occupied by the fleet.
     */
    List<Position> getOccupiedPositions();
}
