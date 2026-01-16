package it.unibo.jnavy.model;

/**
 * An observer interface for handling turn-based events in the game.
 * Classes implementing this interface will be notified by the Game Controller
 * whenever a turn ends. This mechanism is crucial for synchronizing time-dependent
 * mechanics such as:
 *   - Reducing the cooldown of Captain's special abilities
 *   - Updating the duration of Weather conditions (e.g., fog lifting after 3 turns)
 *   - Updating the UI timer or turn counter
 */
public interface TurnObserver {

}
