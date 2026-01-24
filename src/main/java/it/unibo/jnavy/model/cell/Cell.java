package it.unibo.jnavy.model.cell;

import it.unibo.jnavy.model.HitType;
import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Represents a single cell on the game grid.
 * A cell can be empty (water) or occupied by a ship.
 */
public interface Cell {
    
    /**
     * Handles a shot fired at this cell.
     * Updates the cell status and damages the ship if present.
     * @return the result of the shot (MISS, HIT, SUNK, or ALREADY_HIT).
     */
    HitType receiveShot();

    /**
     * Places a ship reference in this cell.
     * @param ship the ship to place.
     */
    void setShip(Ship ship);

    /**
     * Retrieves the ship occupying this cell.
     * @return the ship reference, or null if the cell is water.
     */
    Ship getShip();

    /**
     * Checks if the cell is occupied by a ship.
     * @return true if a ship is present, false otherwise.
     */
    boolean isOccupied();

    /**
     * Retrieves the coordinates of this cell.
     * @return the position of the cell.
     */
    Position getPosition();

    /**
     * Set the content of the cell visible
     */
    void setVisible();

    /**
     * @return if the cell has been hit or no.
     */
    boolean isHit();

    /**
     * @return if the cell is visible
     */
    boolean isVisible();
}
