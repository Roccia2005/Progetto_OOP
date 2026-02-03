package it.unibo.jnavy.model;

import java.util.List;

import it.unibo.jnavy.model.captains.Captain;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.grid.GridImpl;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherManagerImpl;

public class Human implements Player, TurnObserver {

    private Captain captain;
    private Grid grid;

    public Human(final Captain captain) {
        this.grid = new GridImpl();
        this.captain = captain;
    }

    @Override
    public Grid getGrid() {
        return this.grid;
    }

    @Override
    public List<ShotResult> createShot(Position target, Grid grid) {
        return List.of(WeatherManagerImpl.getInstance().applyWeatherEffects(target, grid));
    }

    @Override
    public void processTurnEnd() {
        this.captain.processTurnEnd();
    }

    public boolean useAbility(Position target, Grid grid) {
        return this.captain.useAbility(grid, target);
    }
}