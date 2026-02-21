package it.unibo.jnavy.controller.utilities;

/**
 * Represents the detailed state of a cell on the game grid.
 * This record is primarily used during the setup phase to provide the view with
 * all the necessary information for rendering, including ship presence and
 * adjacency data for correct sprite selection.
 *
 * @param hasShip true if the cell contains a ship, false otherwise.
 * @param shipId a unique identifier for the ship in this cell, used to distinguish
 *               between different ships.
 * @param connectedTop true if there is a part of the same ship in the cell above.
 * @param connectedBottom true if there is a part of the same ship in the cell below.
 * @param connectedLeft true if there is a part of the same ship in the cell to the left.
 * @param connectedRight true if there is a part of the same ship in the cell to the right.
 */
public record CellState(
        boolean hasShip,
        int shipId,           // to identify which ship a cell belongs to
        boolean connectedTop,
        boolean connectedBottom,
        boolean connectedLeft,
        boolean connectedRight
) {
    /**
     * Creates a new CellState representing a standard water cell with no ship.
     *
     * @return a default water CellState.
     */
    public static CellState water() {
        return new CellState(false, -1, false, false, false, false);
    }
}
