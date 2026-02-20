package it.unibo.jnavy.view.game;

import javax.swing.*;
import java.awt.*;

import it.unibo.jnavy.view.components.bot.BotDifficultyPanel;
import it.unibo.jnavy.view.components.captain.CaptainAbilityButton;
import it.unibo.jnavy.view.components.captain.CaptainNamePanel;
import it.unibo.jnavy.view.components.weather.WeatherWidget;

import static it.unibo.jnavy.view.utilities.ViewConstants.*;

public class GameDashboardPanel extends JPanel {

    private final WeatherWidget weatherWidget;
    private final CaptainAbilityButton captainButton;
    private final BotDifficultyPanel difficultyPanel;
    private final CaptainNamePanel captainNamePanel;

    public GameDashboardPanel(String difficulty, int initialCooldown, String captainName) {
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

    public void updateDashboard(int currentCooldown, String currentConditionName) {
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