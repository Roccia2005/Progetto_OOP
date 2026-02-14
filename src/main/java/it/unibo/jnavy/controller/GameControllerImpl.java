package it.unibo.jnavy.controller;

import java.util.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import it.unibo.jnavy.model.utilities.CardinalDirection;

import it.unibo.jnavy.model.Bot;
import it.unibo.jnavy.model.Human;
import it.unibo.jnavy.model.Player;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.fleet.Fleet;
import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.ship.ShipImpl;
import it.unibo.jnavy.model.ShotResult;

import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherManager;
import it.unibo.jnavy.model.weather.WeatherManagerImpl;

public class GameControllerImpl implements GameController{

    private Player human;
    private Player bot;
    private Player currentPlayer;
    private WeatherManager weather;
    private Phase currentPhase;

    private final List<Integer> shipsToPlace = new ArrayList<>(Arrays.asList(5, 4, 3, 3, 2));
    private CardinalDirection currentOrientation = CardinalDirection.RIGHT;
    private Ship currentTempShip = null;
    private final Random random = new Random();

    private int turnCounter = 0;

    public GameControllerImpl() {
        this.human = new Human(null);
        this.bot = new Bot(null);
        this.currentPlayer = this.human;
        this.weather = WeatherManagerImpl.getInstance();
        
        this.currentPhase = Phase.SETUP;

        placeFleetRandomly(this.bot);
    }

    /**
     * Algoritmo che riempie casualmente la griglia di un giocatore.
     * Usato sia per il Bot (all'inizio) sia per l'Umano (se sceglie "Random").
     */
    private void placeFleetRandomly (Player player) {
        Grid grid = player.getGrid();
        Fleet fleet = player.getFleet();
        
        int[] shipSizes = {5, 4, 3, 3, 2};

        for (int size : shipSizes) {
            boolean placed = false;
            
            // Continua a provare finché non trova una posizione valida
            while (!placed) {
                Ship ship = new ShipImpl(size);
                
                // Genera coordinate random (Griglia 10x10)
                int row = random.nextInt(10);
                int col = random.nextInt(10);
                Position pos = new Position(row, col);

                // Genera direzione random
                CardinalDirection[] directions = CardinalDirection.values();
                CardinalDirection dir = directions[random.nextInt(directions.length)];

                // Se la posizione è valida, piazza la nave
                if (grid.isPlacementValid(ship, pos, dir)) {
                    grid.placeShip(ship, pos, dir);
                    fleet.addShip(ship);
                    placed = true;
                }
            }
        }
    }

    private void checkSetupPhaseEnd() {
        if (human.getFleet().isTopologyValid()) {
            this.currentPhase = Phase.PLAY;
        }
    }

    @Override
    public void startGame() {
        this.currentPhase = Phase.SETUP;
    }

    

    @Override
    public void processShot(Position p) {
        if (!isHumanTurn()) {
            return;
        }
        ShotResult result = this.weather.applyWeatherEffects(p, this.bot.getGrid());
        endTurn();
    }

    @Override
    public int endTurn() {
        this.human.processTurnEnd();
        this.bot.processTurnEnd();
        this.weather.processTurnEnd();
        this.turnCounter++;
        this.currentPlayer = this.currentPlayer == this.human ? this.bot : this.human;
        if (this.currentPlayer == this.bot) {
            playBotTurn();
        }
        return this.turnCounter;
    }

    private boolean isHumanTurn() {
        return this.currentPlayer == this.human;
    }

    private void playBotTurn() {
        return;
    }
}
