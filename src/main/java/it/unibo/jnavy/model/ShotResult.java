package it.unibo.jnavy.model;

import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.utilities.Position;

import java.util.Optional;

/**
 * Represents the immutable outcome of a shot fired at the grid.
 * It encapsulates all necessary information about the attack:
 *   - The type of result (Hit, Miss, Sunk, etc.) via {@link HitType}
 *   - The exact position where the event occurred
 *   - The specific ship involved (if sunk)
 */
public record ShotResult(HitType hitType, Position position, Optional<Ship> sunkShip) {

    /**
     * Creates a result indicating a missed shot.
     *
     * @param position The position where the shot was fired.
     * @return A ShotResult with a MISS hit type.
     */
    public static ShotResult miss(final Position position) {
        return new ShotResult(HitType.MISS, position, Optional.empty());
    }
}
