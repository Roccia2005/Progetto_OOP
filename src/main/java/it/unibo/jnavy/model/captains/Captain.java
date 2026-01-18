package it.unibo.jnavy.model.captains;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Represents a Captain in the game.
 * Each captain possesses a unique special ability that can be used on the grid
 * after a certain cooldown period.
 */
public interface Captain {

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
     * @return true if the ability was successfully executed, false otherwise (e.g., on cooldown or invalid target).
     */
    boolean useAbility(Grid grid, Position p);
}