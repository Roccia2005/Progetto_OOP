package it.unibo.jnavy.controller.utilities;

/**
 * Represents the observable state of a cell from the perspective of the view.
 * This enum defines the different visual conditions a cell can be in,
 * depending on whether it has been attacked, revealed by an ability,
 * or is hidden by fog.
 */
public enum CellCondition {
    /**
     * The cell is hidden and its content is unknown.
     */
    FOG,
    /**
     * The cell contains water and has not been hit.
     */
    WATER,
    /**
     * The cell contains a ship that has not been hit.
     */
    SHIP,
    /**
     * A shot was fired at this cell, but it hit water (a miss).
     */
    HIT_WATER,
    /**
     * A shot was fired at this cell and successfully hit a ship.
     */
    HIT_SHIP,
    /**
     * The cell contains a part of a ship that has been completely sunk.
     */
    SUNK_SHIP,
    /**
     * The cell contains a ship revealed by a special ability (e.g., Sonar).
     */
    REVEALED_SHIP,
    /**
     * The cell contains water revealed by a special ability.
     */
    REVEALED_WATER;

    /**
     * Determines if the cell has already been targeted by a standard shot.
     *
     * @return true if the cell's condition is either HIT_WATER or HIT_SHIP, false otherwise.
     */
    public boolean isAlreadyHit() {
        return this == HIT_WATER || this == HIT_SHIP;
    }
}
