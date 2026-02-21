package it.unibo.jnavy.view.game;

import static it.unibo.jnavy.view.utilities.ViewConstants.BACKGROUND_COLOR;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import it.unibo.jnavy.view.components.bot.BotDifficultyPanel;
import it.unibo.jnavy.view.components.captain.CaptainAbilityButton;
import it.unibo.jnavy.view.components.captain.CaptainNamePanel;
import it.unibo.jnavy.view.components.weather.WeatherWidget;

/**
 * A UI component representing the bottom dashboard during the game phase.
 * It aggregates status indicators and controls, like the bot's difficulty,
 * the current weather condition, the captain's name, and the interactive ability button.
 */
public class GameDashboardPanel extends JPanel {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final WeatherWidget weatherWidget;
    private final CaptainAbilityButton captainButton;
    private final BotDifficultyPanel difficultyPanel;
    private final CaptainNamePanel captainNamePanel;

    /**
     * Constructs a new {@code GameDashboardPanel} initializing all its sub-components.
     *
     * @param difficulty the string representing the bot's difficulty level.
     * @param initialCooldown the starting cooldown value required for the captain's ability.
     * @param captainName the name of the player's selected captain.
     */
    public GameDashboardPanel(final String difficulty, final int initialCooldown, final String captainName) {
        super(new FlowLayout(FlowLayout.CENTER, 100, 10));
        this.setBackground(BACKGROUND_COLOR);
        this.setOpaque(true);

        this.difficultyPanel = new BotDifficultyPanel(difficulty);
        this.weatherWidget = new WeatherWidget();
        this.captainButton = new CaptainAbilityButton(initialCooldown);
        this.captainNamePanel = new CaptainNamePanel(captainName);

        this.captainButton.addActionListener(e -> {
            if (this.captainButton.isEnabled()) {
                this.captainButton.select();
            }
        });

        this.add(this.difficultyPanel);
        this.add(this.weatherWidget);
        this.add(this.captainButton);
        this.add(this.captainNamePanel);
    }

    /**
     * Refreshes the dashboard widgets with the latest game state data.
     * Updates the captain's ability cooldown progress and the current weather icon.
     *
     * @param currentCooldown the current cooldown value.
     * @param currentConditionName the name of the current weather condition.
     */
    public void updateDashboard(final int currentCooldown, final String currentConditionName) {
        this.captainButton.updateState(currentCooldown);
        this.weatherWidget.updateWeather(currentConditionName);
    }

    /**
     * Checks if the player has currently toggled the captain's ability button to active.
     *
     * @return {@code true} if the ability is selected and waiting to be deployed, {@code false} otherwise.
     */
    public boolean isCaptainAbilityActive() {
        return this.captainButton.isActive();
    }

    /**
     * Resets the captain's ability button back to its inactive state.
     * This is typically called after the ability has been executed on the grid.
     */
    public void resetCaptainAbility() {
        this.captainButton.reset();
    }
}
