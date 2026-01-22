package it.unibo.jnavy.model.bots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unibo.jnavy.model.HitType;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.CardinalDirection;

public class ProBot extends AbstractBotStrategy{

    public enum State{
        HUNTING,
        SEEKING,
        DESTROYING;
    }

    private State currentState = HUNTING;
    private Position firstHitPosition;
    private Position lastTargetPosition;
    private List<CardinalDirection> availableDirections = new ArrayList<>();
    private CardinalDirection currentDirection = null;

    @Override
    public Position selectTarget(Grid enemyGrid) {

        Position nextTarget = null;

        switch (currentState) {
            case HUNTING:
                nextTarget = super.getRandomValidPosition(enemyGrid);
            break;

            case SEEKING:
                while(nextTarget == null){
                    currentDirection = availableDirections.getFirst();
                    temporaryTarget = firstHitPosition + currentDirection;
                }
            break;

            case DESTROYING:

            break;
        }

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
                    case SEEKING:
                        currentState = State.DESTROYING;
                        currentDirection = //metodo per calcolare direction!!!
                    case DESTROYING:
                        lastTargetPosition = target;
                }

            case MISS:
                if (currentState == State.DESTROYING) {
                    currentDirection = currentDirection.opposite();
                }
        }

    }

    public void resetAvailableDirections() {
        this.availableDirections.clear();
        this.availableDirections.addAll(Arrays.asList(CardinalDirection.values()));
    }
}

    /*
        flusso del probot:
            - sparo random finchè non prendo una cella con nave sopra (salva posizione come FIRSTHITPOSITION)
            - sparo alla adiacente up (se valida), miss1 = sparo alla adiacente destra (se valida), miss2 = sparo alla adiacente sotto (se valida), miss3 = sparo alla adiacente a sinistra (per forza sarà valida) ---> si avrà hit = (salva direction)
            - sparo successivo nella cella adiacente in quella specifica DIRECTION
            - hit = continuo a sparare alla adiacente in quella specifica DIRECTION | miss = DIRECTION = INVERTIDIRECTION
            - sparo alla adiacente della FIRSTHITPOSITION in quella specifica nuova DIRECTION
            - hit = continuo | miss = nave affondata
            - ricomincia il flusso!
    */