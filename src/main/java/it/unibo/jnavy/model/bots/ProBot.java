package it.unibo.jnavy.model.bots;

import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

public class ProBot implements BotStrategy{

    @Override
    public Position selectTarget(Grid enemyGrid) {
        Cell[][] cellsMatrix = enemyGrid.getCellsMatrix();

        /*
        flusso del probot:
            - random shot
            - se miss -> random shot || se hit procedi
            - set the lastshotfeedback
            - spara alla adiacente up (controllando che non sia out of bound)
            - se miss (aggiorna lastshotfeedback) -> spara alla prossima adiacente in senso orario || se hit continua in quella DIRECTION
            - se sto sparando in una DIRECTION allora il prossimo miss indica che ho affondato la nave
            - RICOMINCIA IL PATTERN
        */
        return null;
    }

}
