package it.unibo.jnavy.controller;


import java.util.List;

import it.unibo.jnavy.model.Bot;
import it.unibo.jnavy.model.Human;
import it.unibo.jnavy.model.Player;
import it.unibo.jnavy.model.ShotResult;
import it.unibo.jnavy.model.captains.Captain;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherManager;
import it.unibo.jnavy.model.weather.WeatherManagerImpl;

public class GameControllerImpl implements GameController{

    private Player human;
    private Player bot;
    private Player currentPlayer;
    private WeatherManager weather;

    private int turnCounter = 0;

    public GameControllerImpl(Human player, Bot bot) {
        this.human = player;
        this.bot = bot;
        this.currentPlayer = this.human;
        this.weather = WeatherManagerImpl.getInstance();
    }

    @Override
    public void processShot(Position p) {
        if (!isHumanTurn()) {
            return;
        }
        List<ShotResult> results = this.human.createShot(p, this.bot.getGrid());
        endTurn();
    }

    @Override
    public int endTurn() {
        this.human.processTurnEnd();
        this.weather.processTurnEnd();
        this.turnCounter++;
        this.currentPlayer = this.currentPlayer == this.human ? this.bot : this.human;
        if (this.currentPlayer == this.bot) {
            playBotTurn();
        }
        this.bot.processTurnEnd();
        return this.turnCounter;
    }

    @Override
    public boolean processAbility(Position p) {
        if (!isHumanTurn()) {
            return false; 
        }

        Human humanPlayer = (Human) this.human;
        Captain currentCaptain = humanPlayer.getCaptain();
        boolean targetsEnemy = currentCaptain.targetsEnemyGrid();
        Grid targetGrid = targetsEnemy ? this.bot.getGrid() : this.human.getGrid();

        if (humanPlayer.useAbility(p, targetGrid)) {
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

    private boolean isHumanTurn() {
        return this.currentPlayer == this.human;
    }

    private void playBotTurn() {
        return;
    }
}
