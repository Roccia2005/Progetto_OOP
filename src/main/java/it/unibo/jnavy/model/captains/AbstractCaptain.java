package it.unibo.jnavy.model.captains;

import it.unibo.jnavy.model.TurnObserver;

/**
 * Abstract base implementation of the {@link Captain} interface.
 * 
 * This class handles the common logic for all captains, specifically the
 * cooldown management mechanism. It implements {@link TurnObserver} to
 * automatically increment the cooldown counter at the end of each turn.
 */
public abstract class AbstractCaptain implements Captain {

    private final int cooldown;
    private int currentCooldown;
    private boolean usedThisTurn = false;

    protected AbstractCaptain(final int cooldown) {
        this.cooldown = cooldown;
        this.currentCooldown = 0;
    }

    @Override
    public boolean isAbilityRecharged() {
        return this.currentCooldown >= this.cooldown;
    }

    @Override
    public void processTurnEnd() {
        if (this.usedThisTurn) {
            this.usedThisTurn = false; 
        } else {
            this.currentCooldown++;
        }
    }

    @Override
    public int getCooldown() {
        return this.cooldown;
    }

    @Override
    public int getCurrentCooldown() {
        return this.currentCooldown;
    }

    /**
     * Resets the cooldown counter to zero.
     */
    protected void resetCooldown() {
        this.currentCooldown = 0;
        this.usedThisTurn = true;
    }

}