package it.unibo.jnavy.controller.game;

import it.unibo.jnavy.controller.utilities.CellCondition;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherCondition;

import java.util.List;

public interface GameController {

    int getGridSize();

    int getCaptainCooldown();

    List<Position> processShot(Position p);

    List<Position> processAbility(Position p);

    boolean isGameOver();

    int getCurrentCaptainCooldown();

    CellCondition getHumanCellState(Position p);

    CellCondition getBotCellState(Position p);

    WeatherCondition getWeatherCondition();

    boolean isHumanTurn();

    Position playBotTurn();

    String getBotDifficulty();

    String getPlayerCaptainName();

    boolean isBotDefeated();

    boolean captainAbilityTargetsEnemyGrid();
}