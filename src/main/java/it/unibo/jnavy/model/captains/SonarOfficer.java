package it.unibo.jnavy.model.captains;

import java.util.ArrayList;
import java.util.List;

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

    public static final int COOLDOWN = 3;

    public SonarOfficer() {
        super(COOLDOWN);
    }

    @Override
    public boolean useAbility(Grid grid, Position p) {
        if (this.isAbilityRecharged() && grid.isPositionValid(p)) {
            int effectiveX = Math.max(1, Math.min(p.x(), grid.getSize() - 2));
            int effectiveY = Math.max(1, Math.min(p.y(), grid.getSize() - 2));
            List<Cell> targetCells = new ArrayList<>();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    Position candidate = new Position(effectiveX + dx, effectiveY + dy);
                    if (grid.isPositionValid(candidate)) {
                        grid.getCell(candidate).ifPresent(targetCells::add);
                    }
                }
            }
            boolean shipFound = targetCells.stream().anyMatch(Cell::hisDetectable);
            targetCells.forEach(cell -> cell.setScanResult(shipFound));
            this.resetCooldown();
            return true;
        }
        return false;
    }

    @Override
    public boolean doesAbilityConsumeTurn() {
        return false;
    }

    @Override
    public boolean targetsEnemyGrid() {
        return true;
    }

}
