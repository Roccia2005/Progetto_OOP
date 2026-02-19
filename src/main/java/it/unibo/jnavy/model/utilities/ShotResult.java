package it.unibo.jnavy.model.utilities;

import it.unibo.jnavy.model.ship.Ship;

import java.util.Optional;

/**
 * Represents the immutable outcome of a shot fired at the grid.
 * It encapsulates all necessary information about the attack:
 *   - The type of result (Hit, Miss, Sunk, etc.) via {@link HitType}
 *   - The exact position where the event occurred
 *   - The specific ship involved (if sunk)
 *
 * @param hitType The outcome of the shot.
 * @param position The position where the shot was fired.
 * @param sunkShip The ship that was sunk (if applicable).
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

    /**
     * Creates a result indicating a successful hit on a ship, but not sunk yet.
     *
     * @param position The position of the hit.
     * @return A ShotResult with only a HIT hit type.
     */
    public static ShotResult hit(final Position position) {
        return new ShotResult(HitType.HIT, position, Optional.empty());
    }

    /**
     * Creates a result indicating a ship has been sunk.
     *
     * @param position The position of the final hit.
     * @param ship The ship instance that was sunk.
     * @return A ShotResult with a SUNK hit type and the specific ship object.
     */
    public static ShotResult sunk(final Position position, final Ship ship) {
        return new ShotResult(HitType.SUNK, position, Optional.of(ship));
    }

    /**
     * Creates a result indicating a failed shot.
     *
     * @param position The target position.
     * @param hitType The specific failure type (e.g., INVALID, ALREADY_HIT).
     * @return A ShotResult with the specified failure type and position.
     */
    public static ShotResult failure(final Position position, final HitType hitType) {
        return new ShotResult(hitType, position, Optional.empty());
    }
}
