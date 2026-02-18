package it.unibo.jnavy.controller;

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
}