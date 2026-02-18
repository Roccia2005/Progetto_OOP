package it.unibo.jnavy.view.components.captain;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import java.io.IOException;

public class CaptainAbilityButton extends JButton {

    private static final String BUTTON_TEXT = "Ability";
    private static final String CAPTAIN_IMAGE_PATH = "/images/captain.png"; 
    private static final String ALERT_IMAGE_PATH = "/images/alert.png";
    private static final String READY = "READY";
    private static final String ACTIVE = "ACTIVE";
    private static final String POPUP_MESSAGE = "Abilità del Capitano attivata! Scegli la posizione.";
    private static final double MAX_PERCENTAGE = 1.0;
    private static final int DIMENSION = 100;

    private final int maxCooldown;
    private double fillPercentage = 0.0;
    private boolean isActive = false;

    public CaptainAbilityButton(int maxCooldown) {
        super(BUTTON_TEXT);
        this.maxCooldown = maxCooldown;
        
        setForeground(Color.WHITE);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false); 
        setOpaque(false);  
                
        this.setPreferredSize(new Dimension(DIMENSION, DIMENSION));

        Icon captainIcon = loadIcon(CAPTAIN_IMAGE_PATH, 64);
        if (captainIcon != null) {
            this.setIcon(captainIcon);
        }

        this.setVerticalTextPosition(SwingConstants.BOTTOM);
        this.setHorizontalTextPosition(SwingConstants.CENTER);
        this.setIconTextGap(5);
    }

    public void updateState(int currentCooldown) {
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
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow == null) {
            return;
        }

        JDialog popup = new JDialog(parentWindow, Dialog.ModalityType.MODELESS);
        popup.setUndecorated(true);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(new Color(40, 40, 40)); 
        contentPanel.setBorder(new LineBorder(new Color(255, 140, 0), 3)); 

        JLabel messageLabel = new JLabel(POPUP_MESSAGE, SwingConstants.CENTER);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        Icon alertIcon = loadIcon(ALERT_IMAGE_PATH, 64);
        if (alertIcon != null) {
            messageLabel.setIcon(alertIcon);
        }

        messageLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        messageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        contentPanel.add(messageLabel, BorderLayout.CENTER);
        popup.add(contentPanel);

        popup.pack();
        popup.setLocationRelativeTo(parentWindow);

        Timer timer = new Timer(1500, e -> popup.dispose());
        timer.setRepeats(false);
        timer.start();

        popup.setVisible(true);
    }

    private Icon loadIcon(String path, int size) {
        try {
            URL imageUrl = getClass().getResource(path);
            if (imageUrl != null) {
                BufferedImage localImg = ImageIO.read(imageUrl);
                if (localImg != null) {
                    Image scaledImg = localImg.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImg);
                }
            } 
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, width, height);

        if (isActive) {
            g2d.setColor(Color.GREEN);
        } else if (fillPercentage >= MAX_PERCENTAGE) {
            g2d.setColor(Color.BLUE);
        } else {
            g2d.setColor(Color.DARK_GRAY);
        }

        int fillHeight = (int) (height * fillPercentage);
        int yStart = height - fillHeight;
        g2d.fillRect(0, yStart, width, fillHeight);

        super.paintComponent(g);
    }
}