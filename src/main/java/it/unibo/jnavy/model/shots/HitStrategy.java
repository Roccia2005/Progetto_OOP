package it.unibo.jnavy.model.shots;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.ShotResult;

import java.util.List;

/**
 * Defines the strategy for executing a shot on the game grid.
 * This interface applies the Strategy Pattern to decouple the "shooter" (Captain or Bot)
 * from the "ballistics" (how the shot affects the grid).
 * Different implementations can define unique firing patterns.
 */
@FunctionalInterface
public interface HitStrategy {

    /**
     * Executes the shot logic on the provided grid.
     *
     * @param target The central coordinate aimed by the player.
     * @param grid The grid where the shot effects are applied.
     * @return A list of {@link ShotResult}, representing the outcome for each cell affected.
     */
    List<ShotResult> execute(Position target, Grid grid);
}
