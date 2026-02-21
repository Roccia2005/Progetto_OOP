package it.unibo.jnavy.controller.game;

import it.unibo.jnavy.model.player.Player;
import it.unibo.jnavy.model.weather.WeatherManager;

/**
 * Controller responsible for managing the turn-based logic of the game.
 * It tracks the current player, maintains the turn counter, and coordinates
 * the transition between the human player and the bot player.
 */
public class TurnController {
    private final Player human;
    private final Player bot;
    private final WeatherManager weather;

    private Player currentPlayer;
    private int turnCounter;

    /**
     * Constructs a TurnController with the specified players and initial state.
     *
     * @param human the human player instance.
     * @param bot the bot player instance.
     * @param weather the manager responsible for game weather conditions.
     * @param turnCounter the starting value for the turn counter.
     * @param isHumanTurn true if the game should start with the human player's turn.
     */
    public TurnController(final Player human, final Player bot, final WeatherManager weather,
                          final int turnCounter, final boolean isHumanTurn) {
        this.human = human;
        this.bot = bot;
        this.weather = weather;
        this.turnCounter = turnCounter;
        this.currentPlayer = isHumanTurn ? this.human : this.bot;
    }

    /**
     * Checks if it is currently the human player's turn.
     *
     * @return true if the current player is the human, false if it is the bot.
     */
    public boolean isHumanTurn() {
        return this.currentPlayer.equals(this.human);
    }

    /**
     * Determines if the game has ended based on the status of the players' fleets.
     *
     * @return true if either player's fleet is completely destroyed, false otherwise.
     */
    public boolean isGameOver() {
        return this.human.getFleet().isDefeated() || this.bot.getFleet().isDefeated();
    }

    /**
     * Specifically, checks if the bot player has been defeated.
     *
     * @return true if the bot's fleet is destroyed, false otherwise.
     */
    public boolean isBotDefeated() {
        return this.bot.getFleet().isDefeated();
    }

    /**
     * Retrieves the total number of turns that have occurred during the session.
     *
     * @return the current turn count.
     */
    public int getTurnCounter() {
        return this.turnCounter;
    }

    /**
     * Concludes the current turn, triggering end-of-turn processes for the player
     * and weather system, increments the turn counter, and switches the active player.
     */
    public void endTurn() {
        this.turnCounter++;
        this.currentPlayer.processTurnEnd();
        this.weather.processTurnEnd();

        this.currentPlayer = (this.currentPlayer.equals(this.human)) ? this.bot : this.human;
    }
}
