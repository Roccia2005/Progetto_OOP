package it.unibo.jnavy.model.ship;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a ship entity in the game.
 * It defines the basic behavior of a ship, such as taking damage and checking its status.
 */
public interface Ship extends Serializable {

    /**
     * Serial version ID for backward compatibility in serialization.
     */
    @Serial
    long serialVersionUID = 1L;

    /**
     * Registers a hit on the ship, decreasing its health.
     *
     * @return true if the ship is sunk after this hit.
     * @throws IllegalStateException if the ship is already sunk.
     */
    boolean hit();

    /**
     * Checks if the ship is destroyed.
     *
     * @return true if the ship's health is less than or equal to 0.
     */
    boolean isSunk();

    /**
     * Gets the original size of the ship.
     *
     * @return the size of the ship (e.g., length in cells).
     */
    int getSize();

    /**
     * Gets the current health of the ship.
     *
     * @return the remaining health points.
     */
    int getHealth();

    /**
     * Repairs the ship by increasing its health by 1, up to its maximum size.
     *
     * @return true if the ship was actually repaired.
     */
    boolean repair();
}
