package it.unibo.jnavy.view.game;

import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public final class ToastNotification {

    private ToastNotification() {
    }

    public static void show(final Component parent, final String message, final Color color) {
        final JWindow toast = new JWindow(SwingUtilities.getWindowAncestor(parent));

        toast.setAlwaysOnTop(true);
        toast.setFocusableWindowState(false);
        toast.setType(java.awt.Window.Type.POPUP);

        final JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font(FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        label.setBackground(color);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        toast.add(label);
        toast.pack();

        if (parent != null && parent.isShowing()) {
            final Point location = parent.getLocationOnScreen();
            final int x = location.x + (parent.getWidth() - toast.getWidth()) / 2;
            final int y = location.y + (parent.getHeight() - toast.getHeight()) / 2;
            toast.setLocation(x, y);
        }

        toast.setVisible(true);

        final Timer timer = new Timer(1500, e -> {
            toast.setVisible(false);
            toast.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
