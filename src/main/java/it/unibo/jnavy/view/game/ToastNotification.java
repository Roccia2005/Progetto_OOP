package it.unibo.jnavy.view.game;

import static it.unibo.jnavy.view.utilities.ViewConstants.BORDER_THICKNESS;
import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;
import static it.unibo.jnavy.view.utilities.ViewConstants.FOREGROUND_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.INSET_PADDING;

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

/**
 * Utility class responsible for displaying temporary, non-blocking popup messages.
 * These notifications appear over a designated parent component and automatically
 * close after a brief duration, making them ideal for quick user feedback.
 */
public final class ToastNotification {

    private static final int FONT_SIZE = 18;
    private static final int PADDING_H = 20;
    private static final int DISPLAY_DURATION_MS = 1500;

    private ToastNotification() {
        // Utility classes should not be instantiated.
    }

    /**
     * Creates and displays a temporary toast notification centered over the given parent component.
     * The notification is displayed asynchronously and automatically disposes itself.
     *
     * @param parent  the {@link Component} to center over (defaults to screen if {@code null} or hidden).
     * @param message passed string representing the text to be displayed inside the popup.
     * @param color the background {@link Color} of the notification box.
     */
    public static void show(final Component parent, final String message, final Color color) {
        final JWindow toast = new JWindow(SwingUtilities.getWindowAncestor(parent));

        toast.setAlwaysOnTop(true);
        toast.setFocusableWindowState(false);
        toast.setType(java.awt.Window.Type.POPUP);

        final JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE));
        label.setForeground(FOREGROUND_COLOR);
        label.setBackground(color);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FOREGROUND_COLOR, BORDER_THICKNESS),
                BorderFactory.createEmptyBorder(INSET_PADDING, PADDING_H, INSET_PADDING, PADDING_H)
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

        final Timer timer = new Timer(DISPLAY_DURATION_MS, e -> {
            toast.setVisible(false);
            toast.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
