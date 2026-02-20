package it.unibo.jnavy.model.utilities;

import java.io.Serializable;

/**
 * Represents a two-dimensional position within the game grid.
 * The game uses a Matrix coordinate system (Row-Major Order), not a standard Cartesian one.
 * @param x the vertical coordinate (represents the row index in the grid matrix).
 * @param y the horizontal coordinate (represents the column index in the grid matrix).
 */
public record Position(int x, int y) implements Serializable {
}
