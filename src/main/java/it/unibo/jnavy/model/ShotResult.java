package it.unibo.jnavy.model;

/**
 * Represents the immutable outcome of a shot fired at the grid.
 * It encapsulates all necessary information about the attack:
 *   - The type of result (Hit, Miss, Sunk, etc.) via {@link HitType}
 *   - The exact position where the event occurred
 *   - The specific ship involved (if sunk)
 */
public record ShotResult() {
}
