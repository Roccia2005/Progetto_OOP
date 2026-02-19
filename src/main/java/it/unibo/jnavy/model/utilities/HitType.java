package it.unibo.jnavy.model.utilities;

/**
 * Represents the possible outcomes of a shot fired at the game grid.
 * This enum is used to communicate the results of an attack.
 */
public enum HitType {

    /**
     * The shot landed in water; no ship was hit.
     */
    MISS("Missed, just water!"),

    /**
     * A ship was hit. but is still afloat.
     */
    HIT("Target hit!"),

    /**
     * A ship was hit and sunk.
     */
    SUNK("Ship sunk"),

    /**
     * The target coordinates are outside the grid boundaries or are invalid.
     */
    INVALID("Invalid coordinates");

    private final String description;

    /**
     * Constructor for the HitType enum.
     *
     * @param description A human-readable description of the shot result.
     */
    HitType(final String description) {
        this.description = description;
    }

    /**
     * Returns the human-readable description of the result.
     *
     * @return The description string.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Checks if the shot was successful (i.e., it hit or sunk a ship).
     *
     * @return true if the result is HIT or SUNK, false otherwise.
     */
    public boolean isSuccessful() {
        return this == HIT || this == SUNK;
    }

}
