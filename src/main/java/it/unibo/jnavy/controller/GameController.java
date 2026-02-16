package it.unibo.jnavy.controller;

import it.unibo.jnavy.model.utilities.Position;

public interface GameController {

    void processShot(Position p);

    boolean processAbility(Position p);

    int endTurn();

    boolean isGameOver();
}