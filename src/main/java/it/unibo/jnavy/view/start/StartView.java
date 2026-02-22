package it.unibo.jnavy.view.start;

import static it.unibo.jnavy.view.utilities.ViewConstants.BACKGROUND_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.BORDER_THICKNESS;
import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;
import static it.unibo.jnavy.view.utilities.ViewConstants.FOREGROUND_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.MENUBLUE;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import it.unibo.jnavy.view.utilities.SoundManager;

/**
 * Game start screen.
 */
public final class StartView extends JPanel {

    private static final Color DESC_LABEL_FOREGROUND = new Color(180, 180, 200);
    private static final Color LOAD_BUTTON_BG = new Color(60, 60, 80);
    private static final String SOUND_PATH = "/sounds/ship_horn.wav";
    private static final String TITLE = "Dominate the Ocean. Sink the Enemy.";
    private static final String START_GAME = "NEW GAME";

    private static final int EMPY_BORDER_TOP = 10;
    private static final int EMPY_BORDER_LEFT = 40;
    private static final int EMPY_BORDER_RIGHT = 40;
    private static final int EMPY_BORDER_BOTTOM = 10;

    private static final int INSET_10 = 10;
    private static final int INSET_20 = 20;
    private static final int INSET_50 = 50;

    private static final int TITLE_FONT_SIZE = 100;
    private static final int SUBTITLE_FONT_SIZE = 24;
    private static final int START_BTN_FONT_SIZE = 28;
    private static final int LOAD_BTN_FONT_SIZE = 22;

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final transient Runnable onStartAction;
    private final transient Runnable onLoadAction;
    private transient SoundManager ambientSound;

    /**
     * Constructs the StartView for the game.
     *
     * @param onStartAction the action to execute when the start button is clicked
     * @param onLoadAction  the action to execute when the load button is clicked
     */
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
            public void componentShown(final ComponentEvent e) {
                ambientSound = new SoundManager(SOUND_PATH);
                ambientSound.start();
            }

            @Override
            public void componentHidden(final ComponentEvent e) {
                if (ambientSound != null) {
                    ambientSound.close();
                }
            }
        });

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(INSET_10, 0, INSET_20, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        final JLabel titleLabel = new JLabel("JNavy");
        titleLabel.setFont(new Font("Impact", Font.BOLD, TITLE_FONT_SIZE));
        titleLabel.setForeground(FOREGROUND_COLOR);
        this.add(titleLabel, gbc);

        final JLabel descLabel = new JLabel(TITLE);
        descLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, SUBTITLE_FONT_SIZE));
        descLabel.setForeground(DESC_LABEL_FOREGROUND);
        this.add(descLabel, gbc);

        gbc.insets = new Insets(INSET_50, 0, 0, 0);

        final JButton startButton = new JButton(START_GAME);
        startButton.setFont(new Font(FONT_FAMILY, Font.BOLD, START_BTN_FONT_SIZE));
        startButton.setForeground(FOREGROUND_COLOR);
        startButton.setBackground(MENUBLUE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, BORDER_THICKNESS),
                BorderFactory.createEmptyBorder(EMPY_BORDER_TOP, EMPY_BORDER_LEFT, EMPY_BORDER_BOTTOM, EMPY_BORDER_RIGHT)
        ));
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        startButton.addActionListener(e -> {
            if (onStartAction != null) {
                onStartAction.run();
            }
        });

        this.add(startButton, gbc);

        gbc.insets = new Insets(INSET_20, 0, 0, 0);

        final JButton loadButton = new JButton("LOAD GAME");
        loadButton.setFont(new Font(FONT_FAMILY, Font.BOLD, LOAD_BTN_FONT_SIZE));
        loadButton.setForeground(Color.WHITE);
        loadButton.setBackground(LOAD_BUTTON_BG);
        loadButton.setFocusPainted(false);
        loadButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, BORDER_THICKNESS),
                BorderFactory.createEmptyBorder(EMPY_BORDER_TOP, EMPY_BORDER_LEFT, EMPY_BORDER_BOTTOM, EMPY_BORDER_RIGHT)
        ));
        loadButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        loadButton.addActionListener(e -> {
            if (onLoadAction != null) {
                onLoadAction.run();
            }
        });

        this.add(loadButton, gbc);
    }

    /**
     * Initializes and starts the background ambient sound for the start screen.
     */
    public void startMusic() {
        if (ambientSound == null) {
            ambientSound = new SoundManager(SOUND_PATH);
        }
        ambientSound.start();
    }

    /**
     * Stops the background ambient sound and releases its resources.
     */
    public void stopMusic() {
        if (ambientSound != null) {
            ambientSound.close();
            ambientSound = null;
        }
    }
}
