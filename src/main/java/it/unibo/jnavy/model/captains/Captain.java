package it.unibo.jnavy.model.captains;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.observer.TurnObserver;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Represents a Captain in the game.
 * Each captain possesses a unique special ability that can be used on the grid
 * after a certain cooldown period.
 */
public interface Captain extends TurnObserver{

    /**
     * Checks if the captain's special ability is ready to be used.
     * The ability is available only if the cooldown counter has reached zero.
     *
     * @return true if the ability is recharged and ready, false otherwise.
     */
    boolean isAbilityRecharged();

    /**
     * Attempts to activate the captain's special ability at the specified position.
     * If the ability is ready and the target is valid, the effect is applied to the grid
     * and the cooldown is reset.
     *
     * @param grid the game grid where the ability will be applied.
     * @param p the target position for the ability.
     * @return true if the ability was successfully executed, false otherwise.
     */
    boolean useAbility(Grid grid, Position p);

    /**
     * Checks if the use of this captain's special ability istantly end the player's turn.
     * If an ability consumes the turn, the player will not be able to perform a standard
     * shot or other actions after using it.
     *
     * @return true if using the ability ends the turn, false otherwise.
     */
    boolean doesAbilityConsumeTurn();

    /**
     * Determines the target grid for this captain's special ability.
     *
     * @return true if the ability is applied to the enemy's grid,
     * false if it is applied to the player's own grid.
     */
    boolean targetsEnemyGrid();

    /**
     * @return the total cooldown duration required for the captain's special ability.
     */
    int getCooldown();

    /**
     * @return the current progress of the ability's cooldown.
     */
    int getCurrentCooldown();

    /**
     * Returns the name of the captain.
     * @return the simple class name representing the captain type.
     */
    String getName();
}