package it.unibo.jnavy.controller;

import it.unibo.jnavy.model.utilities.Position;

public interface GameController {

    void processShot(Position p);

    int endTurn();
}