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
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        this.setPreferredSize(new Dimension(80, 80));

        this.iconLabel = new JLabel("☀\uFE0F", SwingConstants.CENTER);
        this.iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));

        this.add(iconLabel, BorderLayout.CENTER);

        updateWeather(WeatherCondition.SUNNY);
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
