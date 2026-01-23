package it.unibo.jnavy.model.bots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unibo.jnavy.model.HitType;
import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.CardinalDirection;

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

    @Override
    public Position selectTarget(Grid enemyGrid) {

        Position nextTarget = null;
        Position temporaryTarget = null;

        switch (currentState) {
            case HUNTING:
                nextTarget = super.getRandomValidPosition(enemyGrid);
            break;

            case SEEKING:
                while(nextTarget == null || !availableDirections.isEmpty()){
                    currentDirection = availableDirections.getFirst();
                    temporaryTarget = targetCalc(firstHitPosition);
                    if (!isTargetValid(temporaryTarget, enemyGrid)) {
                        availableDirections.removeFirst();
                    } else {
                        nextTarget = temporaryTarget;
                    }

                }

                //se il case esce dal ciclo arriva qui allora availabledirections è vuota
                if (availableDirections.isEmpty() || nextTarget == null) {
                    currentState = State.HUNTING;
                }

            break;

            case DESTROYING:
                nextTarget = targetCalc(lastTargetPosition);
                if (next)

            break;
        }

        this.lastTargetPosition = nextTarget;
        return nextTarget;
    }

    @Override
    public void lastShotFeedback(Position target, HitType result) {

        switch (result) {
            case SUNK:
                currentState = State.HUNTING;
                resetAvailableDirections();
                firstHitPosition = null;
                return;

            case HIT:
                switch (currentState) {
                    case HUNTING:
                        currentState = State.SEEKING;
                        firstHitPosition = target;
                        resetAvailableDirections();
                        break;
                    case SEEKING:
                        currentState = State.DESTROYING;
                        break;
                    case DESTROYING:
                        lastTargetPosition = target;
                        break;
                }

            case MISS:
                if (currentState == State.DESTROYING) {
                    currentDirection = currentDirection.opposite();
                }
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
        return !grid.getCell(target)
        .map(c -> !c.isHit())
        .orElse(false)
        && x >= 0
        && x < matrix.length
        && y >= 0
        && y < matrix[0].length;
    }
}