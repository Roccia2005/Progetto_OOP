package it.unibo.jnavy.model.fleet;

import java.util.ArrayList;
import java.util.List;

import it.unibo.jnavy.model.ship.Ship;

/**
 * Concrete implementation of the Fleet interface.
 */
public class FleetImpl implements Fleet {

    final private List<Ship> ships;
    private static final int MAX_SHIPS = 5;

    public FleetImpl() {
        this.ships = new ArrayList<>();
    }

    @Override
    public void addShip(Ship s) {
        if (this.ships.size() >= MAX_SHIPS) {
            throw new IllegalStateException("Fleet is full! Max " + MAX_SHIPS + " ships allowed.");
        }
        long currentCountOfThisSize = ships.stream()
                                       .filter(ship -> ship.getSize() == s.getSize())
                                       .count();
        
        int allowedMax = switch (s.getSize()) {
            case 2 -> 1;
            case 3 -> 2;
            case 4 -> 1;
            case 5 -> 1;
            default -> 0;
        };

        if (currentCountOfThisSize >= allowedMax) {
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
        long size2 = ships.stream().filter(s -> s.getSize() == 2).count();
        long size3 = ships.stream().filter(s -> s.getSize() == 3).count();
        long size4 = ships.stream().filter(s -> s.getSize() == 4).count();
        long size5 = ships.stream().filter(s -> s.getSize() == 5).count();

        return size2 == 1 && size3 == 2 && size4 == 1 && size5 == 1;
    }

    @Override
    public void removeShip(Ship ship) {
        this.ships.remove(ship);
    }

}
