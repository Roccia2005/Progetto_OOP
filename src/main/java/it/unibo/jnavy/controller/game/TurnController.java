package it.unibo.jnavy.controller.game;

import it.unibo.jnavy.model.player.Player;
import it.unibo.jnavy.model.weather.WeatherManager;

public class TurnController {
    private final Player human;
    private final Player bot;
    private final WeatherManager weather;

    private Player currentPlayer;
    private int turnCounter;

    public TurnController(final Player human, final Player bot, final WeatherManager weather,
                          final int turnCounter, final boolean isHumanTurn) {
        this.human = human;
        this.bot = bot;
        this.weather = weather;
        this.turnCounter = turnCounter;
        this.currentPlayer = isHumanTurn ? this.human : this.bot;
    }

    public boolean isHumanTurn() {
        return this.currentPlayer == this.human;
    }

    public boolean isGameOver() {
        return this.human.getFleet().isDefeated() || this.bot.getFleet().isDefeated();
    }

    public boolean isBotDefeated() {
        return this.bot.getFleet().isDefeated();
    }

    public int getTurnCounter() {
        return this.turnCounter;
    }

    public void endTurn() {
        this.turnCounter++;
        this.currentPlayer.processTurnEnd();
        this.weather.processTurnEnd();

        this.currentPlayer = (this.currentPlayer == this.human) ? this.bot : this.human;
    }
}
