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

    @Override
    public boolean placeCurrentShip(Position pos, CardinalDirection dir) {
        // Controllo consistenza fase
        if (this.currentPhase != Phase.SETUP || shipsToPlace.isEmpty()) {
            return false;
        }

        Ship ship = new ShipImpl(shipsToPlace.get(0));
        Grid grid = human.getGrid();

        // Verifica validità nel Model
        if (grid.isPlacementValid(ship, pos, dir)) {
            // Modifica stato Model
            grid.placeShip(ship, pos, dir);
            
            // Aggiorna stato Controller
            shipsToPlace.remove(0);
            
            checkSetupPhaseEnd();
            return true;
        }
        
        return false;
    }

    @Override
    public void randomizeHumanShips() {
        if (this.currentPhase != Phase.SETUP) return;
        
        // Se l'utente aveva già piazzato parzialmente delle navi, potresti voler pulire la griglia qui.
        // Per ora assumiamo che randomize si usi su griglia vuota o che aggiunga al resto.
        
        placeFleetRandomly(this.human);
        shipsToPlace.clear(); // Lista svuotata, setup finito
        checkSetupPhaseEnd();
    }

    @Override
    public int getNextShipSize() {
        return shipsToPlace.isEmpty() ? 0 : shipsToPlace.get(0);
    }

    /**
     * Automatic placement method.
    */
    private void placeFleetRandomly (Player player) {
        Grid grid = player.getGrid();
        
        List<Integer> shipSizes = List.copyOf(shipsToPlace);

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
