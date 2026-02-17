package it.unibo.jnavy;

import javax.swing.SwingUtilities;
import it.unibo.jnavy.view.View;
import it.unibo.jnavy.view.ViewGUI;

public final class JNavyApp {

    private JNavyApp() {}
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View view = new ViewGUI();
            view.start();
        });
    }
}