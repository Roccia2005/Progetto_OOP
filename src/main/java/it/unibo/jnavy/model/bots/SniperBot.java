package it.unibo.jnavy.model.bots;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import it.unibo.jnavy.model.HitType;
import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

public class SniperBot extends ProBot{

    private final List<Position> perfectTargets;

    public SniperBot(final Grid humanGrid) {
        super();
        this.perfectTargets = new ArrayList<>();
        populatePerfectTargets(humanGrid);
    }

    private void populatePerfectTargets(final Grid grid) {
        int rows = grid.getSize();
        int halfColumns = grid.getSize() / 2; //campo? rivedere

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < halfColumns; j++) {
                Position pos = new Position(i, j);
                Optional<Cell> cell = grid.getCell(pos);

                if (cell.isPresent() && cell.get().isOccupied()) {
                    this.perfectTargets.add(pos);
                }
            }
        }
    }

    @Override
    public Position selectTarget(final Grid enemyGrid) {
        //ogni volta che entro tolgo i vari cell che ho colpito
        Iterator<Position> iterator = this.perfectTargets.iterator();
        while (iterator.hasNext()) {
            Position p = iterator.next();
            Optional<Cell> cell = enemyGrid.getCell(p);
            if (cell.isPresent() && cell.get().isHit()) {
                iterator.remove();
            }
        }

        //controllo confine della metà della griglia, da capire
        /**
         *
         *
         */

        // do come posizione la prima delle conosciute
        if (!this.perfectTargets.isEmpty()) {
            return this.perfectTargets.getFirst();
        }

        return super.selectTarget(enemyGrid); // fase del funzionamento come probot nel caso non ci siano più celle conosciute asx
    }

    @Override
    public void lastShotFeedback(final Position target, final HitType result) {
        //modifica allo switch per tenere stato sniper?
    }

    @Override
    protected Position getRandomValidPosition(final Grid enemyGrid) { //deve tirare solo nella metà dx, non va bene il tiro casuale in tutta la griglia
        List<Position> validCells = super.getValidCellsList(enemyGrid);
        List<Position> halfValidCellsDx = new ArrayList<>();

        for (Position p : validCells) {
            if (p.y() >= (enemyGrid.getSize() / 2)) {
                halfValidCellsDx.add(p);
            }
        }

        if (halfValidCellsDx.isEmpty()) {
            throw new IllegalStateException("the bot has no valid cells in the right halfgrid");
        }
        return halfValidCellsDx.get(super.getRandomIndex(halfValidCellsDx));
    }

    /**
     * Cosa viene fornito allo sniper bot:
     * - enemyGrid = griglia avversaria reale (il controller passa direttamente al costruttore dello sniper la human.getGrid() alla creazione di esso)
     * - lo sniper si prende la metà sinistra e salva tutto in una lista knownTargets
     *
     * Cosa fa lo sniper bot finchè ci sono celle in knownTargets:
     * - come prima cella da colpire sceglie la prima cella occupata che incontra nella lista knownTargets;
     * - se colpisce una nave (HIT) ma non la affonda, controlla la knownTargets:
     *      1. se il resto della nave è ancora nella lista (quindi nella metà sinistra): continua a sparare a quelle coordinate.
     *      2. se il resto della nave NON è nella lista (quindi la nave attraversa il confine verso destra):
     *              a. il Bot calcola la prossima cella adiacente verso DESTRA (nella zona buia) e spara li'.
     *
     * Cosa fa lo sniper bot quando knownTargets è vuota:
     * - distrutte tutte le navi nella metà sinistra, sniperBot si comporta come proBot MA solo nella metà destra.
     *
     * Devo ricordarmi di estendere da ProBot direttamente così da avere il comportamento già pronto!!!
     */
}
