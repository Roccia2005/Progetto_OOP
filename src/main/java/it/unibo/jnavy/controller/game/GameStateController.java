package it.unibo.jnavy.controller.game;

import it.unibo.jnavy.controller.utilities.CellCondition;
import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.player.Player;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherCondition;
import it.unibo.jnavy.model.weather.WeatherManager;

public class GameStateController {
    private final Player human;
    private final Player bot;
    private final WeatherManager weather;

    public GameStateController(final Player human, final Player bot, final WeatherManager weather) {
        this.human = human;
        this.bot = bot;
        this.weather = weather;
    }

    public int getGridSize() {
        return this.human.getGrid().getSize();
    }

    public int getCaptainCooldown() {
        return this.human.getAbilityCooldown();
    }

    public int getCurrentCaptainCooldown() {
        return this.human.getCurrentAbilityCooldown();
    }

    public String getBotDifficulty() {
        return this.bot.getProfileName();
    }

    public String getPlayerCaptainName() {
        return this.human.getProfileName();
    }

    public boolean captainAbilityTargetsEnemyGrid() {
        return this.human.abilityTargetsEnemyGrid();
    }

    public WeatherCondition getWeatherCondition() {
        return this.weather.getCurrentWeather();
    }

    public CellCondition getHumanCellState(Position p) {
        return this.human.getGrid().getCell(p)
                   .map(cell -> mapCellToCondition(cell, false))
                   .orElse(CellCondition.WATER);
    }

    public CellCondition getBotCellState(Position p) {
        return this.bot.getGrid().getCell(p)
                   .map(cell -> mapCellToCondition(cell, true))
                   .orElse(CellCondition.FOG);
    }

    private CellCondition mapCellToCondition(Cell cell, boolean isEnemyGrid) {
        if (cell.isHit()) {
            if (cell.isOccupied()) {
                boolean isSunk = cell.getShip().isPresent() && cell.getShip().get().isSunk();
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
