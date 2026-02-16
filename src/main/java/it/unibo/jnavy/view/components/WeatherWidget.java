package it.unibo.jnavy.view.components;

import it.unibo.jnavy.model.weather.WeatherCondition;

import javax.swing.*;
import java.awt.*;

/**
 * Represents the graphical widget that displays the current weather icon.
 * This component is designed to be placed in the top-right corner of the HUD.
 * It shows <b>only the icon</b> representing the active weather state (e.g., Sunny, Fog),
 * keeping the interface clean.
 */
public class WeatherWidget extends JPanel {

    private final JLabel iconLabel;
    private Color borderColor;
    private Color backgroundColor;

    /**
     * Constructs a new {@code WeatherWidget}.
     * The widget is initialized with a default "Sunny" state and contains only
     * a large centered icon.
     */
    public WeatherWidget() {
        this.setLayout(new GridBagLayout());
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(70, 70));

        this.iconLabel = new JLabel("☀\uFE0F", SwingConstants.CENTER);
        this.iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 45));

        this.add(iconLabel);

        updateWeather(WeatherCondition.SUNNY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int strokeWidth = 4;
        int diameter = Math.min(getWidth(), getHeight()) - (strokeWidth * 2);
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;

        g2.setColor(this.backgroundColor);
        g2.fillOval(x, y, diameter, diameter);

        g2.setColor(this.borderColor);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.drawOval(x, y, diameter, diameter);

        g2.dispose();

        super.paintComponent(g);
    }

    /**
     * Updates the widget's icon based on the provided weather condition.
     *
     * @param condition the new {@link WeatherCondition} to display.
     */
    public void updateWeather(WeatherCondition condition) {
        switch (condition) {
            case SUNNY -> {
                this.iconLabel.setText("☀\uFE0F");
                this.borderColor = new Color(255, 200, 50);
                this.backgroundColor = new Color(255, 250, 200, 150);
                this.setToolTipText("Weather: sunny");
            }
            case FOG -> {
                this.iconLabel.setText("🌫️");
                this.borderColor = new Color(100, 120, 140);
                this.backgroundColor = new Color(200, 210, 220, 150);
                this.setToolTipText("Weather: fog");
            }
        }
        this.repaint();
    }
}
