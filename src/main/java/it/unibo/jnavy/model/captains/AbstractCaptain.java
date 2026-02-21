package it.unibo.jnavy.model.captains;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.observer.TurnObserver;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Abstract base implementation of the {@link Captain} interface.
 * This class handles the common logic for all captains, specifically the
 * cooldown management mechanism. It implements {@link TurnObserver} to
 * automatically increment the cooldown counter at the end of each turn.
 */
public abstract class AbstractCaptain implements Captain {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final int cooldown;
    private int currentCooldown;
    private boolean usedThisTurn;

    /**
     * Constructs a new Captain with a specific cooldown.
     *
     * @param cooldown the cooldown duration in turns.
     */
    protected AbstractCaptain(final int cooldown) {
        this.cooldown = cooldown;
        this.currentCooldown = 0;
    }

    /**
     * Executes the specific effect of the captain's ability.
     * This method must be implemented by subclasses to define the ability's behavior.
     *
     * @param grid the target grid where the effect should be applied.
     * @param p the target position for the ability.
     * @return true if the effect was successfully executed, false otherwise.
     */
    protected abstract boolean executeEffect(Grid grid, Position p);

    @Override
    public final boolean useAbility(final Grid grid, final Position p) {
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
