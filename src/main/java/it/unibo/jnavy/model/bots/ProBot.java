package it.unibo.jnavy.model.bots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unibo.jnavy.model.HitType;
import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.CardinalDirection;


/**
 * nel controller avverrà questo:
 * - INIZIO TURNO BOT!
 * - bot.decideTarget(enemyGrid) ---> viene generata uno "sparo" in una certa position in base a selectTarget(enemyGrid)
 * - ShotResult risultato = humanGrid.receiveShot("sparo") ---> viene inserito l'esito dello shot sulla vera griglia nemica in risultato
 * - bot.lastShotFeedback(risultato) ---> il bot impara da questo risultato cambiando le varie modalità in base ad esso
 * - FINE TURNO BOT!
 */
public class ProBot extends AbstractBotStrategy{

    public enum State{
        HUNTING,
        SEEKING,
        DESTROYING;
    }

    private State currentState = State.HUNTING;
    private Position firstHitPosition;
    private Position lastTargetPosition;
    private List<CardinalDirection> availableDirections = new ArrayList<>();
    private CardinalDirection currentDirection = null;

    // uso questo metodo per calcolare le effettive posizioni ovvero i target da restituire e a cui sparare
    @Override
    public Position selectTarget(final Grid enemyGrid) {

        Position nextTarget = null;
        Position temporaryTarget = null;

        switch (currentState) {
            case HUNTING:
                nextTarget = super.getRandomValidPosition(enemyGrid);
            break;

            case SEEKING:
                while(nextTarget == null && !availableDirections.isEmpty()){
                    currentDirection = availableDirections.getFirst();
                    temporaryTarget = targetCalc(firstHitPosition);
                    if (!isTargetValid(temporaryTarget, enemyGrid)) {
                        // al momento mi trovo meglio usando una lista in cui rimuovo le direction non valide
                        this.availableDirections.removeFirst();
                    } else {
                        nextTarget = temporaryTarget;
                        break;
                    }

                }

                if (availableDirections.isEmpty() || nextTarget == null) {
                    currentState = State.HUNTING;
                    nextTarget = super.getRandomValidPosition(enemyGrid);
                }

            break;

            case DESTROYING:
                temporaryTarget = targetCalc(lastTargetPosition);
                if (!isTargetValid(temporaryTarget, enemyGrid)) {
                    this.currentDirection = this.currentDirection.opposite();
                    nextTarget = targetCalc(firstHitPosition);
                    lastTargetPosition = firstHitPosition;
                } else {
                    nextTarget = temporaryTarget;
                }

            break;
        }

        this.lastTargetPosition = nextTarget;
        return nextTarget;
    }

    //uso questo metodo per far imparare al bot, viene chiamato dopo che si conosce il risultato del colpo sulla grid dell'enemy
    @Override
    public void lastShotFeedback(final Position target, final HitType result) {

        switch (result) {
            case SUNK:
                this.currentState = State.HUNTING;
                resetAvailableDirections();
                this.firstHitPosition = null;
                return;

            case HIT:
                switch (this.currentState) {
                    case HUNTING:
                        this.currentState = State.SEEKING;
                        this.firstHitPosition = target;
                        resetAvailableDirections();
                        break;
                    case SEEKING:
                        this.currentState = State.DESTROYING;
                        break;
                    case DESTROYING:
                        this.lastTargetPosition = target;
                        break;
                }
                break;

            case MISS:
                if (this.currentState == State.DESTROYING) {
                    this.currentDirection = this.currentDirection.opposite();
                    this.lastTargetPosition = firstHitPosition;
                }
            break;

            case ALREADY_HIT:
            case INVALID:
            default:
                // uso default vuoto per non far imparare nulla al bot in questi casi che non mi servono a molto
            break;
        }

    }

    // funzione per resettare l'array delle direzioni possibili
    public void resetAvailableDirections() {
        this.availableDirections.clear();
        this.availableDirections.addAll(Arrays.asList(CardinalDirection.values()));
    }

    // posizione calcolata aggiungendo alla x e alla y gli offset della direzione corrispondente
    public Position targetCalc(final Position target) {
        return new Position(target.x()+currentDirection.getRowOffset(), target.y()+currentDirection.getColOffset());
    }

    //estraggo lo status della cell dall'optional e verifico se è valido o meno + verifico se è in bounds
    public boolean isTargetValid(final Position target, final Grid grid) {
        Cell[][] matrix = grid.getCellMatrix();
        int x = target.x();
        int y = target.y();
        return x >= 0
        && x < matrix.length
        && y >= 0
        && y < matrix[0].length
        && grid.getCell(target)  //metto il controllo sulla cell in fondo così non rischio che venga controllato un index non valido sulla grid
        .map(c -> !c.isHit())
        .orElse(false);
    }
}