package it.unibo.jnavy.view;

import it.unibo.jnavy.controller.game.GameController;
import it.unibo.jnavy.controller.setup.SetupController;

public interface View {

    void start();

    void showStartScreen();

    void showBotSelection();

    void showCaptainSelection();

    void showSetupPhase(SetupController setupController);

    void showGamePhase(GameController gameController);

    void showError(String message);
}
