package it.unibo.jnavy;

import javax.swing.SwingUtilities;

import it.unibo.jnavy.controller.selection.SelectionController;
import it.unibo.jnavy.view.View;
import it.unibo.jnavy.view.ViewGUI;

/**
 * The main entry point for the JNavy application.
 * This class is responsible for initializing the core components
 * of the application and starting the graphical user interface.
 */
public final class JNavyApp {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private JNavyApp() { }

    /**
     * The main method that launches the application.
     * It ensures that the GUI creation and initialization are executed
     * safely on the Event Dispatch Thread (EDT).
     *
     * @param args the command-line arguments (not used)
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            final SelectionController sController = new SelectionController();
            final View view = new ViewGUI(sController);
            sController.setView(view);
            view.start();
        });
    }
}
