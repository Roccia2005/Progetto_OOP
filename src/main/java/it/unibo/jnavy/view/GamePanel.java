package it.unibo.jnavy.view;

import javax.swing.*;
import java.awt.*;

import it.unibo.jnavy.controller.GameController;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.view.components.captain.CaptainAbilityButton;
import it.unibo.jnavy.view.components.weather.WeatherWidget;

import it.unibo.jnavy.view.components.grid.GridPanel;

public class GamePanel extends JPanel {

    private static final String HUMAN_FLEET = "My Fleet";
    private static final String BOT_FLEET = "Enemy Fleet";

    private final JPanel humanGridPanel;
    private final JPanel botGridPanel;
    private final WeatherWidget weatherWidget;
    private final CaptainAbilityButton captainButton;
    private final GameController controller;

    public GamePanel(GameController controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());

        JPanel gridsContainer = new JPanel(new GridLayout(1, 2, 40, 0));
        gridsContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel dashboardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 10));

        weatherWidget = new WeatherWidget();
        captainButton = new CaptainAbilityButton(this.controller.getCaptainCooldown());

        captainButton.addActionListener(e -> {
            if (captainButton.isEnabled()) {
                captainButton.select();
            }
        });

        dashboardPanel.add(weatherWidget);
        dashboardPanel.add(captainButton);

        this.humanGridPanel = new GridPanel(this.controller, HUMAN_FLEET,
                                            (Position p) -> {
                                                if (captainButton.isActive()) {
                                                    controller.processAbility(p);
                                                    captainButton.reset();
                                                }
                                             }); 
        this.botGridPanel = new GridPanel(this.controller, BOT_FLEET, 
                                            (Position p) -> {
                                                if (captainButton.isActive()) {
                                                    controller.processAbility(p);
                                                    captainButton.reset();
                                                } else {
                                                    controller.processShot(p);
                                                }
                                            });

        gridsContainer.add(this.humanGridPanel);
        gridsContainer.add(this.botGridPanel);

        JLabel titleLabel = new JLabel("J-NAVY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        this.add(titleLabel, BorderLayout.NORTH);
        this.add(gridsContainer, BorderLayout.CENTER);
        this.add(dashboardPanel, BorderLayout.SOUTH);
    }
}