package it.unibo.jnavy.controller;

import it.unibo.jnavy.model.Bot;
import it.unibo.jnavy.model.Human;
import it.unibo.jnavy.model.Player;
import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Controller responsible for the setup phase of the game.
 * It manages the placement of ships for both the human player (manual or random)
 * and the bot player (random).
 */
public interface SetupController {

    /**
     * Attempts to place the current ship (the one at the top of the list) 
     * at the specified position.
     * If a temporary ship was already placed (but not confirmed), it will be removed 
     * before placing the new one.
     *
     * @param pos The position of the ship's head.
     * @param dir The direction of the ship.
     * @return true if the placement is valid and successful, false otherwise.
     */
    boolean setShip(Position pos, CardinalDirection dir);

    /**
     * Confirms the position of the currently placed ship.
     *
     * @throws IllegalStateException if no ship is currently placed/selected to be confirmed.
     */
    void nextShip();

    /**
     * Randomly places the remaining ships for the human player.
     *
     * If the user was in the middle of placing a ship manually (unconfirmed),
     * that ship is removed and placed randomly along with the others.
     */
    void randomizeHumanShips();

    /**
     * Returns the size of the next ship to be placed.
     * Useful for the View to render the correct ship preview.
     *
     * @return int size, or 0 if there are no more ships to place.
     */
    int getNextShipSize();

    /**
     * Checks if the setup phase is finished.
     *
     * @return true if the human fleet is complete and valid.
     */
    boolean isSetupFinished();

//    /**
//     * Gets the initialized Human player.
//     *
//     * @return the Human player instance.
//     */
//    Player getHumanPlayer();
//
//    /**
//     * Gets the initialized Bot player.
//     *
//     * @return the Bot player instance.
//     */
//    Player getBotPlayer();

    CellState getCellState(Position pos);
}