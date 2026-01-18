package it.unibo.jnavy.model;

import java.util.List;

import it.unibo.jnavy.model.shots.HitStrategy;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.ship.Ship;

public interface Player {
    /**
     * Returns the player's list of ships (fleet).
     *
     * @return The list of {@link Ship} owned by the player.
     */
    List<Ship> getShips();

    /**
     * Creates a shot directed at a specific target position.
     * <p>
     * The shot type may vary depending on the player (e.g., standard or area for Human, standard for Bot).
     * The returned object is intended to be processed by the weather system and potentially modified
     * if an atmospheric event is active.
     *
     * @param target The target {@link Position}.
     * @return The {@link HitStrategy} representing the generated shot.
     */
    HitStrategy createShot(Position target);
}
