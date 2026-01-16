package it.unibo.jnavy.model.captain;

/**
*
*/
public interface Captain {

    /**
     *
     * @return true if the ability can be used, false otherwise
     */
    boolean isAbilityRecharged();

    /*
    *
    */
    void applyAbility();
}