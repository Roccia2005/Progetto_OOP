package it.unibo.jnavy.model.bots;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.utilities.Position;

public class SniperBot extends ProBot{

    private final List<Position> knownTargets;
    private final int halfColumns;

    public SniperBot(final Grid humanGrid) {
        super();
        this.knownTargets = new ArrayList<>();
        this.halfColumns = humanGrid.getSize() / 2;
        populateKnownTargets(humanGrid);
    }

    private void populateKnownTargets(final Grid grid) {
        int rows = grid.getSize();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < this.halfColumns; j++) {
                Position pos = new Position(i, j);
                Optional<Cell> cell = grid.getCell(pos);

                if (cell.isPresent() && cell.get().isOccupied()) {
                    this.knownTargets.add(pos);
                }
            }
        }
    }

    @Override
    public Position selectTarget(final Grid enemyGrid) {
        //ogni volta che entro tolgo i vari cell che ho colpito
        Iterator<Position> iterator = this.knownTargets.iterator();
        while (iterator.hasNext()) {
            Position p = iterator.next();
            Optional<Cell> cell = enemyGrid.getCell(p);
            if (cell.isPresent() && cell.get().isHit()) {
                iterator.remove();
            }
        }

        //controllo dell'attraversamento della metà della griglia
        Optional<Position> borderTarget = checkBorderCrossing(enemyGrid);
        if (borderTarget.isPresent()) {
            return borderTarget.get();
        }

        // do come posizione la prima delle conosciute
        if (!this.knownTargets.isEmpty()) {
            return this.knownTargets.getFirst();
        }

        return super.selectTarget(enemyGrid); // fase del funzionamento come probot nel caso non ci siano più celle conosciute asx
    }

    private Optional<Position> checkBorderCrossing(final Grid grid) {
        //devo capire se esiste una nave colpita nella metà sinistra e continua nella parte destra
        //itero sulla parte sx
        for (int i = 0; i < grid.getSize(); i++) {
            for (int j = 0; j < this.halfColumns; j++) {
                Position p = new Position(i, j);
                Optional<Cell> optCell = grid.getCell(p);

                if (optCell.isPresent()) {
                    Cell cell = optCell.get();
                    if (cell.isHit() && cell.isOccupied()) {
                        Ship ship = cell.getShip().get();

                        //ora se la nave nella cella non è affondata
                        if (!ship.isSunk()) {
                            boolean shipHasPiecesRemaining = false;
                            for (Position targetPos : this.knownTargets) {
                                Optional<Cell> targetCell = grid.getCell(targetPos);
                                //se il target fa parte della nava allora mi fermo
                                if (targetCell.isPresent() && targetCell.get().getShip().equals(ship)) {
                                    shipHasPiecesRemaining = true;
                                    break;
                                }

                            }

                            if (!shipHasPiecesRemaining) { //se non ci sono pezzi della nave a sinistra allora cerco a destra
                                int nextY = this.halfColumns;

                                while (nextY < grid.getSize()) {
                                    Position pos = new Position(i, nextY);
                                    Optional<Cell> c = grid.getCell(pos);
                                    if (c.isPresent() && !c.get().isHit()) {
                                        return Optional.of(pos);
                                    }
                                    nextY++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    protected Position getRandomValidPosition(final Grid enemyGrid) { //deve tirare solo nella metà dx, non va bene il tiro casuale in tutta la griglia
        List<Position> validCells = super.getValidCellsList(enemyGrid);
        List<Position> halfValidCellsDx = new ArrayList<>();

        for (Position p : validCells) {
            if (p.y() >= this.halfColumns) {
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
