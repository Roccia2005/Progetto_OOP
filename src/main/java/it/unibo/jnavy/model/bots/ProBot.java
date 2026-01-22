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
            - sparo random finchè non prendo una cella con nave sopra (salva posizione come FIRSTHITPOSITION)
            - sparo alla adiacente up (se valida), miss1 = sparo alla adiacente destra (se valida), miss2 = sparo alla adiacente sotto (se valida), miss3 = sparo alla adiacente a sinistra (per forza sarà valida) ---> si avrà hit = (salva direction)
            - sparo successivo nella cella adiacente in quella specifica DIRECTION
            - hit = continuo a sparare alla adiacente in quella specifica DIRECTION | miss = DIRECTION = INVERTIDIRECTION
            - sparo alla adiacente della FIRSTHITPOSITION in quella specifica nuova DIRECTION
            - hit = continuo | miss = nave affondata
            - ricomincia il flusso!
        */
        return null;
    }

}
