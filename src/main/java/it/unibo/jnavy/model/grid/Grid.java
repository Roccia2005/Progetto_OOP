package it.unibo.jnavy.model.grid;

import it.unibo.jnavy.model.HitType;
import it.unibo.jnavy.model.ShotResult;
import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.utilities.Direction;
import it.unibo.jnavy.model.utilities.Position;

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
    void placeShip(Ship ship, Position startPos, Direction dir);

    /**
     * Validates if a ship can be placed at the given coordinates.
     * Checks for boundary limits and collisions with existing ships.
     * @param ship the ship to check.
     * @param startPos the starting position.
     * @param dir the orientation.
     * @return true if the placement is valid, false otherwise.
     */
    boolean isPlacementValid(Ship ship, Position startPos, Direction dir);

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
} 
