package it.unibo.jnavy.controller;

import it.unibo.jnavy.model.Bot;
import it.unibo.jnavy.model.Human;
import it.unibo.jnavy.model.Player;
import it.unibo.jnavy.model.ShotResult;
import it.unibo.jnavy.model.captains.Captain;
import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherCondition;
import it.unibo.jnavy.model.weather.WeatherManager;
import it.unibo.jnavy.model.weather.WeatherManagerImpl;

public class GameControllerImpl implements GameController{

    private final Human human;
    private final Bot bot;
    private final WeatherManager weather;
    private Player currentPlayer;
    private int turnCounter = 0;

    public GameControllerImpl(Human player, Bot bot) {
        this.human = player;
        this.bot = bot;
        this.currentPlayer = this.human;
        this.weather = WeatherManagerImpl.getInstance();
    }


    @Override
    public int getGridSize() {
        return this.human.getGrid().getSize();
    }

    @Override
    public int getCaptainCooldown() {
        return this.human.getCaptain().getCooldown();
    }

    @Override
    public void processShot(Position p) {
        if (!isHumanTurn()) {
            return;
        }
        this.human.createShot(p, this.bot.getGrid());
        endTurn();
    }

    @Override
    public boolean processAbility(Position p) {
        if (!isHumanTurn()) {
            return false;
        }

        Captain currentCaptain = this.human.getCaptain();
        boolean targetsEnemy = currentCaptain.targetsEnemyGrid();
        Grid targetGrid = targetsEnemy ? this.bot.getGrid() : this.human.getGrid();

        if (this.human.useAbility(p, targetGrid)) {
            if (currentCaptain.doesAbilityConsumeTurn()) {
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
        return this.human.getCaptain().getCurrentCooldown();
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
        } else {
            if (cell.isVisible() || (!isEnemyGrid && cell.isOccupied())) {
                
                if (cell.isOccupied()) {
                    if (isEnemyGrid && cell.isVisible()) {
                        return CellCondition.REVEALED_SHIP; 
                    }
                    return CellCondition.SHIP;
                } else {
                    return CellCondition.REVEALED_WATER; 
                }
                
            } else {
                return CellCondition.FOG;
            }
        }
    }

    @Override
    public boolean isHumanTurn() {
        return this.currentPlayer == this.human;
    }

    @Override
    public void playBotTurn() {
        if (isGameOver()) return;

        Position target = this.bot.decideTarget(this.human.getGrid());
        ShotResult result = this.weather.applyWeatherEffects(target, this.human.getGrid());
        this.bot.receiveFeedback(result.position(), result.hitType());
        endTurn();
    }
}