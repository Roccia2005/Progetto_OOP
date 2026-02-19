package it.unibo.jnavy.controller.game;

import it.unibo.jnavy.controller.utilities.CellCondition;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherCondition;

public interface GameController {

    int getGridSize();

    int getCaptainCooldown();

    void processShot(Position p);

    boolean processAbility(Position p);

    boolean isGameOver();

    int getCurrentCaptainCooldown();

    CellCondition getHumanCellState(Position p);

    CellCondition getBotCellState(Position p);

    WeatherCondition getWeatherCondition();

    boolean isHumanTurn();

    void playBotTurn();

    String getBotDifficulty();

    String getPlayerCaptainName();

    boolean isBotDefeated();

    boolean captainAbilityTargetsEnemyGrid();
}