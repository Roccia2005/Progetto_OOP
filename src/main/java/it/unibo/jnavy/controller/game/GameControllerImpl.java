package it.unibo.jnavy.controller.game;

import java.util.Optional;

import it.unibo.jnavy.controller.utilities.CellCondition;
import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.player.Bot;
import it.unibo.jnavy.model.player.Human;
import it.unibo.jnavy.model.player.Player;
import it.unibo.jnavy.model.serialization.GameState;
import it.unibo.jnavy.model.serialization.SaveManager;
import it.unibo.jnavy.model.serialization.SaveManagerImpl;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.ShotResult;
import it.unibo.jnavy.model.weather.WeatherCondition;
import it.unibo.jnavy.model.weather.WeatherManager;
import it.unibo.jnavy.model.weather.WeatherManagerImpl;

public class GameControllerImpl implements GameController {

    private final Player human;
    private final Player bot;
    private final WeatherManager weather;
    private Player currentPlayer;
    private int turnCounter = 0;

    public GameControllerImpl(final Player human, final Player bot) {
        this.human = human;
        this.bot = bot;
        this.currentPlayer = this.human;
        this.weather = WeatherManagerImpl.getInstance();
        ((WeatherManagerImpl) this.weather).reset();
    }

    public GameControllerImpl(GameState loadedState) {
        this.human = (Human) loadedState.getHuman();
        this.bot = (Bot) loadedState.getBot();
        this.turnCounter = loadedState.getTurnCounter();
        this.currentPlayer = loadedState.isHumanTurn() ? this.human : this.bot;
        this.weather = WeatherManagerImpl.getInstance();
    }

    @Override
    public int getGridSize() {
        return this.human.getGrid().getSize();
    }

    @Override
    public int getCaptainCooldown() {
        return this.human.getAbilityCooldown();
    }

    @Override
    public void processShot(Position p) {
        if (!isHumanTurn()) return;
        this.human.createShot(p, this.bot.getGrid());
        endTurn();
    }

    @Override
    public boolean processAbility(Position p) {
        if (!isHumanTurn()) return false;

        Grid targetGrid = this.human.abilityTargetsEnemyGrid() ? this.bot.getGrid() : this.human.getGrid();

        if (this.human.useAbility(p, targetGrid)) {
            if (this.human.doesAbilityConsumeTurn()) {
                endTurn();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isGameOver() {
        return this.human.getFleet().isDefeated() || this.bot.getFleet().isDefeated();
    }

    @Override
    public int getCurrentCaptainCooldown() {
        return this.human.getCurrentAbilityCooldown();
    }

    @Override
    public CellCondition getHumanCellState(Position p) {
        return this.human.getGrid().getCell(p)
                   .map(cell -> mapCellToCondition(cell, false))
                   .orElse(CellCondition.WATER);
    }

    @Override
    public CellCondition getBotCellState(Position p) {
        return this.bot.getGrid().getCell(p)
                   .map(cell -> mapCellToCondition(cell, true))
                   .orElse(CellCondition.FOG);
    }

    @Override
    public WeatherCondition getWeatherCondition() {
        return this.weather.getCurrentWeather();
    }

    private int endTurn() {
        this.turnCounter++;
        this.currentPlayer.processTurnEnd();
        this.weather.processTurnEnd();
        this.currentPlayer = (this.currentPlayer == this.human) ? this.bot : this.human;
        return this.turnCounter;
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

    @Override
    public boolean isHumanTurn() {
        return this.currentPlayer == this.human;
    }

    @Override
    public void playBotTurn() {
        if (isGameOver()) return;

        Optional<Position> optionalTarget = this.bot.generateTarget(this.human.getGrid());
        if (optionalTarget.isPresent()) {
            Position target = optionalTarget.get();
            ShotResult result = this.weather.applyWeatherEffects(target, this.human.getGrid());
            this.bot.receiveFeedback(result.position(), result.hitType());
        }
        endTurn();
    }

    @Override
    public String getBotDifficulty() {
        return this.bot.getProfileName();
    }


    @Override
    public String getPlayerCaptainName() {
        return this.human.getProfileName();
    }

    @Override
    public boolean isBotDefeated() {
        return this.bot.getFleet().isDefeated();
    }

    @Override
    public boolean captainAbilityTargetsEnemyGrid() {
        return this.human.abilityTargetsEnemyGrid();
    }

    @Override
    public boolean saveGame() {
        GameState currentState = new GameState(
                this.human,
                this.bot,
                this.turnCounter,
                this.getWeatherCondition(),
                this.isHumanTurn()
        );

        SaveManager saveManager = new SaveManagerImpl();
        return saveManager.save(currentState);
    }
}