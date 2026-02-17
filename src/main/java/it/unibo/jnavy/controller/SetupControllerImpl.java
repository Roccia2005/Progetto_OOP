package it.unibo.jnavy.controller;

import java.util.*;

import it.unibo.jnavy.model.Bot;
import it.unibo.jnavy.model.Human;
import it.unibo.jnavy.model.Player;
import it.unibo.jnavy.model.bots.BeginnerBot;
import it.unibo.jnavy.model.bots.BotStrategy;
import it.unibo.jnavy.model.captains.Captain;
import it.unibo.jnavy.model.captains.Gunner;
import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.fleet.Fleet;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.ship.ShipImpl;
import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.view.CapSelectionPanel.CaptainAbility;

/**
 * Implementation of SetupController interface.
 */
public class SetupControllerImpl implements SetupController {

    private final List<Integer> shipsToPlace;
    private final Random random;
    private final Player human;
    private final Player bot;

    /**
     * The ship object currently placed on the grid but NOT yet confirmed.
     * Needed to remove it if the user moves it to another position.
     */
    private Ship currentShipObject;

    public SetupControllerImpl(final Captain selectedCaptain, final BotStrategy selectedBotStrategy) {
        this.shipsToPlace = new ArrayList<>(buildFleetConfig());
        this.random = new Random();

        this.human = new Human(selectedCaptain);
        this.bot = new Bot(selectedBotStrategy);
        this.placeFleetRandomly(this.bot, new ArrayList<>(buildFleetConfig()));
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

//    @Override
//    public Player getHumanPlayer() {
//        return this.human;
//    }
//
//    @Override
//    public Player getBotPlayer() {
//        return this.bot;
//    }

    @Override
    public CellState getCellState(Position pos) {
        Grid grid = human.getGrid();
        var cellOpt = grid.getCell(pos);
        if (cellOpt.isEmpty() || cellOpt.get().getShip().isEmpty()) {
            return CellState.water();
        }
        Ship ship = cellOpt.get().getShip().get();
        int shipId = ship.hashCode(); // or a proper ID
        return new CellState(
                true,
                shipId,
                hasSameShip(grid, ship, new Position(pos.x() - 1, pos.y())),
                hasSameShip(grid, ship, new Position(pos.x() + 1, pos.y())),
                hasSameShip(grid, ship, new Position(pos.x(), pos.y() - 1)),
                hasSameShip(grid, ship, new Position(pos.x(), pos.y() + 1))
        );
    }

    private static List<Integer> buildFleetConfig() {
        return Fleet.FLEET_COMPOSITION.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByKey().reversed()) // largest first
                .flatMap(e -> Collections.nCopies(e.getValue(), e.getKey()).stream())
                .toList();
    }

    private boolean hasSameShip(Grid grid, Ship ship, Position neighbor) {
        if (!grid.isPositionValid(neighbor)) return false;
        return grid.getCell(neighbor)
                .flatMap(Cell::getShip)
                .map(s -> s.equals(ship))
                .orElse(false);
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