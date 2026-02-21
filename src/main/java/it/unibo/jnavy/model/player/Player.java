package it.unibo.jnavy.model.player;

import java.util.List;
import java.util.Optional;

import it.unibo.jnavy.model.fleet.Fleet;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.observer.TurnObserver;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.ShotResult;
import it.unibo.jnavy.model.utilities.HitType;

/**
 * Defines the contract for a participant in the game.
 *
 * <p>
 * This interface abstracts the common behaviors of both human players and computer-controlled
 * opponents (Bots). It ensures that every player has a {@link Grid} containing their ships
 * and provides a mechanism to generate offensive moves (shots).
 */
public interface Player extends TurnObserver {

    /**
     * Retrieves the game grid associated with this player.
     *
     * <p>
     * The grid represents the player's board, containing their fleet and tracking
     * the state of each cell (e.g., hit, miss, empty). This is essential for
     * the game controller to apply shots and verify the state of the player's ships.
     *
     * @return The {@link Grid} owned by this player.
     */
    Grid getGrid();

    /**
     * Retrieves the fleet associated with this player.
     *
     * <p>
     * This is a default method that delegates the call to the player's Grid,
     * avoiding code duplication in Bot and Human classes.
     *
     * @return The {@link Fleet} owned by this player.
     */
    default Fleet getFleet() {
        return getGrid().getFleet();
    }

    /**
     * Creates a shot directed at a specific target position.
     *
     * <p>
     * The shot type may vary depending on the player (e.g., standard or area for Human, standard for Bot).
     * The returned object is intended to be processed by the weather system and potentially modified
     * if an atmospheric event is active.
     *
     * @param target The target {@link Position}.
     * @param grid The grid {@link Grid}.
     * @return The {@link ShotResult} representing the list of generated shot outcomes.
     */
    List<ShotResult> createShot(final Position target, final Grid grid);

    @Override
    default void processTurnEnd() {
    }

    default boolean useAbility(Position target, Grid grid) {
        return false;
    }

    default int getAbilityCooldown() {
        return 0;
    }

    default int getCurrentAbilityCooldown() {
        return 0;
    }

    default boolean abilityTargetsEnemyGrid() {
        return false;
    }

    default boolean doesAbilityConsumeTurn() {
        return false;
    }

    /**
     * Chiede al giocatore di generare autonomamente una mossa.
     * Utilizza Optional: se il giocatore è Umano, ritorna Optional.empty().
     */
    default Optional<Position> generateTarget(final Grid enemyGrid) {
        return Optional.empty();
    }

    /**
     * Invia un feedback al giocatore sul colpo appena effettuato.
     * Implementazione di default vuota.
     */
    default void receiveFeedback(final Position target, final HitType result) {  }

     /**
     * Restituisce il nome identificativo del "profilo" del giocatore.
     * (Es: Nome del Capitano per l'Umano, Difficoltà per il Bot).
     */
    String getProfileName();
}
