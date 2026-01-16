package it.unibo.jnavy.model.captains;

/**
 *
 */
public interface Captain {

    /**
     *
     * @return true if the ability can be used, false otherwise
     */
    boolean isAbilityRecharged();

    /**
     *
     */
    void applyAbility();
}