package it.unibo.jnavy.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.jnavy.model.Bot;
import it.unibo.jnavy.model.Human;
import it.unibo.jnavy.model.Player;
import it.unibo.jnavy.model.bots.BeginnerBot;
import it.unibo.jnavy.model.captains.Gunner;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.ship.ShipImpl;
import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Implementation of SetupController interface.
 */
public class SetupControllerImpl implements SetupController {

    private static final List<Integer> FLEET_CONFIG = List.of(5, 4, 3, 3, 2);

    private final List<Integer> shipsToPlace;
    private final Random random;
    private final Human human;
    private final Bot bot;

    /**
     * The ship object currently placed on the grid but NOT yet confirmed.
     * Needed to remove it if the user moves it to another position.
     */
    private Ship currentShipObject;

    public SetupControllerImpl() {
        this.shipsToPlace = new ArrayList<>(FLEET_CONFIG);
        this.random = new Random();
        
        // TODO: The Captain and BotStrategy could be passed as arguments or set later.
        this.human = new Human(new Gunner());
        this.bot = new Bot(new BeginnerBot());

        this.placeFleetRandomly(this.bot, new ArrayList<>(FLEET_CONFIG));
    }

    @Override
    public boolean setShip(final Position pos, final CardinalDirection dir) {
        if (shipsToPlace.isEmpty()) {
            return false;
        }

        final Grid grid = human.getGrid();
        
        if (this.currentShipObject != null) {
            grid.removeShip(this.currentShipObject);
        }

        final Ship newShip = new ShipImpl(shipsToPlace.getFirst());

        if (grid.isPlacementValid(newShip, pos, dir)) {
            grid.placeShip(newShip, pos, dir);
            this.currentShipObject = newShip;
            return true;
        } 
        
        this.currentShipObject = null;
        return false;
    }

    @Override
    public void nextShip() {
        if (this.currentShipObject == null) {
            throw new IllegalStateException("Cannot confirm: no valid ship is currently placed.");
        }
        
        shipsToPlace.removeFirst();
        this.currentShipObject = null;
    }

    @Override
    public void randomizeHumanShips() {
    
        this.unsetShip();
        
        if (!shipsToPlace.isEmpty()) {
            placeFleetRandomly(this.human, this.shipsToPlace);
            this.shipsToPlace.clear();
        }
    }

    @Override
    public int getNextShipSize() {
        return shipsToPlace.isEmpty() ? 0 : shipsToPlace.getFirst();
    }

    @Override
    public boolean isSetupFinished() {
        return shipsToPlace.isEmpty() && currentShipObject == null;
    }

    @Override
    public Human getHumanPlayer() {
        return this.human;
    }

    @Override
    public Bot getBotPlayer() {
        return this.bot;
    }

    private void unsetShip() {
        if (this.currentShipObject != null) {
            human.getGrid().removeShip(this.currentShipObject);
            this.currentShipObject = null;
        }
    }

    /**
     * Internal helper to place a list of ships randomly on a player's grid.
     * * @param player The player target.
     * @param shipsToInsert The list of ship sizes to place.
     */
    private void placeFleetRandomly(final Player player, final List<Integer> shipsToInsert) {
        final Grid grid = player.getGrid();
        
        for (final int size : shipsToInsert) {
            boolean placed = false;
            while (!placed) {
                final Ship ship = new ShipImpl(size);
                final Position pos = new Position(random.nextInt(grid.getSize()), random.nextInt(grid.getSize()));

                final CardinalDirection[] directions = CardinalDirection.values();
                final CardinalDirection dir = directions[random.nextInt(directions.length)];

                if (grid.isPlacementValid(ship, pos, dir)) {
                    grid.placeShip(ship, pos, dir);
                    placed = true;
                }
            }
        }
    }
}