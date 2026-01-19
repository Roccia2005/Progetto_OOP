package it.unibo.jnavy.model.shots;

import it.unibo.jnavy.model.ShotResult;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

import java.util.List;

/**
 * Represents a standard attack that hits a single cell.
 * This is the default shot strategy.
 */
public class StandardShot implements HitStrategy {

    //aggiungere campo position dello shot
    Position shotPosition;
    //aggiungere costruttore

    @Override
    public List<ShotResult> execute(final Position target, final Grid grid) {
        ShotResult result = grid.receiveShot(target); //deve diventare weatherManager.receiveShot(target) e va usato grid.receiveShot(target) in weatherManager
        return List.of(result);
    }
}
