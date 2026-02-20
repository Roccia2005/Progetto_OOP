package it.unibo.jnavy.controller.game;

import java.util.Optional;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.player.Player;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.ShotResult;
import it.unibo.jnavy.model.weather.WeatherManager;

public class CombatController {
    private final Player human;
    private final Player bot;
    private final WeatherManager weather;
    private final TurnController turnController;

    public CombatController(final Player human, final Player bot,
                            final WeatherManager weather, final TurnController turnController) {
        this.human = human;
        this.bot = bot;
        this.weather = weather;
        this.turnController = turnController;
    }

    public void processShot(final Position p) {
        if (!this.turnController.isHumanTurn()) { return; }

        this.human.createShot(p, this.bot.getGrid());
        this.turnController.endTurn();
    }

    public boolean processAbility(final Position p) {
        if (!this.turnController.isHumanTurn()) { return false; }

        final Grid targetGrid = this.human.abilityTargetsEnemyGrid() ? this.bot.getGrid() : this.human.getGrid();

        if (this.human.useAbility(p, targetGrid)) {
            if (this.human.doesAbilityConsumeTurn()) {
                this.turnController.endTurn();
            }
            return true;
        }
        return false;
    }

    public Position playBotTurn() {
        if (this.turnController.isGameOver()) { return null; }

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
