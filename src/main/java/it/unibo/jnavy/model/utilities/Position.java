package it.unibo.jnavy.model.utilities;

/**
 * Represents a two-dimensional position within the game grid.
 * This record is used to identify specific coordinates on the map.
 * 
 * @param x the horizontal coordinate (often referred to as the column).
 * @param y the vertical coordinate (often referred to as the row).
 */
public record Position(int x, int y) {
}
