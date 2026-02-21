package it.unibo.jnavy.controller.game;

import it.unibo.jnavy.controller.utilities.CellCondition;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Interface representing the main controller for the game logic.
 * It manages the game flow, player turns, and interactions between the model and the view.
 */
public interface GameController {

    /**
     * Retrieves the size of the game grid.
     *
     * @return the grid dimension.
     */
    int getGridSize();

    /**
     * Retrieves the maximum cooldown duration for the player's captain ability.
     *
     * @return the total cooldown value.
     */
    int getCaptainCooldown();

    /**
     * Processes a shot fired by the current player at the specific position.
     *
     * @param p the target coordinates on the grid.
     */
    void processShot(Position p);

    /**
     * Attempts to activate the captain's special ability at the given position.
     *
     * @param p the target coordinates for the ability.
     * @return true if the ability was successfully triggered, false otherwise.
     */
    boolean processAbility(Position p);

    /**
     * Checks whether the game has reached a terminal state.
     *
     * @return true if the game is over, false otherwise.
     */
    boolean isGameOver();

    /**
     * Retrieves the current remaining cooldown for the player's captain ability.
     * A value of zero indicates that the ability is ready to be used.
     *
     * @return the number of turns remaining before the ability can be activated again.
     */
    int getCurrentCaptainCooldown();

    /**
     * Retrieves the condition (e.g., hit, miss, ship) of a cell in the human player's grid.
     *
     * @param p the position to check.
     * @return the current condition of the specified cell.
     */
    CellCondition getHumanCellState(Position p);

    /**
     * Retrieves the condition of a cell in the bot player's grid.
     *
     * @param p the position to check.
     * @return the current condition of the specified cell.
     */
    CellCondition getBotCellState(Position p);

    /**
     * Retrieves the name of the current weather condition affecting the game.
     *
     * @return the name of the weather condition as a String.
     */
    String getWeatherConditionName();

    /**
     * Checks if it is currently the human player's turn.
     *
     * @return true if the human player can move, false otherwise.
     */
    boolean isHumanTurn();

    /**
     * Executes the bot's logic for its turn.
     *
     * @return the position targeted by the bot.
     */
    Position playBotTurn();

    /**
     * Retrieves the difficulty level of the opponent bot.
     *
     * @return the bot difficulty.
     */
    String getBotDifficulty();

    /**
     * Retrieves the name of the captain chosen by the human player.
     *
     * @return the player's captain's name.
     */
    String getPlayerCaptainName();

    /**
     * Checks if the bot player has lost all its ships.
     *
     * @return true if the bot is defeated, false otherwise.
     */
    boolean isBotDefeated();

    /**
     * Determines whether the current captain's ability should be targeted
     * at the enemy grid or the player's own grid.
     *
     * @return true if it targets the enemy grid, false if it targets the player grid.
     */
    boolean captainAbilityTargetsEnemyGrid();

    /**
     * Saves the current game state to a persistent storage file.
     *
     * @return true if the save operation was successful, false otherwise.
     */
    boolean saveGame();
}
