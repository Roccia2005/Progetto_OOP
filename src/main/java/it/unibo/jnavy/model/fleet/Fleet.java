package it.unibo.jnavy.model.fleet;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import it.unibo.jnavy.model.ship.Ship;

/**
 * Manages a collection of ships for a player.
 * It provides methods to check the overall status of the players' fleet.
 */
public interface Fleet extends Serializable {

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
     * Returns a copy of the list of ships.
     * @return a new list containing the ships.
     */
    List<Ship> getShips();

    /**
     * Checks if the fleet composition matches the game rules:
     * 1x size 2, 2x size 3, 1x size 4, 1x size 5.
     * @return true if the fleet is complete and valid.
     */
    boolean isTopologyValid();

    /**
     * remove the given ship.
     * @param ship to remove.
     */
    void removeShip(Ship ship);
} 