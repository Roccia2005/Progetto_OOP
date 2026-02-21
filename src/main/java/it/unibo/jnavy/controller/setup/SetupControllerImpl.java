package it.unibo.jnavy.controller.setup;

import it.unibo.jnavy.controller.utilities.CellState;
import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.fleet.Fleet;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.player.Player;
import it.unibo.jnavy.model.player.Bot;
import it.unibo.jnavy.model.player.Human;
import it.unibo.jnavy.model.bots.BotStrategy;
import it.unibo.jnavy.model.captains.Captain;
import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.ship.ShipImpl;
import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Implementation of the {@link SetupController} interface.
 * This class manages the ship placement phase for both the human player and the bot,
 * ensuring that all game rules regarding ship positioning are respected.
 */
public final class SetupControllerImpl implements SetupController {

    private final List<Integer> shipsToPlace;
    private final Random random;
    private final Player human;
    private final Player bot;

    /**
     * The ship object currently placed on the grid but NOT yet confirmed.
     * Needed to remove it if the user moves it to another position.
     */
    private Ship currentShipObject;
    private Position currentShipPos;
    private CardinalDirection currentShipDir;

    /**
     * Constructs a new SetupControllerImpl with the chosen captain and bot strategy.
     * It initializes the human and bot players and automatically places the bot's fleet.
     *
     * @param selectedCaptain the captain selected by the human player.
     * @param selectedBotStrategy the difficulty strategy for the bot.
     */
    public SetupControllerImpl(final Captain selectedCaptain, final BotStrategy selectedBotStrategy) {
        this.shipsToPlace = new ArrayList<>(buildFleetConfig());
        this.random = new Random();
        this.human = new Human(selectedCaptain);
        this.bot = new Bot(selectedBotStrategy);
        this.placeFleetRandomly(this.bot, new ArrayList<>(buildFleetConfig()));
    }

    @Override
    public boolean tryPlaceShip(final Position pos, final CardinalDirection dir) {
        if (shipsToPlace.isEmpty()) {
            return false;
        }

        final Grid grid = human.getGrid();

        final Ship oldShip = this.currentShipObject;
        final Position oldPos = this.currentShipPos;
        final CardinalDirection oldDir = this.currentShipDir;

        if (oldShip != null) {
            grid.removeShip(oldShip);
        }

        final Ship newShip = new ShipImpl(shipsToPlace.getFirst());

        if (grid.isPlacementValid(newShip, pos, dir)) {
            grid.placeShip(newShip, pos, dir);
            this.currentShipObject = newShip;
            this.currentShipPos = pos;
            this.currentShipDir = dir;
            return true;
        }

        if (oldShip != null && oldPos != null && oldDir != null) {
            grid.placeShip(oldShip, oldPos, oldDir);
            this.currentShipObject = oldShip;
        } else {
            this.currentShipObject = null;
        }

        return false;
    }

    @Override
    public void nextShip() {
        if (this.currentShipObject == null) {
            throw new IllegalStateException("Cannot confirm: no valid ship is currently placed.");
        }

        shipsToPlace.removeFirst();
        this.currentShipObject = null;
        this.currentShipPos = null;
        this.currentShipDir = null;
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
    public Player getHumanPlayer() {
        return this.human;
    }

    @Override
    public Player getBotPlayer() {
        return this.bot;
    }

    @Override
    public CellState getCellState(final Position pos) {
        final Grid grid = human.getGrid();
        final var cellOpt = grid.getCell(pos);
        if (cellOpt.isEmpty() || cellOpt.get().getShip().isEmpty()) {
            return CellState.water();
        }
        final Ship ship = cellOpt.get().getShip().get();
        final int shipId = ship.hashCode();
        return new CellState(
                true,
                shipId,
                hasSameShip(grid, ship, new Position(pos.x() - 1, pos.y())),
                hasSameShip(grid, ship, new Position(pos.x() + 1, pos.y())),
                hasSameShip(grid, ship, new Position(pos.x(), pos.y() - 1)),
                hasSameShip(grid, ship, new Position(pos.x(), pos.y() + 1))
        );
    }

    @Override
    public void clearFleet() {

        this.unsetShip();

        final List<Ship> placedShips = new ArrayList<>(this.human.getGrid().getFleet().getShips());
        for (final Ship s : placedShips) {
            this.human.getGrid().removeShip(s);
        }

        this.shipsToPlace.clear();
        this.shipsToPlace.addAll(buildFleetConfig());
    }

    private static List<Integer> buildFleetConfig() {
        return Fleet.FLEET_COMPOSITION.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByKey().reversed()) // largest first
                .flatMap(e -> Collections.nCopies(e.getValue(), e.getKey()).stream())
                .toList();
    }

    private boolean hasSameShip(final Grid grid, final Ship ship, final Position neighbor) {
        return grid.isPositionValid(neighbor) && grid.getCell(neighbor)
                .flatMap(Cell::getShip)
                .map(s -> s.equals(ship))
                .orElse(false);
    }

    private void unsetShip() {
        if (this.currentShipObject != null) {
            human.getGrid().removeShip(this.currentShipObject);
            this.currentShipObject = null;
            this.currentShipPos = null;
            this.currentShipDir = null;
        }
    }

    /**
     * Internal helper to place a list of ships randomly on a player's grid.
     *
     * @param player The player target.
     * @param shipsToInsert The list of ship sizes to place.
     */
    private void placeFleetRandomly(final Player player, final List<Integer> shipsToInsert) {
        final Grid grid = player.getGrid();

        for (final int size : shipsToInsert) {
            boolean placed = false;
            final Ship ship = new ShipImpl(size);
            while (!placed) {
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
