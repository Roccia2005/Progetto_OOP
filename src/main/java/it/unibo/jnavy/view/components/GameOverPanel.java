package it.unibo.jnavy.view.components;

import it.unibo.jnavy.view.utilities.ImageLoader;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static it.unibo.jnavy.view.utilities.ViewConstants.BORDER_THICKNESS;
import static it.unibo.jnavy.view.utilities.ViewConstants.BTN_PADDING_H;
import static it.unibo.jnavy.view.utilities.ViewConstants.BTN_PADDING_V;
import static it.unibo.jnavy.view.utilities.ViewConstants.BUTTON_BLUE;
import static it.unibo.jnavy.view.utilities.ViewConstants.BUTTON_HOVER_BLUE;
import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;
import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_SIZE_DESC;
import static it.unibo.jnavy.view.utilities.ViewConstants.FOREGROUND_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.GAMEOVER_IMG_WIDTH;
import static it.unibo.jnavy.view.utilities.ViewConstants.GAMEOVER_INSET_L;
import static it.unibo.jnavy.view.utilities.ViewConstants.GAMEOVER_INSET_S;
import static it.unibo.jnavy.view.utilities.ViewConstants.OVERLAY_COLOR;

/**
 * A panel that displays the final game result (victory or defeat).
 * It provides a semi-transparent overlay covering the game area and features
 * themed icons and navigation buttons to return to the menu or exit the application.
 */
public final class GameOverPanel extends JPanel {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final ImageIcon winIcon;
    private final ImageIcon loseIcon;
    private final JLabel imageLabel;

    private final JButton menuButton;
    private final JButton exitButton;

    /**
     * Constructs a new {@code GameOverPanel} with the specified action listeners.
     * The panel is initially hidden and blocks mouse events to prevent interaction
     * with the underlying game grid.
     *
     * @param onMenu the action to execute when the "Back to Menu" button is clicked.
     * @param onExit the action to execute when the "Exit" button is clicked.
     */
    public GameOverPanel(final ActionListener onMenu, final ActionListener onExit) {
        this.setOpaque(false);
        setLayout(new GridBagLayout());
        setVisible(false);

        // Blocks mouse events from reaching the panels below
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) { }
        });

        this.winIcon = createScaledIcon("/images/winner.png", GAMEOVER_IMG_WIDTH);
        this.loseIcon = createScaledIcon("/images/loser.png", GAMEOVER_IMG_WIDTH);

        this.imageLabel = new JLabel();

        this.menuButton = createStyledButton("Back to the menu");
        this.exitButton = createStyledButton("Exit the game");

        this.menuButton.addActionListener(onMenu);
        this.exitButton.addActionListener(onExit);
    }

    /**
     * Updates the panel with the game result and makes it visible.
     * It adjusts the layout and the displayed icon based on the outcome.
     *
     * @param victory {@code true} if the player won, {@code false} if they lost.
     */
    public void showResult(final boolean victory) {
        this.imageLabel.setIcon(victory ? winIcon : loseIcon);
        removeAll();

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        final int bottomMargin = victory ? GAMEOVER_INSET_L : GAMEOVER_INSET_S;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, bottomMargin, 0);
        add(imageLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, GAMEOVER_INSET_S, 0);
        add(menuButton, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(exitButton, gbc);

        revalidate();
        repaint();
        setVisible(true);
    }

    /**
     * Overrides the default painting behavior to draw a semi-transparent black overlay
     * covering the entire panel area.
     *
     * @param g the Graphics object used for drawing.
     */
    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        g2.setColor(OVERLAY_COLOR);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private ImageIcon createScaledIcon(final String path, final int targetWidth) {
        final Image img = ImageLoader.getImage(path);
        if (img == null) {
            return null;
        }
        final int targetHeight = (int) ((double) img.getHeight(null) / img.getWidth(null) * targetWidth);
        final Image scaledImg = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    private JButton createStyledButton(final String label) {
        final JButton button = new JButton(label);
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_DESC));
        button.setForeground(FOREGROUND_COLOR);
        button.setBackground(BUTTON_BLUE);
        button.setFocusPainted(false);

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FOREGROUND_COLOR, BORDER_THICKNESS),
                BorderFactory.createEmptyBorder(BTN_PADDING_V, BTN_PADDING_H, BTN_PADDING_V, BTN_PADDING_H)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(final MouseEvent evt) {
                button.setBackground(BUTTON_HOVER_BLUE);
            }

            @Override
            public void mouseExited(final MouseEvent evt) {
                button.setBackground(BUTTON_BLUE);
            }
        });
        return button;
    }
}
