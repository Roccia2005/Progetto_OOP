package it.unibo.jnavy.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

public class NotificationOverlay extends JComponent {

    private String title = "";
    private String subtitle = "";
    private final Timer timer;

    public NotificationOverlay() {
        this.timer = new Timer(3000, e -> {
            this.title = "";
            this.subtitle = "";
            repaint();
        });
        this.timer.setRepeats(false);
        this.setOpaque(false);
    }

    public void showWeatherAlert(String weatherName) {
        this.title = "⚠️ ATTENTION! WEATHER CHANGED";
        this.subtitle = "The weather is now " + weatherName + ".";
        this.setForeground(new Color(255, 215, 0));

        this.timer.restart();
        this.setVisible(true);
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (this.title.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        Font fontTitle = new Font("Segoe UI", Font.BOLD, 50);
        drawOutlinedText(g2, title, fontTitle, centerX, centerY - 30);

        Font fontSubtitle = new Font("Segoe UI", Font.BOLD, 40);
        drawOutlinedText(g2, subtitle, fontSubtitle, centerX, centerY + 30);
    }

    private void drawOutlinedText(Graphics2D g2, String text, Font font, int x, int y) {
        FontMetrics fm = g2.getFontMetrics(font);
        int textWidth = fm.stringWidth(text);

        float drawX = x - (textWidth / 2f);
        float drawY = y;

        TextLayout textLayout = new TextLayout(text, font, g2.getFontRenderContext());

        AffineTransform transform = AffineTransform.getTranslateInstance(drawX, drawY);
        Shape textShape = textLayout.getOutline(transform);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(4f)); // Spessore del bordo (4 pixel)
        g2.draw(textShape);

        g2.setColor(getForeground());
        g2.fill(textShape);
    }

}
