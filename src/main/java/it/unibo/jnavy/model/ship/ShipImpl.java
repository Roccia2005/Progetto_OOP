package it.unibo.jnavy.model.ship;

/**
 * Concrete implementation of the Ship interface.
 */
public class ShipImpl implements Ship {

    private final int size;
    private int health;

    private static final int MIN_SIZE = 2;
    private static final int MAX_SIZE = 5;

    @java.io.Serial
    private static final long serialVersionUID = 1L;
    /**
     * Creates a new Ship with the specified size.
     * @param size the length of the ship. Must be at least 2.
     * @throws IllegalArgumentException if size is less than 2.
     */
    public ShipImpl(final int size) {
        if (size < MIN_SIZE || size > MAX_SIZE) {
            throw new IllegalArgumentException("Ship size must be between " + MIN_SIZE + " and " + MAX_SIZE);
        }
        this.size = size;
        this.health = size;
    }

    @Override
    public boolean hit() {
        if (isSunk()) {
            throw new IllegalStateException("Cannot hit a ship that is already sunk.");
        }
        this.health--;
        return isSunk();
    }

    @Override
    public boolean isSunk() {
        return this.health <= 0;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public int getHealth() {
        return this.health;
    }

    @Override
    public boolean repair() {
        if (this.health < this.size && !isSunk()) {
            this.health++;
            return true;
        }
        return false;
    }
}
