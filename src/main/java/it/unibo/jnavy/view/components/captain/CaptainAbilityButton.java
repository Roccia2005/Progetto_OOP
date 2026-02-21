package it.unibo.jnavy.view.components.captain;

import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;
import static it.unibo.jnavy.view.utilities.ViewConstants.FOREGROUND_COLOR;

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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import it.unibo.jnavy.view.utilities.ImageLoader;

public class CaptainAbilityButton extends JButton {

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

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final int maxCooldown;
    private double fillPercentage = 0.0;
    private boolean isActive = false;

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

    public void select() {
        this.isActive = !this.isActive;
        if (this.isActive) {
            showFeedbackPopup();
        }
        repaint();
    }

    public void reset() {
        if (this.isActive) {
            this.isActive = false;
            repaint();
        }
    }

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

        final JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(new Color(40, 40, 40)); 
        contentPanel.setBorder(new LineBorder(new Color(255, 140, 0), 3)); 

        final JLabel messageLabel = new JLabel(POPUP_MESSAGE, CENTER);
        messageLabel.setForeground(FOREGROUND_COLOR);
        messageLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 18));
        
        final Icon alertIcon = ImageLoader.getScaledIcon(ALERT_IMAGE_PATH, IMAGE_DIMENSION, IMAGE_DIMENSION);
        if (alertIcon != null) {
            messageLabel.setIcon(alertIcon);
        }

        messageLabel.setVerticalTextPosition(BOTTOM);
        messageLabel.setHorizontalTextPosition(CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        contentPanel.add(messageLabel, BorderLayout.CENTER);
        popup.add(contentPanel);

        popup.pack();
        popup.setLocationRelativeTo(parentWindow);

        final Timer timer = new Timer(TIMER_TIME, e -> popup.dispose());
        timer.setRepeats(false);
        timer.start();

        popup.setVisible(true);
    }

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