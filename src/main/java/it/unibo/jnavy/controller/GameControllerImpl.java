package it.unibo.jnavy.controller;

import it.unibo.jnavy.model.Bot;
import it.unibo.jnavy.model.Human;
import it.unibo.jnavy.model.Player;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherManager;
import it.unibo.jnavy.model.weather.WeatherManagerImpl;

public class GameControllerImpl implements GameController{

    private Player human;
    private Player bot;
    private WeatherManager weather;

    private int turnCounter = 0;

    public GameControllerImpl() {
        this.human = new Human(null);
        this.bot = new Bot(null);
        this.weather = WeatherManagerImpl.getInstance();
    }

    @Override
    public void startGame() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'startGame'");
    }

    @Override
    public void processShot(Position p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processShot'");
    }

    @Override
    public int endTurn() {
        this.human.processTurnEnd();
        this.bot.processTurnEnd();
        this.weather.processTurnEnd();
        return ++this.turnCounter;
    }
}
