package it.unibo.jnavy.model.ship;

/**
 * Concrete implementation of the Ship interface.
 */
public class ShipImpl implements Ship {

    private final int size;
    private int health;

    /**
     * Creates a new Ship with the specified size.
     * @param size the length of the ship. Must be at least 1.
     * @throws IllegalArgumentException if size is less than 1.
     */
    public ShipImpl(final int size) {
        if (size < 2) {
            throw new IllegalArgumentException("Ship size must be at least 2");
        }
        this.size = size;
        this.health = size;
    }

    @Override
    public boolean hit() {
        if(!isSunk()) this.health--;
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
    public void setHealth(int value) {
        this.health = value;
    }
}
