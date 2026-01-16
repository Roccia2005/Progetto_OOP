package it.unibo.jnavy.model.captains;

import it.unibo.jnavy.model.ShotResult;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

import java.util.List;

public class AreaShot implements HitStrategy {

    @Override
    public List<ShotResult> execute(Position target, Grid grid) {
        return List.of();
    }
}
