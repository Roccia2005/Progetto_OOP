package it.unibo.jnavy.model;

import java.util.List;

import it.unibo.jnavy.model.captains.Captain;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.grid.GridImpl;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherManagerImpl;

/**
 * Represents a human player in the game.
 *
 * The Human player controls a {@link Grid}, possesses a specific {@link Captain},
 * and participates in the turn-based mechanics.
 */
public class Human implements Player {

    private Captain captain;
    private Grid grid;

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

    @Override
    public Grid getGrid() {
        return this.grid;
    }

    @Override
    public List<ShotResult> createShot(Position target, Grid grid) {
        return List.of(WeatherManagerImpl.getInstance().applyWeatherEffects(target, grid));
    }

    @Override
    public void processTurnEnd() {
        this.captain.processTurnEnd();
    }

    /**
     * Attempts to activate the Captain's special ability.
     * 
     * This method acts as a bridge between the User Interface and the Captain's internal logic.
     *
     * @param target The target {@link Position} for the ability.
     * @param grid The {@link Grid} on which to apply the ability (usually the enemy's).
     * @return true if the ability was successfully used (cooldown reset),
     * false if it was not ready or the target was invalid.
     */
    public boolean useAbility(Position target, Grid grid) {
        return this.captain.useAbility(grid, target);
    }

    /**
     * @return the captain of the human
     */
    public Captain getCaptain() {
        return this.captain;
    }
}