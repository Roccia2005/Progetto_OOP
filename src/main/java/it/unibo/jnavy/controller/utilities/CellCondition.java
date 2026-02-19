package it.unibo.jnavy.controller.utilities;

public enum CellCondition {
    FOG,
    WATER,
    SHIP,
    HIT_WATER,
    HIT_SHIP,
    SUNK_SHIP,
    REVEALED_SHIP,
    REVEALED_WATER;

    public boolean isAlreadyHit() {
        return this == HIT_WATER || this == HIT_SHIP;
    }
}
