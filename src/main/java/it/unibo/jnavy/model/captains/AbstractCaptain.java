package it.unibo.jnavy.model.captains;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.observer.TurnObserver;
import it.unibo.jnavy.model.utilities.Position;

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

    protected abstract boolean executeEffect(Grid grid, Position p);

    @Override
    public final boolean useAbility(Grid grid, Position p) {
        if (this.isAbilityRecharged() && grid.isPositionValid(p)) {
            if (this.executeEffect(grid, p)) {
                this.resetCooldown();
                return true;
            }
        }
        return false;
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

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Resets the cooldown counter to zero.
     */
    private void resetCooldown() {
        this.currentCooldown = 0;
        this.usedThisTurn = true;
    }

}