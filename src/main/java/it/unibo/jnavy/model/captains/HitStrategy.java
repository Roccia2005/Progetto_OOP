package it.unibo.jnavy.model.captains;

/**
 * Defines the strategy for executing a shot on the game grid.
 * This interface applies the Strategy Pattern to decouple the "shooter" (Captain)
 * from the "ballistics" (how the shot affects the grid).
 *
 * Different implementations can define unique firing patterns.
 */
public interface HitStrategy {

}
