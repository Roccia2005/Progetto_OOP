package it.unibo.jnavy.controller;

import it.unibo.jnavy.model.utilities.Position;

public interface GameController {

    void startGame();

    void processShot(Position p);

    void endTurn();
}