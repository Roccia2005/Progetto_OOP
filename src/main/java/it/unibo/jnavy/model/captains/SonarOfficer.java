package it.unibo.jnavy.model.captains;

import java.util.Optional;

import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

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
