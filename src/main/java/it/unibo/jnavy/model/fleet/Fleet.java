package it.unibo.jnavy.model.fleet;

import java.util.List;
import java.util.Map;

import it.unibo.jnavy.model.ship.Ship;

/**
 * Maneges a collection of ships for a player.
 * It provides methods to check the overall status of the players's fleet.
 */
public interface Fleet {

    /**
     * The standard fleet composition: ship size -> allowed count.
     */
    Map<Integer, Integer> FLEET_COMPOSITION = Map.of(
            5, 1,
            4, 1,
            3, 2,
            2, 1
    );
    
    /**
     * Adds a ship to the fleet.
     * @param s the ship to add.
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

    /**
     * Checks if the fleet composition matches the game rules:
     * 2x size 2, 1x size 3, 1x size 4, 1x size 5.
     * @return true if the fleet is complete and valid.
     */
    boolean isTopologyValid();

    /**
     * remove the given ship.
     * @param ship to remove.
     */
    void removeShip(Ship ship);
} 