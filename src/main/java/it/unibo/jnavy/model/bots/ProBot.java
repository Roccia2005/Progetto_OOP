package it.unibo.jnavy.model.bots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.HitType;

/**
 * An advanced bot strategy that uses a state machine to hunt, seek, and destroy enemy ships.
 * It adapts its behavior based on the feedback received from previous shots.
 */
public final class ProBot extends AbstractBotStrategy {

    /**
     * Represents the possible states of the ProBot's targeting logic.
     */
    public enum State {
        /** Searching for a ship by shooting randomly. */
        HUNTING,
        /** Finding the orientation of a ship after an initial hit. */
        SEEKING,
        /** Systematically destroying a ship once its orientation is known. */
        DESTROYING
    }

    /**
     * Serial version UID for serialization.
     */
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private State currentState = State.HUNTING;
    private Position firstHitPosition;
    private Position lastTargetPosition;
    private final List<CardinalDirection> availableDirections = new ArrayList<>();
    private CardinalDirection currentDirection;

    /**
     * Selects the next target based on the current state of the bot.
     * @param enemyGrid the grid representing the opponent's territory
     * @return the calculated {@link Position} to target
     */
    @Override
    public Position selectTarget(final Grid enemyGrid) {

        return switch (currentState) {
            case SEEKING -> handleSeeking(enemyGrid);
            case DESTROYING -> handleDestroying(enemyGrid);
            default -> handleHunting(enemyGrid);
        };
    }

    /**
     * Handles the hunting state by selecting a random valid position.
     */
    private Position handleHunting(final Grid enemyGrid) {
        return getRandomValidPosition(enemyGrid);
    }

    /**
     * Handles the seeking state to determine the direction of a hit ship.
     */
    private Position handleSeeking(final Grid enemyGrid) {
        if (this.availableDirections.isEmpty() && this.firstHitPosition != null) {
            resetAvailableDirections();
        }
        while (!this.availableDirections.isEmpty()) {
            currentDirection = availableDirections.getFirst();
            final Position target = targetCalc(firstHitPosition);
            if (enemyGrid.isTargetValid(target)) {
                return target;
            }
            this.availableDirections.removeFirst();
        }

        resetAvailableDirections();
        this.currentState = State.HUNTING;
        return getRandomValidPosition(enemyGrid);
    }

    /**
     * Handles the destroying state by continuing to shoot in a specific direction.
     */
    private Position handleDestroying(final Grid enemyGrid) {
        final Position target = targetCalc(lastTargetPosition);
        if (!enemyGrid.isTargetValid(target)) {
            this.currentDirection = this.currentDirection.opposite();
            final Position secondTarget = targetCalc(firstHitPosition);

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

    /**
     * Updates the bot's internal state based on the result of the last shot.
     * @param target the position that was shot at
     * @param result the result of the shot (HIT, MISS, SUNK, etc.)
     */
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

            case NONE:
            default:
            break;
        }

    }

    /**
     * Updates logic when a shot results in a HIT.
     */
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

    /**
     * Updates logic when a shot results in a MISS.
     */
    private void handleMiss() {
        if (this.currentState == State.DESTROYING) {
            if (this.currentDirection != null) {
                this.currentDirection = this.currentDirection.opposite();
            }
            this.lastTargetPosition = firstHitPosition;
        } else if (this.currentState == State.SEEKING) {
            if (this.availableDirections.isEmpty()) {
                return;
            }
            this.availableDirections.removeFirst();
        }
    }

    /**
     * Calculates the cardinal direction between two positions.
     */
    private CardinalDirection findDirection(final Position p1, final Position p2) {
        for (final CardinalDirection dir : CardinalDirection.values()) {
            if (p1.x() + dir.getRowOffset() == p2.x()
                    && p1.y() + dir.getColOffset() == p2.y()) {
                return dir;
            }
        }
        return null;
    }

    /**
     * Resets the list of available cardinal directions to explore.
     */
    private void resetAvailableDirections() {
        this.availableDirections.clear();
        this.availableDirections.addAll(Arrays.asList(CardinalDirection.values()));
    }

    /**
     * Calculates a new position based on an initial target and the current direction.
     * @param target the starting position
     * @return the new {@link Position} shifted by the direction offsets
     */
    public Position targetCalc(final Position target) {
        if (this.currentDirection == null) {
            return target;
        }
        return new Position(target.x() + currentDirection.getRowOffset(), target.y() + currentDirection.getColOffset());
    }

    /**
     * {@inheritDoc}
     * @return the string "Pro"
     */
    @Override
    protected String getStrategyName() {
        return "Pro";
    }
}
