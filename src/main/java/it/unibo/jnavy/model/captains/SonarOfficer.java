package it.unibo.jnavy.model.captains;

import java.util.ArrayList;
import java.util.List;

import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Represents the Sonar Officer captain.
 * The Sonar Officer's special ability is to reveal information about a specific cell
 * on the grid without firing a shot. This makes the target cell visible to the player.
 */
public class SonarOfficer extends AbstractCaptain {

    public static final int COOLDOWN = 3;

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    public SonarOfficer() {
        super(COOLDOWN);
    }

    @Override
    public boolean executeEffect(final Grid grid, final Position p) {
        final int effectiveX = Math.max(1, Math.min(p.x(), grid.getSize() - 2));
        final int effectiveY = Math.max(1, Math.min(p.y(), grid.getSize() - 2));
        final List<Cell> targetCells = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                final Position candidate = new Position(effectiveX + dx, effectiveY + dy);
                grid.getCell(candidate).ifPresent(targetCells::add);
            }
        }
        final boolean shipFound = targetCells.stream().anyMatch(Cell::hisDetectable);
        targetCells.forEach(cell -> cell.setScanResult(shipFound));
        return true;
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
