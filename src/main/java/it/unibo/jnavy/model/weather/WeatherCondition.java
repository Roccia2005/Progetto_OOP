package it.unibo.jnavy.model.weather;

/**
 * Represents the possible weather conditions affecting the naval battle.
 * Each condition defines a specific environment state that can
 * influence shot accuracy or captain abilities.
 */
public enum WeatherCondition {

    /**
     * Standard weather condition.
     * Visibility is perfect. Shots land exactly where targeted without any deviation.
     */
    SUNNY("Sunny", "The weather is sunny and clear."),

    /**
     * Dense fog covering the battlefield.
     * This condition reduces visibility, causing shots to potentially deviate
     * from their intended target.
     */
    FOG("Foggy", "The weather is foggy and cloudy.");

    private final String name;
    private final String description;

    /**
     * Internal constructor to associate display properties with the weather condition.
     *
     * @param name The human-readable name of the condition.
     * @param description A brief explanation of the effect.
     */
    WeatherCondition(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * @return The display name of the weather condition.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return A brief description of the weather effect.
     */
    public String getDescription() {
        return this.description;
    }
}
