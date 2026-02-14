package it.unibo.jnavy.view.components;

import it.unibo.jnavy.model.weather.WeatherCondition;

import javax.swing.*;

/**
 * Represents the graphical widget that displays the current weather icon.
 * This component is designed to be placed in the top-right corner of the HUD.
 * It shows <b>only the icon</b> representing the active weather state (e.g., Sunny, Fog),
 * keeping the interface clean.
 */
public class WeatherWidget extends JPanel {

    private final JLabel iconLabel;
    private final JLabel textLabel;

    /**
     * Constructs a new {@code WeatherWidget}.
     * The widget is initialized with a default "Sunny" state and contains only
     * a large centered icon.
     */
    public WeatherWidget() {

    }

    /**
     * Updates the widget's icon based on the provided weather condition.
     *
     * @param condition the new {@link WeatherCondition} to display.
     */
    public void updateWeather(WeatherCondition condition) {

    }
}
