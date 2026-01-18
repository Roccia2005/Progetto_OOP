package it.unibo.jnavy.model.shots;

import it.unibo.jnavy.model.ShotResult;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

import java.util.List;

/**
 * Defines the strategy for executing a shot on the game grid.
 * This interface applies the Strategy Pattern to decouple the "shooter" (Captain)
 * from the "ballistics" (how the shot affects the grid).
 *
 * Different implementations can define unique firing patterns.
 */
public interface HitStrategy {

    /**
     * Executes the shot logic on the provided grid.
     *
     * @param target The central coordinate aimed by the player.
     * @param grid The grid where the shot effects are applied.
     * @return A list of {@link ShotResult}, representing the outcome for each cell affected.
     * (e.g., a standard shot returns a list of size 1, an area shot returns multiple results).
     */
    List<ShotResult> execute(Position target, Grid grid);
}
