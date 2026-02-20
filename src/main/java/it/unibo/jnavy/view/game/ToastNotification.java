package it.unibo.jnavy.view.game;

import javax.swing.*;
import java.awt.*;

import static it.unibo.jnavy.view.utilities.ViewConstants.*;

public final class ToastNotification {

    private ToastNotification() {
    }

    public static void show(Component parent, String message) {
        final JWindow toast = new JWindow(SwingUtilities.getWindowAncestor(parent));

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font(FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        label.setBackground(MENUBLUE);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        toast.add(label);
        toast.pack();

        if (parent != null && parent.isShowing()) {
            Point location = parent.getLocationOnScreen();
            int x = location.x + (parent.getWidth() - toast.getWidth()) / 2;
            int y = location.y + (parent.getHeight() - toast.getHeight()) / 2;
            toast.setLocation(x, y);
        }

        toast.setVisible(true);

        Timer timer = new Timer(1500, e -> {
            toast.setVisible(false);
            toast.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }
}