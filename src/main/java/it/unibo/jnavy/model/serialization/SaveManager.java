package it.unibo.jnavy.model.serialization;

import java.util.Optional;

/**
 * Manages the persistence of the game state to a file.
 */
public interface SaveManager {
    /**
     * Saves the current state of the game.
     *
     * @param state The GameState object to be saved.
     * @return true if the save was successful, false otherwise.
     */
    boolean save(GameState state);

    /**
     * Loads the previously saved game state.
     *
     * @return An Optional containing the loaded GameState if the file exists and is valid, otherwise an empty Optional.
     */
    Optional<GameState> load();

    /**
     * Deletes the previously saved game file, if it exists.
     *
     * @return true if the file was successfully deleted or didn't exist, false if deletion failed.
     */
    boolean deleteSave();
}
