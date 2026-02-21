package it.unibo.jnavy.model.player;

import java.io.Serial;
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
     * Serial version ID for backward compatibility in serialization.
     */
    @Serial
    long serialVersionUID = 1L;

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
    List<ShotResult> createShot(Position target, Grid grid);

    /**
     * {@inheritDoc}
     * Default implementation does nothing.
     */
    @Override
    default void processTurnEnd() {
    }

    /**
     * Attempts to use a special ability.
     * @param target The target {@link Position} for the ability.
     * @param grid The {@link Grid} on which to apply the ability.
     * @return true if the ability was successfully activated, false otherwise.
     */
    default boolean useAbility(final Position target, final Grid grid) {
        return false;
    }

    /**
     * Gets the maximum cooldown of the player's ability.
     * @return the total cooldown turns.
     */
    default int getAbilityCooldown() {
        return 0;
    }

    /**
     * Gets the current remaining turns for the ability cooldown.
     * @return the current cooldown progress.
     */
    default int getCurrentAbilityCooldown() {
        return 0;
    }

    /**
     * Determines if the ability is designed to be used on the opponent's grid.
     * @return true if it targets the enemy grid, false otherwise.
     */
    default boolean abilityTargetsEnemyGrid() {
        return false;
    }

    /**
     * Determines if using the special ability ends the player's current turn.
     * @return true if the turn is consumed, false otherwise.
     */
    default boolean doesAbilityConsumeTurn() {
        return false;
    }

    /**
     * Asks the player to autonomously generate a move.
     * @param enemyGrid The opponent's {@link Grid} used for targeting logic.
     * @return an {@link Optional} containing the {@link Position} if the player is a Bot,
     * otherwise {@link Optional#empty()}.
     */
    default Optional<Position> generateTarget(final Grid enemyGrid) {
        return Optional.empty();
    }

    /**
     * Sends feedback to the player about the result of the last shot.
     * @param target the {@link Position} that was attacked.
     * @param result the {@link HitType} outcome of the attack.
     */
    default void receiveFeedback(final Position target, final HitType result) { }

    /**
     * Retrieves the identification name of the player's profile.
     * @return the name of the Captain (Human) or the Difficulty (Bot).
     */
    String getProfileName();
}
