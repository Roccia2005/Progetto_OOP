package it.unibo.jnavy.model.captains;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.shots.AreaShot;
import it.unibo.jnavy.model.shots.HitStrategy;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Represents the Gunner captain.
 * 
 * The Gunner's special ability is an offensive move that executes an {@link AreaShot}.
 * Instead of hitting a single cell, this ability targets a 2x2 area, maximizing the potential damage.
 */
public class Gunner extends AbstractCaptain{

    public static final int COOLDOWN = 3;

    public Gunner() {
        super(COOLDOWN);
    }

    @Override
    public boolean useAbility(Grid grid, Position p) {
        if (this.isAbilityRecharged() && grid.isPositionValid(p)) {
            HitStrategy areaShot = new AreaShot();
            areaShot.execute(p, grid);
            this.resetCooldown();
            return true;
        }
        return false;
    }
    
}