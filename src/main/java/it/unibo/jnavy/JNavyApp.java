package it.unibo.jnavy;

import javax.swing.SwingUtilities;

import it.unibo.jnavy.controller.selection.SelectionController;
import it.unibo.jnavy.view.View;
import it.unibo.jnavy.view.ViewGUI;

public final class JNavyApp {

    private JNavyApp() { }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            SelectionController sController = new SelectionController();
            final View view = new ViewGUI(sController);
            sController.setView(view);
            view.start();
        });
    }
}
