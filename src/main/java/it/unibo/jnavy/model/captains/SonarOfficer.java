package it.unibo.jnavy.model.captains;

import java.util.Optional;

import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Represents the Sonar Officer captain.
 * 
 * The Sonar Officer's special ability is to reveal information about a specific cell
 * on the grid without firing a shot. This makes the target cell visible to the player.
 */
public class SonarOfficer extends AbstractCaptain {

    private static final int COOLDOWN = 3;

    public SonarOfficer() {
        super(COOLDOWN);
    }

    @Override
    public boolean useAbility(Grid grid, Position p) {
        Optional<Cell> cell = grid.getCell(p);
        if (cell.isPresent()) {
            cell.get().setVisible();
            this.resetCooldown();
            return true;
        }
        return false;
    }

}
