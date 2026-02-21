package it.unibo.jnavy.model.captains;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.shots.AreaShot;
import it.unibo.jnavy.model.shots.HitStrategy;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Represents the Gunner captain.
 * The Gunner's special ability is an offensive move that executes an {@link AreaShot}.
 * Instead of hitting a single cell, this ability targets a 2x2 area, maximizing the potential damage.
 */
public final class Gunner extends AbstractCaptain {

    public static final int COOLDOWN = 3;

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a Gunner with a cooldown of 3 turns.
     */
    public Gunner() {
        super(COOLDOWN);
    }

    @Override
    public boolean executeEffect(final Grid grid, final Position p) {
        final HitStrategy areaShot = new AreaShot(true);
        areaShot.execute(p, grid);
        return true;
    }

    @Override
    public boolean doesAbilityConsumeTurn() {
        return true;
    }

    @Override
    public boolean targetsEnemyGrid() {
        return true;
    }
}
