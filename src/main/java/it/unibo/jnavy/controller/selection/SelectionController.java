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

public class SelectionController {
    private View view;
    private BotStrategy selectedBotStrategy;
    private Captain selectedCaptain;
    private boolean isSniperSelected;

    public void setView(final View view) {
        this.view = view;
    }

    public void newGame() {
        final SaveManager saveManager = new SaveManagerImpl();
        saveManager.deleteSave();
        this.selectedBotStrategy = null;
        this.selectedCaptain = null;
        this.isSniperSelected = false;
        this.view.showBotSelection();
    }

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

    public void capSelection(final CaptainAbility ability) {
        switch (ability) {
            case ENGINEER -> this.selectedCaptain = new Engineer();
            case GUNNER -> this.selectedCaptain = new Gunner();
            case SONAROFFICER -> this.selectedCaptain = new SonarOfficer();
        }
        final SetupController setupController = new SetupControllerImpl(this.selectedCaptain, this.selectedBotStrategy);
        view.showSetupPhase(setupController);
    }

    public void setupComplete(final SetupController completedSetup) {
        final Human humanPlayer = (Human) completedSetup.getHumanPlayer();
        final Bot botPlayer = (Bot) completedSetup.getBotPlayer();
        if (isSniperSelected) {
            botPlayer.setStrategy(new SniperBot(humanPlayer.getGrid().getOccupiedPositions()));
        }
        final GameController gameController = new GameControllerImpl(humanPlayer, botPlayer);
        view.showGamePhase(gameController);
    }

    public void backToMenu() {
        this.selectedBotStrategy = null;
        this.selectedCaptain = null;
        this.isSniperSelected = false;
        view.showStartScreen();
    }

    public void showStartScreen() {
        view.showStartScreen();
    }

    public void showBotSelection() {
        this.selectedCaptain = null;
        view.showBotSelection();
    }

    public void showCaptainSelection() {
        this.selectedCaptain = null;
        view.showCaptainSelection();
    }
}
