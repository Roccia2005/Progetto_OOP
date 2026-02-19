package it.unibo.jnavy.view;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;

/**
 * Game start screen.
 */
public class StartView extends JPanel {

    private static final Color THEME_BACKGROUND = new Color(20, 20, 30);
    private static final Color THEME_TEXT = new Color(240, 240, 255);
    private static final Color COLOR_ACCENT = new Color(41, 86, 246);

    private final Runnable onStartAction;

    public StartView(final Runnable onStartAction) {
        this.onStartAction = onStartAction;
        this.initUI();
    }

    private void initUI() {
        this.setLayout(new GridBagLayout());
        this.setBackground(THEME_BACKGROUND);
        AmbientSoundManager ambientSound = new AmbientSoundManager("/sounds/ship_horn.wav", 10000);
        ambientSound.start();


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("JNavy");
        titleLabel.setFont(new Font("Impact", Font.BOLD, 100));
        titleLabel.setForeground(THEME_TEXT);
        this.add(titleLabel, gbc);

        JLabel descLabel = new JLabel("Dominates the Ocean. Sink the Enemy.");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        descLabel.setForeground(new Color(180, 180, 200));
        this.add(descLabel, gbc);

        gbc.insets = new Insets(50, 0, 0, 0);

        JButton startButton = new JButton("START GAME");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 28));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(COLOR_ACCENT);
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