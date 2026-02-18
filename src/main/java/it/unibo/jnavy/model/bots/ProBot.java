package it.unibo.jnavy.model.bots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unibo.jnavy.model.HitType;
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
                nextTarget = getRandomValidPosition(enemyGrid);
            break;

            case SEEKING:
                if (this.availableDirections.isEmpty() && this.firstHitPosition != null) {
                    resetAvailableDirections();
                }

                while(nextTarget == null && !availableDirections.isEmpty()){
                    currentDirection = availableDirections.getFirst();
                    temporaryTarget = targetCalc(firstHitPosition);
                    if (!enemyGrid.isTargetValid(temporaryTarget)) {
                        // al momento mi trovo meglio usando una lista in cui rimuovo le direction non valide
                        this.availableDirections.removeFirst();
                    } else {
                        nextTarget = temporaryTarget;
                        break;
                    }

                }

                if (nextTarget == null) {
                    if (this.firstHitPosition != null) {
                        resetAvailableDirections();
                        this.currentState = State.HUNTING;
                        nextTarget = getRandomValidPosition(enemyGrid);
                    } else {
                        this.currentState = State.HUNTING;
                        nextTarget = getRandomValidPosition(enemyGrid);
                    }
                }

            break;

            case DESTROYING:
                temporaryTarget = targetCalc(lastTargetPosition);
                if (!enemyGrid.isTargetValid(temporaryTarget)) {
                    this.currentDirection = this.currentDirection.opposite();
                    nextTarget = targetCalc(firstHitPosition);

                    if (!enemyGrid.isTargetValid(nextTarget)) {
                         this.currentState = State.SEEKING;
                         resetAvailableDirections();
                         return this.selectTarget(enemyGrid);
                    } else {
                        lastTargetPosition = firstHitPosition;
                    }
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
                this.lastTargetPosition = null;
                this.currentDirection = null;
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
                        this.lastTargetPosition = target;

                        if (this.currentDirection == null && this.firstHitPosition != null) {
                            this.currentDirection = findDirection(this.firstHitPosition, target);
                        }
                        break;
                    case DESTROYING:
                        this.lastTargetPosition = target;
                        break;
                }
            break;

            case MISS:
                if (this.currentState == State.DESTROYING) {
                    if (this.currentDirection != null) {
                        this.currentDirection = this.currentDirection.opposite();
                    }
                    this.lastTargetPosition = firstHitPosition;
                } else if (this.currentState == State.SEEKING) {
                    if (!this.availableDirections.isEmpty()) {
                        this.availableDirections.removeFirst();
                    }
                }
            break;

            case INVALID:
            default:
                // uso default vuoto per non far imparare nulla al bot in questi casi che non mi servono a molto
            break;
        }

    }

    // metodo per capire la direzione tra due celle data la precedente(p1) e quella attuale(p2)uso per i test
    private CardinalDirection findDirection(final Position p1, final Position p2) {
        for (CardinalDirection dir : CardinalDirection.values()) {
            if (p1.x() + dir.getRowOffset() == p2.x() &&
            p1.y() + dir.getColOffset() == p2.y()) {
                return dir;
            }
        }
        return null;
    }

    // funzione per resettare l'array delle direzioni possibili
    private void resetAvailableDirections() {
        this.availableDirections.clear();
        this.availableDirections.addAll(Arrays.asList(CardinalDirection.values()));
    }

    // posizione calcolata aggiungendo alla x e alla y gli offset della direzione corrispondente
    //non deve mai essere null!!!
    public Position targetCalc(final Position target) {
        if (this.currentDirection == null) {
            return target;
        }
        return new Position(target.x()+currentDirection.getRowOffset(), target.y()+currentDirection.getColOffset());
    }

}