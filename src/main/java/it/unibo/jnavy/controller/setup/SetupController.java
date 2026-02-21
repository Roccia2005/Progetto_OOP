package it.unibo.jnavy.controller.setup;

import it.unibo.jnavy.controller.utilities.CellState;
import it.unibo.jnavy.model.player.Player;
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
     * If the user was in the middle of placing a ship manually (unconfirmed),
     * that ship is removed and placed randomly along with the others.
     */
    void randomizeHumanShips();

    /**
     * Returns the size of the next ship to be placed.
     * This is useful for the View to render the correct ship preview.
     *
     * @return the size of the next ship, or 0 if all ships have been placed.
     */
    int getNextShipSize();

    /**
     * Checks if the setup phase is finished.
     *
     * @return true if the human fleet is complete and valid.
     */
    boolean isSetupFinished();

    /**
     * Gets the initialized Human player.
     *
     * @return the Human player instance.
     */
    Player getHumanPlayer();

    /**
     * Gets the initialized Bot player.
     *
     * @return the Bot player instance.
     */
    Player getBotPlayer();

    /**
     * Retrieves the visual state of a specific cell during the setup phase.
     *
     * @param pos the position of the cell.
     * @return the {@link CellState} representing the cell's appearance and connections.
     */
    CellState getCellState(Position pos);

    /**
     * Removes all ships from the human player's grid and resets the placement list.
     */
    void clearFleet();
}
