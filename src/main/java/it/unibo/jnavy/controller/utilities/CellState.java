package it.unibo.jnavy.controller.utilities;

public record CellState(
        boolean hasShip,
        int shipId,           // to identify which ship a cell belongs to
        boolean connectedTop,
        boolean connectedBottom,
        boolean connectedLeft,
        boolean connectedRight
) {
    public static CellState water() {
        return new CellState(false, -1, false, false, false, false);
    }
}
