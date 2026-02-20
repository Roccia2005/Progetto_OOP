package it.unibo.jnavy.model.bots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.HitType;

public class ProBot extends AbstractBotStrategy{

    public enum State{
        HUNTING,
        SEEKING,
        DESTROYING
    }

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private State currentState = State.HUNTING;
    private Position firstHitPosition;
    private Position lastTargetPosition;
    private final List<CardinalDirection> availableDirections = new ArrayList<>();
    private CardinalDirection currentDirection = null;

    // uso questo metodo per calcolare le effettive posizioni ovvero i target da restituire e a cui sparare
    @Override
    public Position selectTarget(final Grid enemyGrid) {

        return switch (currentState) {
            case SEEKING -> handleSeeking(enemyGrid);
            case DESTROYING -> handleDestroying(enemyGrid);
            default -> handleHunting(enemyGrid);
        };
    }

    private Position handleHunting(final Grid enemyGrid) {
        return getRandomValidPosition(enemyGrid);
    }

    private Position handleSeeking(final Grid enemyGrid) {
        if (this.availableDirections.isEmpty() && this.firstHitPosition != null) {
            resetAvailableDirections();
        }
        while(!this.availableDirections.isEmpty()){
            currentDirection = availableDirections.getFirst();
            Position target = targetCalc(firstHitPosition);
            if (enemyGrid.isTargetValid(target)) {
                return target;
            }
            this.availableDirections.removeFirst();
        }

        resetAvailableDirections();
        this.currentState = State.HUNTING;
        return getRandomValidPosition(enemyGrid);
    }

    private Position handleDestroying(final Grid enemyGrid) {
        Position target = targetCalc(lastTargetPosition);
        if (!enemyGrid.isTargetValid(target)) {
            this.currentDirection = this.currentDirection.opposite();
            Position secondTarget = targetCalc(firstHitPosition);

            if (!enemyGrid.isTargetValid(secondTarget)) {
                this.currentState = State.SEEKING;
                resetAvailableDirections();
                return this.selectTarget(enemyGrid);
            } else {
                this.lastTargetPosition = this.firstHitPosition;
                return secondTarget;
            }
        }
        return target;
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
                handleHit(target);
            break;

            case MISS:
                handleMiss();
            break;

            case INVALID:
            default:
                // uso default vuoto per non far imparare nulla al bot in questi casi che non mi servono a molto
            break;
        }

    }

    private void handleHit(final Position target) {
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
    }

    private void handleMiss() {
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
    public Position targetCalc(final Position target) {
        if (this.currentDirection == null) {
            return target;
        }
        return new Position(target.x()+currentDirection.getRowOffset(), target.y()+currentDirection.getColOffset());
    }

    @Override
    protected String getStrategyName() {
        return "Pro";
    }

}