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

    @Override
    public boolean useAbility(Grid grid, Position p) {
        if (this.isAbilityRecharged()) {
            Optional<Cell> cell = grid.getCell(p);
            if (cell.isPresent() && grid.repair(p)) {
                this.resetCooldown();
                return true;
            }
        }
        return false;
    }
}