package it.unibo.jnavy.model.serialization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Optional;



public class SaveManagerImpl implements SaveManager {

    private static final String FILE_NAME = "game_state.dat";

    @Override
    public boolean save(final GameState state) {
        boolean result;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(state);
            result = true;
        } catch (final IOException e) {
            System.err.println("Critical error while saving the game: " + e.getMessage());
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    @Override
    public Optional<GameState> load() {
        final File file = new File(FILE_NAME);

        if (!file.exists()) {
            return Optional.empty();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            final GameState loadedState = (GameState) ois.readObject();
            return Optional.of(loadedState);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error while loading the game (corrupted file or incompatible version): " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteSave() {
        final File file = new File(FILE_NAME);

        if (file.exists()) {
            return file.delete();
        }
        return true;
    }
}
