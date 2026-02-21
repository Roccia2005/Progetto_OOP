package it.unibo.jnavy.model.serialization;

import it.unibo.jnavy.model.player.Player;
import it.unibo.jnavy.model.weather.WeatherCondition;

import java.io.Serializable;

/**
 * Represents the serializable state of a game session.
 * This class acts as a data carrier that captures all necessary information
 * to save and subsequently restore a match, including players' status,
 * turn progress, and environmental conditions.
 */
public final class GameState implements Serializable {
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final Player human;
    private final Player bot;
    private final int turnCounter;
    private final boolean isHumanTurn;
    private final WeatherCondition weatherCondition;

    /**
     * Constructs a new GameState with the specified game parameters.
     *
     * @param human the human player instance.
     * @param bot the bot player instance.
     * @param turnCounter the current value of the turn counter.
     * @param weatherCondition the current weather condition affecting the game.
     * @param isHumanTurn true if the game state is saved during the human player's turn.
     */
    public GameState(final Player human, final Player bot, final int turnCounter,
                     final WeatherCondition weatherCondition, final boolean isHumanTurn) {
        this.human = human;
        this.bot = bot;
        this.turnCounter = turnCounter;
        this.weatherCondition = weatherCondition;
        this.isHumanTurn = isHumanTurn;
    }

    /**
     * Retrieves the human player stored in this game state.
     *
     * @return the human player.
     */
    public Player getHuman() {
        return human;
    }

    /**
     * Retrieves the bot player stored in this game state.
     *
     * @return the bot player.
     */
    public Player getBot() {
        return bot;
    }

    /**
     * Retrieves the turn counter recorded at the time of saving.
     *
     * @return the turn counter-value.
     */
    public int getTurnCounter() {
        return turnCounter;
    }

    /**
     * Retrieves the weather condition active during this game state.
     *
     * @return the weather condition.
     */
    public WeatherCondition getWeatherCondition() {
        return weatherCondition;
    }

    /**
     * Checks if it was the human player's turn when the state was captured.
     *
     * @return true if it was the human player's turn, false otherwise.
     */
    public boolean isHumanTurn() {
        return isHumanTurn;
    }
}
