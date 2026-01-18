package it.unibo.jnavy.model.captains;

import it.unibo.jnavy.model.TurnObserver;

public abstract class AbstractCaptain implements Captain, TurnObserver{

    private final int cooldown;
    private int currentCooldown;

    protected AbstractCaptain(final int cooldown) {
        this.cooldown = cooldown;
        this.currentCooldown = 0;
    }

    @Override
    public boolean isAbilityRecharged() {
        return this.currentCooldown >= this.cooldown;
    }

    @Override
    public void onTurnEnd() {
        this.currentCooldown++;
    }

    protected void resetCooldown() {
        this.currentCooldown = 0;
    }

}
