package it.unibo.jnavy.model.cell;

import java.io.Serializable;
import java.util.Optional;

import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.utilities.HitType;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Represents a single cell on the game grid.
 * A cell can be empty (water) or occupied by a ship.
 */
public interface Cell extends Serializable {

    /**
     * Handles a shot fired at this cell.
     * Updates the cell status and damages the ship if present.
     *
     * @return the result of the shot (MISS, HIT, SUNK, or ALREADY_HIT).
     */
    HitType receiveShot();

    /**
     * Places a ship reference in this cell.
     *
     * @param ship the ship to place.
     */
    void setShip(Ship ship);

    /**
     * Retrieves the ship occupying this cell.
     *
     * @return the ship reference, or null if the cell is water.
     */
    Optional<Ship> getShip();

    /**
     * Checks if the cell is occupied by a ship.
     *
     * @return true if a ship is present, false otherwise.
     */
    boolean isOccupied();

    /**
     * Retrieves the coordinates of this cell.
     *
     * @return the position of the cell.
     */
    Position getPosition();

    /**
     * @return if the cell has been hit or no.
     */
    boolean isHit();

    /**
     * Resets the cell's hit status if the ship occupying it is not sunk.
     * This allows the cell to be targeted again or to be considered "repaired".
     *
     * @return true if the status was successfully reset, false if the ship is already sunk or no ship is present.
     */
    boolean repair();

    /**
     * Sets the result of a scan for this cell.
     * This represents whether a ship was detected in the area during a reconnaissance action,
     * without physically hitting the cell.
     *
     * @param shipFound true if a ship was detected in the scanned area, false otherwise.
     */
    void setScanResult(boolean shipFound);

    /**
     * Retrieves the result of the last sonar scan performed on this cell.
     *
     * @return an {@link Optional} containing true if a ship was found, 
     * false if only water was found, or an empty Optional if this cell has never been scanned.
     */
    Optional<Boolean> getScanResult();

    /**
     * Determines whether the cell contains an active and hidden entity 
     * that can be detected by sensors.
     * 
     * @return true if the cell represents an undiscovered cell, false otherwise.
     */
    boolean hisDetectable();
}
