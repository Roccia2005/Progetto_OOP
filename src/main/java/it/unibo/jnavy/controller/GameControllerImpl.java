package it.unibo.jnavy.controller;


import it.unibo.jnavy.model.Bot;
import it.unibo.jnavy.model.Human;
import it.unibo.jnavy.model.Player;
import it.unibo.jnavy.model.ShotResult;

import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherManager;
import it.unibo.jnavy.model.weather.WeatherManagerImpl;

public class GameControllerImpl implements GameController{

    private Player human;
    private Player bot;
    private Player currentPlayer;
    private WeatherManager weather;

    private int turnCounter = 0;

    public GameControllerImpl() {
        this.human = new Human(null);
        this.bot = new Bot(null);
        this.currentPlayer = this.human;
        this.weather = WeatherManagerImpl.getInstance();
    }

    @Override
    public void processShot(Position p) {
        if (!isHumanTurn()) {
            return;
        }
        ShotResult result = this.weather.applyWeatherEffects(p, this.bot.getGrid());
        endTurn();
    }

    @Override
    public int endTurn() {
        this.human.processTurnEnd();
        this.bot.processTurnEnd();
        this.weather.processTurnEnd();
        this.turnCounter++;
        this.currentPlayer = this.currentPlayer == this.human ? this.bot : this.human;
        if (this.currentPlayer == this.bot) {
            playBotTurn();
        }
        return this.turnCounter;
    }

    private boolean isHumanTurn() {
        return this.currentPlayer == this.human;
    }

    private void playBotTurn() {
        return;
    }
}
