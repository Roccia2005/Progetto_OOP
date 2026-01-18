package it.unibo.jnavy.model.captains;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

public class Gunner extends AbstractCaptain{

    private static final int COOLDOWN = 3;

    public Gunner() {
        super(COOLDOWN);
    }

    @Override
    public boolean useAbility(Grid grid, Position p) {
        if (this.isAbilityRecharged()) {
            HitStrategy areaShot = new AreaShot();
            areaShot.execute(p, grid);
            this.resetCooldown();
            return true;
        }
        return false;
    }
    
}