package it.unibo.jnavy.model.serialization;

import it.unibo.jnavy.model.player.Player;
import it.unibo.jnavy.model.weather.WeatherCondition;

import java.io.Serializable;

public class GameState implements Serializable {
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final Player human;
    private final Player bot;
    private final int turnCounter;
    private boolean isHumanTurn;
    private final WeatherCondition weatherCondition;


    public GameState(Player human, Player bot, int turnCounter, WeatherCondition weatherCondition, boolean isHumanTurn) {
        this.human = human;
        this.bot = bot;
        this.turnCounter = turnCounter;
        this.weatherCondition = weatherCondition;
        this.isHumanTurn = isHumanTurn;
    }

    public Player getHuman() {
        return human;
    }

    public Player getBot() {
        return bot;
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public WeatherCondition getWeatherCondition() {
        return weatherCondition;
    }

    public boolean isHumanTurn() {
        return isHumanTurn;
    }
}
