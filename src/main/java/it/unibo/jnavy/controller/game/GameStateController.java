package it.unibo.jnavy.controller.game;

import it.unibo.jnavy.controller.utilities.CellCondition;
import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.player.Player;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherCondition;
import it.unibo.jnavy.model.weather.WeatherManager;

/**
 * Controller responsible for managing and exposing the current game state.
 * It acts as an adapter between the domain model and the view, mapping internal
 * states to display-ready conditions.
 */
public class GameStateController {
    private final Player human;
    private final Player bot;
    private final WeatherManager weather;

    /**
     * Constructs a new GameStateController with the specified players and weather manager.
     *
     * @param human the human player instance.
     * @param bot the bot player instance.
     * @param weather the manager handling weather conditions.
     */
    public GameStateController(final Player human, final Player bot, final WeatherManager weather) {
        this.human = human;
        this.bot = bot;
        this.weather = weather;
    }

    /**
     * Retrieves the size of the game grid.
     *
     * @return the number of cells along one side of the square grid.
     */
    public int getGridSize() {
        return this.human.getGrid().getSize();
    }

    /**
     * Retrieves the total cooldown period required for the captain's ability.
     *
     * @return the maximum cooldown value.
     */
    public int getCaptainCooldown() {
        return this.human.getAbilityCooldown();
    }

    /**
     * Retrieves the current remaining cooldown for the human player's captain ability.
     *
     * @return the current cooldown progress.
     */
    public int getCurrentCaptainCooldown() {
        return this.human.getCurrentAbilityCooldown();
    }

    /**
     * Retrieves the difficulty level or strategy name of the opponent bot.
     *
     * @return a string representing the bot's profile name.
     */
    public String getBotDifficulty() {
        return this.bot.getProfileName();
    }

    /**
     * Retrieves the name of the captain selected by the human player.
     *
     * @return the captain's name.
     */
    public String getPlayerCaptainName() {
        return this.human.getProfileName();
    }

    /**
     * Determines whether the human player's captain ability is intended to target
     * the opponent's grid.
     *
     * @return true if the ability targets the enemy grid, false if it targets the player's own grid.
     */
    public boolean captainAbilityTargetsEnemyGrid() {
        return this.human.abilityTargetsEnemyGrid();
    }

    /**
     * Retrieves the current weather condition affecting the game.
     *
     * @return he active {@link WeatherCondition}.
     */
    public WeatherCondition getWeatherCondition() {
        return this.weather.getCurrentWeather();
    }

    /**
     * Provides the visual condition of a specific cell on the human player's grid.
     *
     * @param p position of the cell to check.
     * @return the {@link CellCondition} representing the cell's state.
     */
    public CellCondition getHumanCellState(final Position p) {
        return this.human.getGrid().getCell(p)
                   .map(cell -> mapCellToCondition(cell, false))
                   .orElse(CellCondition.WATER);
    }

    /**
     * Provides the visual condition of a specific cell on the bot's grid,
     * respecting the fog of war and scan results.
     *
     * @param p position of the cell to check.
     * @return the {@link CellCondition} representing the cell's state as seen by the human.
     */
    public CellCondition getBotCellState(final Position p) {
        return this.bot.getGrid().getCell(p)
                   .map(cell -> mapCellToCondition(cell, true))
                   .orElse(CellCondition.FOG);
    }

    /**
     * Maps an internal {@link Cell} object to a displayable {@link CellCondition}.
     * This method handles the logic for hiding occupied cells on the enemy grid
     * unless they have been hit or scanned.
     *
     * @param cell the cell to map.
     * @param isEnemyGrid whether the cell belongs to the opponent's grid.
     * @return the mapped visual condition.
     */
    private CellCondition mapCellToCondition(final Cell cell, final boolean isEnemyGrid) {
        if (cell.isHit()) {
            if (cell.isOccupied()) {
                final boolean isSunk = cell.getShip().isPresent() && cell.getShip().get().isSunk();
                return isSunk ? CellCondition.SUNK_SHIP : CellCondition.HIT_SHIP;
            } else {
                return CellCondition.HIT_WATER;
            }
        }

        if (isEnemyGrid && cell.getScanResult().isPresent()) {
            return cell.getScanResult().get() ? CellCondition.REVEALED_SHIP : CellCondition.REVEALED_WATER;
        }

        if (!isEnemyGrid) {
            return cell.isOccupied() ? CellCondition.SHIP : CellCondition.WATER;
        }

        return CellCondition.FOG;
    }
}
