package it.unibo.jnavy.view.start;

import java.awt.*;
import javax.swing.*;

import it.unibo.jnavy.view.utilities.AmbientSoundManager;
import static it.unibo.jnavy.view.utilities.ViewConstants.*;

/**
 * Game start screen.
 */
public class StartView extends JPanel {

    private static final Color BACKGROUND_BUTTON = new Color(41, 86, 246);

    private final Runnable onStartAction;

    public StartView(final Runnable onStartAction) {
        this.onStartAction = onStartAction;
        this.initUI();
    }

    private void initUI() {
        this.setLayout(new GridBagLayout());
        this.setBackground(BACKGROUND_COLOR);
        AmbientSoundManager ambientSound = new AmbientSoundManager("/sounds/ship_horn.wav", 10000);
        ambientSound.start();


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("JNavy");
        titleLabel.setFont(new Font("Impact", Font.BOLD, 100));
        titleLabel.setForeground(FOREGROUND_COLOR);
        this.add(titleLabel, gbc);

        JLabel descLabel = new JLabel("Dominates the Ocean. Sink the Enemy.");
        descLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 24));
        descLabel.setForeground(new Color(180, 180, 200));
        this.add(descLabel, gbc);

        gbc.insets = new Insets(50, 0, 0, 0);

        JButton startButton = new JButton("START GAME");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 28));
        startButton.setForeground(FOREGROUND_COLOR);
        startButton.setBackground(BACKGROUND_BUTTON);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(10, 40, 10, 40)
        ));
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        startButton.addActionListener(e -> {
            if (onStartAction != null) {
                ambientSound.stop();
                onStartAction.run();
            }
        });

        this.add(startButton, gbc);
    }
}