package it.unibo.jnavy.model.captains;

import java.util.Optional;

import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Represents the Engineer captain.
 * 
 * The Engineer's special ability allows the player to repair a damaged part of a ship.
 */
public class Engineer extends AbstractCaptain{

    public static final int COOLDOWN = 3;

    public Engineer() {
        super(COOLDOWN);
    }

    public boolean executeEffect(Grid grid, Position p) {
        Optional<Cell> cell = grid.getCell(p);
        return cell.isPresent() && grid.repair(p);
    }

    @Override
    public boolean doesAbilityConsumeTurn() {
        return false;
    }

    @Override
    public boolean targetsEnemyGrid() {
        return false;
    }
}