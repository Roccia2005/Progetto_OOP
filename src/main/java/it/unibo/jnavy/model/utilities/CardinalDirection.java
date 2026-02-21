package it.unibo.jnavy.model.utilities;

/**
 * Represents the four main cardinal directions used for grid navigation.
 * Each direction contains row and column offsets to easily calculate adjacent positions.
 */
public enum CardinalDirection {

    /**
     * Upward direction (decreases row index).
     */
    UP(-1, 0),

    /**
     * Rightward direction (increases column index).
     */
    RIGHT(0, 1),

    /**
     * Downward direction (increases row index).
     */
    DOWN(1, 0),

    /**
     * Leftward direction (decreases column index).
     */
    LEFT(0, -1);

    private final int rowOffset;
    private final int colOffset;

    /**
     * Constructs a CardinalDirection with specific offsets.
     *
     * @param rowOffset the offset to be applied to the row coordinate
     * @param colOffset the offset to be applied to the column coordinate
     */
    CardinalDirection(final int rowOffset, final int colOffset) {
        this.rowOffset = rowOffset;
        this.colOffset = colOffset;
    }

    /**
     * Returns the opposite direction of the current one.
     *
     * @return the opposite {@link CardinalDirection}
     * @throws IllegalStateException if an invalid direction is encountered
     */
    public CardinalDirection opposite() {
        return switch (this) {
            case UP -> DOWN;
            case RIGHT -> LEFT;
            case DOWN -> UP;
            case LEFT -> RIGHT;
        };
    }

    /**
     * Gets the row offset associated with this direction.
     *
     * @return the row offset value
     */
    public int getRowOffset() {
        return this.rowOffset;
    }

    /**
     * Gets the column offset associated with this direction.
     *
     * @return the column offset value
     */
    public int getColOffset() {
        return this.colOffset;
    }
}
