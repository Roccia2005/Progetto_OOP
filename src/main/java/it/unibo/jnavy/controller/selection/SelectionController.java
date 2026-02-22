package it.unibo.jnavy.controller.selection;

import it.unibo.jnavy.controller.game.GameController;
import it.unibo.jnavy.controller.game.GameControllerImpl;
import it.unibo.jnavy.controller.setup.SetupController;
import it.unibo.jnavy.controller.setup.SetupControllerImpl;
import it.unibo.jnavy.model.bots.BeginnerBot;
import it.unibo.jnavy.model.bots.BotStrategy;
import it.unibo.jnavy.model.bots.ProBot;
import it.unibo.jnavy.model.bots.SniperBot;
import it.unibo.jnavy.model.captains.Captain;
import it.unibo.jnavy.model.captains.Engineer;
import it.unibo.jnavy.model.captains.Gunner;
import it.unibo.jnavy.model.captains.SonarOfficer;
import it.unibo.jnavy.model.player.Bot;
import it.unibo.jnavy.model.player.Human;
import it.unibo.jnavy.model.serialization.SaveManager;
import it.unibo.jnavy.model.serialization.SaveManagerImpl;
import it.unibo.jnavy.view.View;
import it.unibo.jnavy.view.selection.BotSelectionPanel.BotLevel;
import it.unibo.jnavy.view.selection.CapSelectionPanel.CaptainAbility;

/**
 * Controller responsible for managing the initial selection phases of the game.
 * It handles navigation between menus (Bot selection, Captain selection),
 * the loading/deletion of save files, and the initialization of the setup phase.
 */
public class SelectionController {
    private View view;
    private BotStrategy selectedBotStrategy;
    private Captain selectedCaptain;
    private boolean isSniperSelected;

    /**
     * Associates a view with this controller.
     *
     * @param view the view implementation to be used for UI updates.
     */
    public void setView(final View view) {
        this.view = view;
    }

    /**
     * Initializes a new game session.
     * It clears any existing save data, resets the selection state,
     * and directs the view to the bot selection screen.
     */
    public void newGame() {
        final SaveManager saveManager = new SaveManagerImpl();
        saveManager.deleteSave();
        this.selectedBotStrategy = null;
        this.selectedCaptain = null;
        this.isSniperSelected = false;
        this.view.showBotSelection();
    }

    /**
     * Attempts to load a previously saved game state.
     * If a valid save is found, it initializes the game controller and switches
     * to the game phase; otherwise, it displays an error message.
     */
    public void loadGame() {
        final SaveManager saveManager = new SaveManagerImpl();

        saveManager.load().ifPresentOrElse(
                loadedState -> {
                    final GameController loadedController = new GameControllerImpl(loadedState);
                    this.view.showGamePhase(loadedController);
                },
                () -> view.showError("No valid save file found")
        );
    }

    /**
     * Handles the selection of the bot's difficulty level.
     *
     * @param level the chosen difficulty level for the bot.
     */
    public void botSelection(final BotLevel level) {
        switch (level) {
            case BEGINNER -> this.selectedBotStrategy = new BeginnerBot();
            case PRO -> this.selectedBotStrategy = new ProBot();
            case SNIPER -> {
                this.selectedBotStrategy = new ProBot();
                this.isSniperSelected = true;
            }
        }
        view.showCaptainSelection();
    }

    /**
     * Handles the selection of the player's captain and initiates the setup phase.
     *
     * @param ability the chosen captain ability/type.
     */
    public void capSelection(final CaptainAbility ability) {
        switch (ability) {
            case ENGINEER -> this.selectedCaptain = new Engineer();
            case GUNNER -> this.selectedCaptain = new Gunner();
            case SONAROFFICER -> this.selectedCaptain = new SonarOfficer();
        }
        final SetupController setupController = new SetupControllerImpl(this.selectedCaptain, this.selectedBotStrategy);
        view.showSetupPhase(setupController);
    }

    /**
     * Transitions the game from the setup phase to the main combat phase.
     * It finalizes the bot's strategy (specifically for Sniper mode) and
     * initializes the main GameController.
     *
     * @param completedSetup the setup controller containing the finalized player configuration
     */
    public void setupComplete(final SetupController completedSetup) {
        final Human humanPlayer = (Human) completedSetup.getHumanPlayer();
        final Bot botPlayer = (Bot) completedSetup.getBotPlayer();
        if (isSniperSelected) {
            botPlayer.setStrategy(new SniperBot(humanPlayer.getGrid().getOccupiedPositions()));
        }
        final GameController gameController = new GameControllerImpl(humanPlayer, botPlayer);
        view.showGamePhase(gameController);
    }

    /**
     * Resets the current selection state and returns the user to the main menu.
     */
    public void backToMenu() {
        this.selectedBotStrategy = null;
        this.selectedCaptain = null;
        this.isSniperSelected = false;
        view.showStartScreen();
    }

    /**
     * Commands the view to display the initial start screen.
     */
    public void showStartScreen() {
        view.showStartScreen();
    }

    /**
     * Commands the view to display the bot selection screen, resetting
     * previous captain choices.
     */
    public void showBotSelection() {
        this.selectedCaptain = null;
        view.showBotSelection();
    }

    /**
     * Commands the view to display the captain selection screen, resetting
     * previous captain choices.
     */
    public void showCaptainSelection() {
        this.selectedCaptain = null;
        view.showCaptainSelection();
    }
}
