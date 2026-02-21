package it.unibo.jnavy.view.game;

import static it.unibo.jnavy.view.utilities.ViewConstants.BACKGROUND_COLOR;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import it.unibo.jnavy.view.components.bot.BotDifficultyPanel;
import it.unibo.jnavy.view.components.captain.CaptainAbilityButton;
import it.unibo.jnavy.view.components.captain.CaptainNamePanel;
import it.unibo.jnavy.view.components.weather.WeatherWidget;

public class GameDashboardPanel extends JPanel {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final WeatherWidget weatherWidget;
    private final CaptainAbilityButton captainButton;
    private final BotDifficultyPanel difficultyPanel;
    private final CaptainNamePanel captainNamePanel;

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

    public void updateDashboard(final int currentCooldown, final String currentConditionName) {
        this.captainButton.updateState(currentCooldown);
        this.weatherWidget.updateWeather(currentConditionName);
    }

    public boolean isCaptainAbilityActive() {
        return this.captainButton.isActive();
    }

    public void resetCaptainAbility() {
        this.captainButton.reset();
    }
}
