package it.unibo.jnavy.model.grid;

import it.unibo.jnavy.model.HitType;
import it.unibo.jnavy.model.ShotResult;
import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.cell.CellImpl;
import it.unibo.jnavy.model.fleet.Fleet;
import it.unibo.jnavy.model.fleet.FleetImpl;
import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.utilities.Direction;
import it.unibo.jnavy.model.utilities.Position;

import java.util.Optional;

/**
 * Concrete implementation of the Grid interface.
 */
public class GridImpl implements Grid{

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
    public void placeShip(Ship ship, Position startPos, Direction dir) {
        if (!isPlacementValid(ship, startPos, dir)) {
            throw new IllegalArgumentException("Invalid ship placement!");
        }

        for (int i = 0; i < ship.getSize(); i++) {
            int x = startPos.x() + (dir == Direction.VERTICAL ? i : 0);
            int y = startPos.y() + (dir == Direction.HORIZONTAL ? i : 0);

            cells[x][y].setShip(ship);
        }

        this.fleet.addShip(ship);
    }

    @Override
    public boolean isPlacementValid(Ship ship, Position startPos, Direction dir) {
        for (int i = 0; i < ship.getSize(); i++) {
            int x = startPos.x() + (dir == Direction.VERTICAL ? i : 0);
            int y = startPos.y() + (dir == Direction.HORIZONTAL ? i : 0);

            if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
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
        if (p.x() < 0 || p.x() >= SIZE || p.y() < 0 || p.y() >= SIZE) {
            return ShotResult.failure(p, HitType.INVALID);
        }
        var targetCell = cells[p.x()][p.y()];

        HitType cellResult = targetCell.receiveShot();

        if (cellResult == HitType.SUNK) {
            Ship sunkShip = targetCell.getShip();
            return ShotResult.sunk(p, sunkShip);
        }

        return new ShotResult(cellResult, p, Optional.empty());
    }

    @Override
    public boolean isDefeated() {
        return this.fleet.isDefeated();
    }

    @Override
    public boolean repair(Position p) {
        Cell cellToRapair = getCell(p).get();
        if(cellToRapair.isOccupied()) {
            Ship s = cellToRapair.getShip();
            s.setHealth(s.getHealth() + 1);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Cell> getCell(Position p) {
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

    
} 
