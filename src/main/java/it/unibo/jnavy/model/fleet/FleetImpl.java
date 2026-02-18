package it.unibo.jnavy.model.fleet;

import java.util.ArrayList;
import java.util.List;

import it.unibo.jnavy.model.ship.Ship;

/**
 * Concrete implementation of the Fleet interface.
 */
public class FleetImpl implements Fleet {

    private final List<Ship> ships;

    public FleetImpl() {
        this.ships = new ArrayList<>();
    }

    @Override
    public void addShip(Ship s) {
        int allowedMax = Fleet.FLEET_COMPOSITION.getOrDefault(s.getSize(), 0);
        if (allowedMax == 0) {
            throw new IllegalArgumentException("Ship of size " + s.getSize() + " is not allowed.");
        }

        long currentCount = ships.stream()
                .filter(ship -> ship.getSize() == s.getSize())
                .count();
        if (currentCount >= allowedMax) {
            throw new IllegalStateException("Cannot add more ships of size " + s.getSize());
        }
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

    @Override
    public boolean isTopologyValid() {
        return Fleet.FLEET_COMPOSITION.entrySet().stream().allMatch(entry -> {
            long actual = ships.stream().filter(s -> s.getSize() == entry.getKey()).count();
            return actual == entry.getValue();
        });
    }

    @Override
    public void removeShip(Ship ship) {
        this.ships.remove(ship);
    }

}
