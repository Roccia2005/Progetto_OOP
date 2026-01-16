package it.unibo.jnavy.model.ship;

/**
 * Represents a ship entity in the game.
 * It defines the basic behavior of a ship, such as taking damage and checking its status.
 */
public interface Ship {

    /**
     * Registers a hit on the ship, decreasing its health.
     * @return true if the ship is sunk after this hit.
     */
    boolean hit();

    /**
     * Checks if the ship is destroyed.
     * @return true if the ship's health is less than or equal to 0.
     */
    boolean isSunk();

    /**
     * Gets the original size of the ship.
     * @return the size of the ship (e.g., length in cells).
     */
    int getSize();

    /**
     * Gets the current health of the ship.
     * @return the remaining health points.
     */
    int getHealth();
}