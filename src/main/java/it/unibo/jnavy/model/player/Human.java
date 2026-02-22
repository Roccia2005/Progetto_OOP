package it.unibo.jnavy.model.player;

import java.util.List;

import it.unibo.jnavy.model.captains.Captain;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.grid.GridImpl;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.ShotResult;
import it.unibo.jnavy.model.weather.WeatherManagerImpl;

/**
 * Represents a human player in the game.
 * The Human player controls a {@link Grid}, possesses a specific {@link Captain},
 * and participates in the turn-based mechanics.
 */
public final class Human implements Player {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final Captain captain;
    private final Grid grid;

    /**
     * Constructs a new Human player with the selected captain.
     * Initializes the player's grid (empty) and assigns the chosen captain logic.
     *
     * @param captain The {@link Captain} chosen by the player.
     */
    public Human(final Captain captain) {
        this.grid = new GridImpl();
        this.captain = captain;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Grid getGrid() {
        return this.grid;
    }

    /**
     * {@inheritDoc}
     * Applies weather effects to the shot before returning the result.
     */
    @Override
    public List<ShotResult> createShot(final Position target, final Grid targetGrid) {
        return List.of(WeatherManagerImpl.getInstance().applyWeatherEffects(target, targetGrid));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTurnEnd() {
        this.captain.processTurnEnd();
    }

    /**
     * Attempts to activate the Captain's special ability.
     * This method acts as a bridge between the User Interface and the Captain's internal logic.
     *
     * @param target The target {@link Position} for the ability.
     * @param targetGrid The {@link Grid} on which to apply the ability (usually the enemy's).
     * @return true if the ability was successfully used (cooldown reset), false if it was not ready or the target was invalid.
     */
    @Override
    public boolean useAbility(final Position target, final Grid targetGrid) {
        return this.captain.useAbility(targetGrid, target);
    }

    /**
     * @return the cooldown value of the Captain's special ability.
     */
    @Override
    public int getAbilityCooldown() {
        return this.captain.getCooldown();
    }

    /**
     * * @return the current progress of the ability's cooldown.
     */
    @Override
    public int getCurrentAbilityCooldown() {
        return this.captain.getCurrentCooldown();
    }

    /**
     * Checks if the Captain's ability targets the enemy grid.
     *
     * @return true if the ability targets the opponent, false otherwise.
     */
    @Override
    public boolean abilityTargetsEnemyGrid() {
        return this.captain.targetsEnemyGrid();
    }

    /**
     * Checks if using the Captain's ability consumes the player's turn.
     *
     * @return true if using the ability ends the turn, false if the player can still shoot after using it.
     */
    @Override
    public boolean doesAbilityConsumeTurn() {
        return this.captain.doesAbilityConsumeTurn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfileName() {
        return this.captain.getName();
    }
}
