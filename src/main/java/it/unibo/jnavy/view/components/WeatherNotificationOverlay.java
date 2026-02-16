package it.unibo.jnavy.view.components;

import javax.swing.*;
import java.awt.*;

public class WeatherNotificationOverlay extends JComponent {

    private String title = "";
    private String subtitle = "";
    private final Timer timer;

    private static final int PADDING = 40;
    private static final int CORNER_RADIUS = 30;

    public WeatherNotificationOverlay() {
        this.timer = new Timer(3000, e -> {
            title = "";
            subtitle = "";
            repaint();
        });
        this.timer.setRepeats(false);
        this.setOpaque(false);
    }

    public void showWeatherAlert(String weatherName) {
        this.title = "Attention! Weather changed.";
        this.subtitle = "Current weather: " + weatherName;

        this.timer.restart();
        this.setVisible(true);
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (title.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font titleFont = new Font("SansSerif", Font.BOLD, 28);
        Font subFont = new Font("SansSerif", Font.PLAIN, 22);

        FontMetrics fmTitle = g2.getFontMetrics(titleFont);
        FontMetrics fmSub = g2.getFontMetrics(subFont);

        int textWidth = Math.max(fmTitle.stringWidth(title), fmSub.stringWidth(subtitle));
        int boxWidth = textWidth + (PADDING * 2);
        int boxHeight = 120;

        int boxX = (getWidth() - boxWidth) / 2;
        int boxY = (getHeight() - boxHeight) / 2;

        g2.setColor(new Color(20, 20, 30, 220));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, CORNER_RADIUS, CORNER_RADIUS);

        g2.setColor(new Color(255, 200, 50));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, CORNER_RADIUS, CORNER_RADIUS);

        g2.setFont(titleFont);
        g2.setColor(new Color(255, 200, 50));
        int titleX = boxX + (boxWidth - fmTitle.stringWidth(title)) / 2;
        g2.drawString(title, titleX, boxY + 50);

        g2.setFont(subFont);
        g2.setColor(new Color(240, 240, 255));
        int subX = boxX + (boxWidth - fmSub.stringWidth(subtitle)) / 2;
        g2.drawString(subtitle, subX, boxY + 90);
    }
}