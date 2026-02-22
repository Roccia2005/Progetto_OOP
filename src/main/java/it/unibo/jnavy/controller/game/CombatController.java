package it.unibo.jnavy.controller.game;

import java.util.Optional;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.player.Player;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.ShotResult;
import it.unibo.jnavy.model.weather.WeatherManager;

/**
 * Controller responsible for managing combat actions, including firing shots
 * and using special abilities for both the human player and the bot player.
 * It coordinates interaction between the players, the weather system, and turn management.
 */
public class CombatController {
    private final Player human;
    private final Player bot;
    private final WeatherManager weather;
    private final TurnController turnController;

    /**
     * Constructs a new CombatController.
     *
     * @param human the human player instance.
     * @param bot the bot player instance.
     * @param weather the manager for weather-related effects.
     * @param turnController the controller that manages turn transitions.
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "The controller needs direct references to Model objects to read their updated state."
    )
    public CombatController(final Player human, final Player bot,
                            final WeatherManager weather, final TurnController turnController) {
        this.human = human;
        this.bot = bot;
        this.weather = weather;
        this.turnController = turnController;
    }

    /**
     * Processes a standard shot fired by the human player.
     * If it is the human player's turn, the shot is executed against the bot's grid,
     * and the turn is concluded.
     *
     * @param p the target position on the enemy grid.
     */
    public void processShot(final Position p) {
        if (!this.turnController.isHumanTurn()) {
            return;
        }

        this.human.createShot(p, this.bot.getGrid());
        this.turnController.endTurn();
    }

    /**
     * Attempts to activate the human player's captain ability at a specific position.
     * The target grid (own or enemy) is determined by the specific captain type.
     * If the ability is used successfully and is configured to consume a turn,
     * the turn is ended.
     *
     * @param p the target coordinates for the ability.
     * @return true if the ability was successfully used, false otherwise.
     */
    public boolean processAbility(final Position p) {
        if (!this.turnController.isHumanTurn()) {
            return false;
        }

        final Grid targetGrid = this.human.abilityTargetsEnemyGrid() ? this.bot.getGrid() : this.human.getGrid();

        if (this.human.useAbility(p, targetGrid)) {
            if (this.human.doesAbilityConsumeTurn()) {
                this.turnController.endTurn();
            }
            return true;
        }
        return false;
    }

    /**
     * Executes the logic for the bot's turn.
     * It generates a target, applies weather effects (which may alter the target),
     * updates the bot's internal state with the result, and ends the turn.
     *
     * @return the final position targeted by the bot after weather effects,
     *         or null if the game is over, no target was generated, or it is the human's turn.
     */
    public Position playBotTurn() {
        if (this.turnController.isGameOver() || this.turnController.isHumanTurn()) {
            return null;
        }

        final Optional<Position> optionalTarget = this.bot.generateTarget(this.human.getGrid());
        if (optionalTarget.isPresent()) {
            final Position target = optionalTarget.get();
            final ShotResult result = this.weather.applyWeatherEffects(target, this.human.getGrid());
            this.bot.receiveFeedback(result.position(), result.hitType());
            this.turnController.endTurn();
            return result.position();
        }

        this.turnController.endTurn();
        return null;
    }
}
