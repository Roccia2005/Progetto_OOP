package it.unibo.jnavy.view.components.captain;

import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;
import static it.unibo.jnavy.view.utilities.ViewConstants.FOREGROUND_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.CAPTAIN_POPUP_BACKGROUND;
import static it.unibo.jnavy.view.utilities.ViewConstants.CAPTAIN_POPUP_BORDER;
import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_SIZE_DESC;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import it.unibo.jnavy.view.utilities.ImageLoader;

/**
 * A custom interactive button representing the captain's special ability.
 * It visually displays the cooldown progress through a dynamic background fill
 * and handles the activation state, providing visual feedback to the player.
 */
public final class CaptainAbilityButton extends JButton {

    private static final Color BUTTON_ACTIVE = Color.GREEN;
    private static final Color BUTTON_CHARGED = Color.BLUE;
    private static final Color BUTTON_RECHARGING = Color.CYAN;

    private static final String BUTTON_TEXT = "Ability";
    private static final String CAPTAIN_IMAGE_PATH = "/images/captain.png";
    private static final String ALERT_IMAGE_PATH = "/images/alert.png";
    private static final int IMAGE_DIMENSION = 64;

    private static final String READY = "READY";
    private static final String ACTIVE = "ACTIVE";
    private static final String POPUP_MESSAGE = "Ability activate! Choose the position.";
    private static final double MAX_PERCENTAGE = 1.0;
    private static final int DIMENSION = 100;
    private static final int TEXT_GAP = 5;
    private static final int TIMER_TIME = 1500;

    private static final int POPUP_HGAP = 20;
    private static final int POPUP_VGAP = 20;
    private static final int POPUP_BORDER_THICKNESS = 3;
    private static final int POPUP_INSET_TOP = 30;
    private static final int POPUP_INSET_LEFT = 40;
    private static final int POPUP_INSET_BOTTOM = 30;
    private static final int POPUP_INSET_RIGHT = 40;

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final int maxCooldown;
    private double fillPercentage;
    private boolean isActive;

    /**
     * Constructs a new {@code CaptainAbilityButton}.
     *
     * @param maxCooldown the maximum number of turns required to fully recharge the ability.
     */
    public CaptainAbilityButton(final int maxCooldown) {
        super(BUTTON_TEXT);
        this.maxCooldown = maxCooldown;

        setForeground(FOREGROUND_COLOR);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);

        this.setPreferredSize(new Dimension(DIMENSION, DIMENSION));

        final Icon captainIcon = ImageLoader.getScaledIcon(CAPTAIN_IMAGE_PATH, IMAGE_DIMENSION, IMAGE_DIMENSION);
        if (captainIcon != null) {
            this.setIcon(captainIcon);
        }

        this.setVerticalTextPosition(BOTTOM);
        this.setHorizontalTextPosition(CENTER);
        this.setIconTextGap(TEXT_GAP);
    }

    /**
     * Updates the button's visual state based on the current cooldown.
     * The method calculates the fill percentage and updates the text to display either
     * the remaining turns or the "READY" status.
     *
     * @param currentCooldown the current cooldown value.
     */
    public void updateState(final int currentCooldown) {
        if (maxCooldown > 0) {
            this.fillPercentage = (double) currentCooldown / this.maxCooldown;
            this.fillPercentage = Math.min(this.fillPercentage, MAX_PERCENTAGE);
        } else {
            this.fillPercentage = MAX_PERCENTAGE;
        }

        setEnabled(this.fillPercentage >= MAX_PERCENTAGE);

        if (this.fillPercentage >= MAX_PERCENTAGE) {
            setText(isActive ? ACTIVE : READY);
        } else {
            setText("(" + currentCooldown + "/" + this.maxCooldown + ")");
            this.isActive = false;
        }
        repaint();
    }

    /**
     * Toggles the active state of the ability.
     * If activated, it triggers a temporary visual feedback popup to notify the user.
     */
    public void select() {
        this.isActive = !this.isActive;
        if (this.isActive) {
            showFeedbackPopup();
        }
        repaint();
    }

    /**
     * Resets the button's state to inactive.
     * Typically used after an ability has been successfully deployed on the grid or canceled.
     */
    public void reset() {
        if (this.isActive) {
            this.isActive = false;
            repaint();
        }
    }

    /**
     * Checks if the captain's ability is currently selected and ready to be deployed.
     *
     * @return {@code true} if the ability is active, {@code false} otherwise.
     */
    public boolean isActive() {
        return this.isActive;
    }

    private void showFeedbackPopup() {
        final Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow == null) {
            return;
        }

        final JDialog popup = new JDialog(parentWindow, Dialog.ModalityType.MODELESS);
        popup.setUndecorated(true);

        final JPanel contentPanel = new JPanel(new BorderLayout(POPUP_HGAP, POPUP_VGAP));
        contentPanel.setBackground(CAPTAIN_POPUP_BACKGROUND);
        contentPanel.setBorder(new LineBorder(CAPTAIN_POPUP_BORDER, POPUP_BORDER_THICKNESS));

        final JLabel messageLabel = new JLabel(POPUP_MESSAGE, CENTER);
        messageLabel.setForeground(FOREGROUND_COLOR);
        messageLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_DESC));

        final Icon alertIcon = ImageLoader.getScaledIcon(ALERT_IMAGE_PATH, IMAGE_DIMENSION, IMAGE_DIMENSION);
        if (alertIcon != null) {
            messageLabel.setIcon(alertIcon);
        }

        messageLabel.setVerticalTextPosition(BOTTOM);
        messageLabel.setHorizontalTextPosition(CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(POPUP_INSET_TOP, POPUP_INSET_LEFT,
                                                                POPUP_INSET_BOTTOM, POPUP_INSET_RIGHT));

        contentPanel.add(messageLabel, BorderLayout.CENTER);
        popup.add(contentPanel);

        popup.pack();
        popup.setLocationRelativeTo(parentWindow);

        final Timer timer = new Timer(TIMER_TIME, e -> popup.dispose());
        timer.setRepeats(false);
        timer.start();

        popup.setVisible(true);
    }

    /**
     * Overrides the default painting behavior to draw a custom dynamic background fill
     * based on the current cooldown percentage.
     *
     * @param g the Graphics context used for painting.
     */
    @Override
    protected void paintComponent(final Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int width = getWidth();
        final int height = getHeight();

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, width, height);

        if (isActive) {
            g2d.setColor(BUTTON_ACTIVE);
        } else if (fillPercentage >= MAX_PERCENTAGE) {
            g2d.setColor(BUTTON_CHARGED);
        } else {
            g2d.setColor(BUTTON_RECHARGING);
        }

        final int fillHeight = (int) (height * fillPercentage);
        final int yStart = height - fillHeight;
        g2d.fillRect(0, yStart, width, fillHeight);

        super.paintComponent(g);
    }
}
