package it.unibo.jnavy.model.grid;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.cell.CellImpl;
import it.unibo.jnavy.model.fleet.Fleet;
import it.unibo.jnavy.model.fleet.FleetImpl;
import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.HitType;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.ShotResult;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Concrete implementation of the Grid interface.
 */
public final class GridImpl implements Grid {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private static final int SIZE = 10;
    private final Cell[][] cells;
    private final Fleet fleet;

    /**
     * Constructs a new Grid with a 10x10 grid.
     */
    public GridImpl() {
        this.cells = new Cell[SIZE][SIZE];
        this.fleet = new FleetImpl();
        initializeGrid();
    }

    private void initializeGrid() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = new CellImpl(new Position(i, j));
            }
        }
    }

    @Override
    public void placeShip(final Ship ship, final Position startPos, final CardinalDirection dir) {
        if (!isPlacementValid(ship, startPos, dir)) {
            throw new IllegalArgumentException("Invalid ship placement!");
        }

        for (int i = 0; i < ship.getSize(); i++) {
            final int x = startPos.x() + (i * dir.getRowOffset());
            final int y = startPos.y() + (i * dir.getColOffset());

            cells[x][y].setShip(ship);
        }

        this.fleet.addShip(ship);
    }

    @Override
    public boolean isPlacementValid(final Ship ship, final Position startPos, final CardinalDirection dir) {
        for (int i = 0; i < ship.getSize(); i++) {
            final int x = startPos.x() + (i * dir.getRowOffset());
            final int y = startPos.y() + (i * dir.getColOffset());

            if (!isPositionValid(new Position(x, y))) {
                return false;
            }

            if (cells[x][y].isOccupied()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ShotResult receiveShot(final Position p) {
        if (!isPositionValid(p)) {
            return ShotResult.failure(p, HitType.INVALID);
        }
        final var targetCell = cells[p.x()][p.y()];

        if (targetCell.isHit()) {
            return ShotResult.failure(p, HitType.INVALID);
        }

        final HitType cellResult = targetCell.receiveShot();
        final Ship ship = targetCell.getShip().orElse(null);

        if (cellResult == HitType.SUNK) {
            if (ship == null) {
                throw new IllegalArgumentException("Sunk ship is null!");
            }
            return ShotResult.sunk(p, ship);
        } else {
            return new ShotResult(cellResult, p, Optional.empty());
        }
    }

    @Override
    public boolean isDefeated() {
        return this.fleet.isDefeated();
    }

    @Override
    public boolean repair(final Position p) {
        return getCell(p).map(c -> {
            if (c.isOccupied() && c.isHit()) {
                    return c.repair();
                }
            return false;
        }).orElse(false);
    }

    @Override
    public Optional<Cell> getCell(final Position p) {
        if (!isPositionValid(p)) {
            return Optional.empty();
        }
        return Optional.of(cells[p.x()][p.y()]);
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP",
        justification = "The game logic requires direct access to the fleet to manage ship statuses."
    )
    @Override
    public Fleet getFleet() {
        return this.fleet;
    }

    @Override
    public List<Position> getAvailableTargets() {
        return Arrays.stream(this.cells)
                .flatMap(Arrays::stream)
                .filter(c -> !c.isHit())
                .map(Cell::getPosition)
                .toList();
    }

    @Override
    public boolean isTargetValid(final Position target) {
        final Cell[][] matrix = this.cells;
        final int x = target.x();
        final int y = target.y();
        return x >= 0
                && x < matrix.length
                && y >= 0
                && y < matrix[0].length
                && this.getCell(target)
                .map(c -> !c.isHit())
                .orElse(false);
    }

    @Override
    public boolean isPositionValid(final Position p) {
        return p.x() >= 0 && p.x() < SIZE && p.y() >= 0 && p.y() < SIZE;
    }

    @Override
    public void removeShip(final Ship ship) {
        Arrays.stream(this.cells)
          .flatMap(Arrays::stream)
          .filter(c -> c.isOccupied() && c.getShip().map(s -> s.equals(ship)).orElse(false))
          .forEach(c -> c.setShip(null));

        this.fleet.removeShip(ship);
    }

    @Override
    public List<Position> getOccupiedPositions() {
        return Arrays.stream(this.cells)
                .flatMap(Arrays::stream)
                .filter(Cell::isOccupied)
                .map(Cell::getPosition)
                .toList();
    }
}
