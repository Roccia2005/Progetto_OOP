package it.unibo.jnavy.model.grid;

import it.unibo.jnavy.model.HitType;
import it.unibo.jnavy.model.ShotResult;
import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.cell.CellImpl;
import it.unibo.jnavy.model.fleet.Fleet;
import it.unibo.jnavy.model.fleet.FleetImpl;
import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Concrete implementation of the Grid interface.
 */
public class GridImpl implements Grid {

    private static final int SIZE = 10;
    private final Cell[][] cells;
    private final Fleet fleet;

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
    public void placeShip(Ship ship, Position startPos, CardinalDirection dir) {
        if (!isPlacementValid(ship, startPos, dir)) {
            throw new IllegalArgumentException("Invalid ship placement!");
        }

        for (int i = 0; i < ship.getSize(); i++) {
            int x = startPos.x() + (i * dir.getRowOffset());
            int y = startPos.y() + (i * dir.getColOffset());

            cells[x][y].setShip(ship);
        }

        this.fleet.addShip(ship);
    }

    @Override
    public boolean isPlacementValid(Ship ship, Position startPos, CardinalDirection dir) {
        for (int i = 0; i < ship.getSize(); i++) {
            int x = startPos.x() + (i * dir.getRowOffset());
            int y = startPos.y() + (i * dir.getColOffset());

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
    public ShotResult receiveShot(Position p) {
        if (!isPositionValid(p)) {
            return ShotResult.failure(p, HitType.INVALID);
        }
        var targetCell = cells[p.x()][p.y()];

        HitType cellResult = targetCell.receiveShot();
        Ship ship = targetCell.getShip().orElse(null);

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
    public boolean repair(Position p) {
        return getCell(p).map(c -> {
            boolean result = false;

            if (c.isOccupied() && c.isHit() && !c.getShip().map(Ship::isSunk).orElse(false)) {
                c.repair();
                Ship s = c.getShip().get();
                s.setHealth(s.getHealth() + 1);
                result = true;
            }
            return result;
        }).orElse(false);
    }

    @Override
    public Optional<Cell> getCell(Position p) {
        if (!isPositionValid(p)) {
            return Optional.empty();
        }
        return Optional.of(cells[p.x()][p.y()]);
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public Fleet getFleet() {
        return this.fleet;
    }

    @Override
    public List<Position> getPositions() {
        final Cell[][] matrix = this.getCellMatrix();
        if (matrix == null) {
            return new ArrayList<>();
        }

        return Arrays.stream(matrix)
                .flatMap(Arrays::stream)
                .filter(c -> !c.isHit())
                .map(Cell::getPosition)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isTargetValid(Position target) {
        Cell[][] matrix = this.getCellMatrix();
        int x = target.x();
        int y = target.y();
        return x >= 0
                && x < matrix.length
                && y >= 0
                && y < matrix[0].length
                && this.getCell(target)  //metto il controllo sulla cell in fondo così non rischio che venga controllato un index non valido sulla grid
                .map(c -> !c.isHit())
                .orElse(false);
    }


    @Override
    public boolean isPositionValid(Position p) {
        return p.x() >= 0 && p.x() < SIZE && p.y() >= 0 && p.y() < SIZE;
    }

    @Override
    public void removeShip(Ship ship) {
        Arrays.stream(this.cells)              
          .flatMap(Arrays::stream)         
          .filter(c -> c.isOccupied() && c.getShip().map(s -> s.equals(ship)).orElse(false))
          .forEach(c -> c.setShip(null));

        this.fleet.removeShip(ship);
    }

    private Cell[][] getCellMatrix() {
        return this.cells;
    }
}
