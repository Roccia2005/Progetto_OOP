package it.unibo.jnavy.controller.game;

import it.unibo.jnavy.controller.utilities.CellCondition;
import it.unibo.jnavy.model.utilities.Position;

public interface GameController {

    int getGridSize();

    int getCaptainCooldown();

    void processShot(Position p);

    boolean processAbility(Position p);

    boolean isGameOver();

    int getCurrentCaptainCooldown();

    CellCondition getHumanCellState(Position p);

    CellCondition getBotCellState(Position p);

    String getWeatherConditionName();

    boolean isHumanTurn();

    Position playBotTurn();

    String getBotDifficulty();

    String getPlayerCaptainName();

    boolean isBotDefeated();

    boolean captainAbilityTargetsEnemyGrid();

    /**
     * Salva lo stato attuale della partita su file.
     * @return true se il salvataggio va a buon fine, false altrimenti.
     */
    boolean saveGame();
}