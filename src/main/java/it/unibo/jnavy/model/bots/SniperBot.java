package it.unibo.jnavy.model.bots;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import it.unibo.jnavy.model.HitType;
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
                        Ship ship = cell.getShip();

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

    @Override
    public void lastShotFeedback(final Position p, final HitType state) {
        // ho ancora knownTargets a sinistra quindi evito di intaccare la logica di probot col rischio di colpire a sinistra in seeking
        if (!this.knownTargets.isEmpty()) {
            return;
        }
        super.lastShotFeedback(p, state);
    }
}
