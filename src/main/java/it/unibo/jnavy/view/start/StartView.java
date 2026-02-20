package it.unibo.jnavy.view.start;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;

import it.unibo.jnavy.view.utilities.SoundManager;

import static it.unibo.jnavy.view.utilities.ViewConstants.*;

/**
 * Game start screen.
 */
public class StartView extends JPanel {

    private static final Color BACKGROUND_BUTTON = new Color(41, 86, 246);
    private static final String SOUND_PATH = "/sounds/ship_horn.wav";
    private static final String TITLE = "Dominate the Ocean. Sink the Enemy.";
    private static final String START_GAME = "NEW GAME";

    private final Runnable onStartAction;
    private SoundManager ambientSound;
    private final Runnable onLoadAction;

    public StartView(final Runnable onStartAction, final Runnable onLoadAction) {
        this.onStartAction = onStartAction;
        this.onLoadAction = onLoadAction;
        this.initUI();
    }

    private void initUI() {
        this.setLayout(new GridBagLayout());
        this.setBackground(BACKGROUND_COLOR);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                ambientSound = new SoundManager(SOUND_PATH);
                ambientSound.start();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                if (ambientSound != null) {
                    ambientSound.close();
                }
            }
        });


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("JNavy");
        titleLabel.setFont(new Font("Impact", Font.BOLD, 100));
        titleLabel.setForeground(FOREGROUND_COLOR);
        this.add(titleLabel, gbc);

        JLabel descLabel = new JLabel(TITLE);
        descLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 24));
        descLabel.setForeground(new Color(180, 180, 200));
        this.add(descLabel, gbc);

        gbc.insets = new Insets(50, 0, 0, 0);

        JButton startButton = new JButton(START_GAME);
        startButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 28));
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

        gbc.insets = new Insets(20, 0, 0, 0);

        JButton loadButton = new JButton("LOAD GAME");
        loadButton.setFont(new Font("SansSerif", Font.BOLD, 22));
        loadButton.setForeground(Color.WHITE);
        loadButton.setBackground(new Color(60, 60, 80));
        loadButton.setFocusPainted(false);
        loadButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createEmptyBorder(10, 40, 10, 40)
        ));
        loadButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        loadButton.addActionListener(e -> {
            if (onLoadAction != null) {
                ambientSound.stop();
                onLoadAction.run();
            }
        });

        this.add(loadButton, gbc);
    }
}