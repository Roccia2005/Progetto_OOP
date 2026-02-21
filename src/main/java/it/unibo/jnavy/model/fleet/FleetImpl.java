package it.unibo.jnavy.model.fleet;

import java.util.ArrayList;
import java.util.List;

import it.unibo.jnavy.model.ship.Ship;

/**
 * Concrete implementation of the Fleet interface.
 */
public final class FleetImpl implements Fleet {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final List<Ship> ships;

    public FleetImpl() {
        this.ships = new ArrayList<>();
    }

    @Override
    public void addShip(final Ship s) {
        final int allowedMax = FLEET_COMPOSITION.getOrDefault(s.getSize(), 0);
        if (allowedMax == 0) {
            throw new IllegalArgumentException("Ship of size " + s.getSize() + " is not allowed.");
        }

        final long currentCount = ships.stream()
                .filter(ship -> ship.getSize() == s.getSize())
                .count();
        if (currentCount >= allowedMax) {
            throw new IllegalStateException("Cannot add more ships of size " + s.getSize());
        }
        this.ships.add(s);
    }

    @Override
    public boolean isDefeated() {
        return !this.ships.isEmpty() && this.ships.stream().allMatch(Ship::isSunk);
    }

    @Override
    public List<Ship> getShips() {
        return new ArrayList<>(this.ships);
    }

    @Override
    public boolean isTopologyValid() {
        return FLEET_COMPOSITION.entrySet().stream().allMatch(entry -> {
            final long actual = ships.stream().filter(s -> s.getSize() == entry.getKey()).count();
            return actual == entry.getValue();
        });
    }

    @Override
    public void removeShip(final Ship ship) {
        this.ships.remove(ship);
    }

}
