package it.unibo.jnavy.view;

import it.unibo.jnavy.controller.game.GameController;
import it.unibo.jnavy.controller.setup.SetupController;

/**
 * The main interface for the application's View component.
 * It defines the methods required to transition between different screens
 * and display game states to the user.
 */
public interface View {

    /**
     * Starts the view application, initializing and displaying the main window.
     */
    void start();

    /**
     * Displays the initial start screen (main menu).
     */
    void showStartScreen();

    /**
     * Navigates to the bot selection screen to choose the opponent's difficulty.
     */
    void showBotSelection();

    /**
     * Navigates to the captain selection screen to choose the player's special ability.
     */
    void showCaptainSelection();

    /**
     * Displays the ship placement phase.
     * @param setupController the controller handling the setup logic
     */
    void showSetupPhase(SetupController setupController);

    /**
     * Displays the main game interface where the battle takes place.
     * @param gameController the controller handling the match logic
     */
    void showGamePhase(GameController gameController);

    /**
     * Shows an error message to the user, typically via a dialog or notification.
     * @param message the error message to be displayed
     */
    void showError(String message);
}
