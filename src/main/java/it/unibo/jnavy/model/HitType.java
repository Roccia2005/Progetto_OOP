package it.unibo.jnavy.model;

public enum HitType {

    MISS("Missed, just water!"),
    HIT("Target hit!"),
    SUNK("Ship sunk"),
    ALREADY_HIT("You already fired here!"),
    INVALID("Invalid coordinates");

    private final String description;

    HitType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isSuccessful() {
        return this == HIT || this == SUNK;
    }
    
}
