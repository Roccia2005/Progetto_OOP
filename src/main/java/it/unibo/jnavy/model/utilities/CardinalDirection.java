package it.unibo.jnavy.model.utilities;

public enum CardinalDirection {
    UP(-1, 0),
    RIGHT(0, 1),
    DOWN(1, 0),
    LEFT(0, -1);

    private final int rowOffset;
    private final int colOffset;

    CardinalDirection(final int rowOffset, final int colOffset) {
        this.rowOffset = rowOffset;
        this.colOffset = colOffset;
    }

    public CardinalDirection opposite() {
        switch (this) {
            case UP:
                return DOWN;
            case RIGHT:
                return LEFT;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            default:
                throw new IllegalStateException("Invalid corresponding enum(opposite) value: " + this);
        }
    }

    public int getRowOffset() {
        return this.rowOffset;
    }

    public int getColOffset() {
        return this.colOffset;
    }
}
