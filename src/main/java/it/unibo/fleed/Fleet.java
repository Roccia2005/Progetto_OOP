package it.unibo.fleed;

import java.util.List;

import it.unibo.jnavy.model.ship.Ship;

/**
 * Maneges a collection of ships for a player.
 * It provides methods to check the overall status of the players's fleet.
 */
public interface Fleet {
    
    /**
     * Adds a ship to the fleet.
     * @param ship the ship to add.
     */
    void addShip(Ship s);

    /**
     * Checks if all ships in the fleet are destroyed.
     * @return true if all ships are sunk, false otherwise.
     */
    boolean isDefeated();

    /**
     * Counts the number of ships that are still afloat.
     * @return the number of active ships.
     */
    int getShipsAlive(); 

    /**
     * Returns a copy of the list of ships.
     * @return a new list containing the ships.
     */
    List<Ship> getShips();
} 