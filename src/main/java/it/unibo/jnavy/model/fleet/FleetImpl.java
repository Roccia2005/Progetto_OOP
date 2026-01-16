package it.unibo.jnavy.model.fleet;

import java.util.ArrayList;
import java.util.List;

import it.unibo.jnavy.model.ship.Ship;

/**
 * Concrete implementation of the Fleet interface.
 */
public class FleetImpl implements Fleet {

    private List<Ship> ships;

    public FleetImpl() {
        this.ships = new ArrayList<>();
    }

    @Override
    public void addShip(Ship s) {
        this.ships.add(s);
    }

    @Override
    public boolean isDefeated() {
        return this.ships.stream().allMatch(Ship::isSunk);
    }

    //per adesso non lo implementiamo
    @Override
    public int getShipsAlive() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getShipsAlive'");
    }

    @Override
    public List<Ship> getShips() {
        return new ArrayList<>(this.ships);
    }
    
}
