package it.unibo.jnavy.view.components;

import javax.swing.*;
import java.awt.*;

public class NotificationOverlay extends JComponent {

    private String message = "";
    private final Timer timer;

    public NotificationOverlay() {
        this.timer = new Timer(3000, e -> {
            message = "";
            repaint();
        });
        this.timer.setRepeats(false);
        this.setOpaque(false);
    }

    public void showMessage(String text, Color color) {
        this.message = text;
        this.setBackground(color);
        this.timer.restart();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (this.message.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 60));
        FontMetrics fm = g2.getFontMetrics();

        int textWidth = fm.stringWidth(message);
        int textHeight = fm.getAscent();
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() + textHeight) / 2;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(message, x + 4, y + 4);

        g2.setColor(getForeground());
        g2.drawString(message, x, y);
    }

}
