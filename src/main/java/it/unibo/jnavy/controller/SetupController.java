package it.unibo.jnavy.controller;

import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.Position;

public interface SetupController {
    /**
     * Attempts to place the next available ship in the human player's fleet.
     * @param pos The position of the ship's head.
     * @param dir The direction of the ship.
     * @return true if placement is successful, false if invalid (e.g., out of bounds, collision).
    */
    boolean placeCurrentShip(Position pos, CardinalDirection dir);

    /**
     * Returns the size of the next ship to be placed (to inform the View).
     * @return int size, or 0 if there are no more ships.
    */
    int getNextShipSize();

    /**
     * Automatic placement method for Human.
    */
    void randomizeHumanShips();
    
} 