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

    /**
     * Constructs a new {@code WeatherWidget}.
     * The widget is initialized with a default "Sunny" state and contains only
     * a large centered icon.
     */
    public WeatherWidget() {
        this.setLayout(new GridBagLayout());
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(80, 80));

        this.iconLabel = new JLabel("☀\uFE0F", SwingConstants.CENTER);
        this.iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));

        this.add(iconLabel);

        updateWeather(WeatherCondition.SUNNY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int diameter = Math.min(getWidth(), getHeight()) - 4;
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;

        g2.setColor(getBackground());
        g2.fillOval(x, y, diameter, diameter);

        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(2));
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
                this.setToolTipText("Weather: sunny");
            }
            case FOG -> {
                this.iconLabel.setText("🌫️");
                this.setToolTipText("Weather: fog");
            }
        }
        this.repaint();
    }
}
