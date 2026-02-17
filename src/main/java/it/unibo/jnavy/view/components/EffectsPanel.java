package it.unibo.jnavy.view.components;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class EffectsPanel extends JPanel {

    private final Image bulletImg;
    private final Image explosionGif;
    private final Image splashGif;

    private boolean isAnimating = false;
    private int bulletX, bulletY;
    private int targetY;
    private Image currentEffect = null;

    private final Timer animationTimer;
    private final int SPEED = 20;
    private final int YSTART = -50;

    public EffectsPanel() {
        this.setOpaque(false);

        this.bulletImg = loadImage("cannonball.png");
        this.explosionGif = loadImage("explosion.gif");
        this.splashGif = loadImage("watersplash.gif");

        this.animationTimer = new Timer(15, e -> updateAnimation());
    }

    private Image loadImage(final String filename) {
        URL url = getClass().getResource("/images/" + filename);
        if (url == null) {
            return null;
        }
        return new ImageIcon(url).getImage();
    }

    public void startShot(Component targetBtn, boolean isHit) {
        if (isAnimating) return;

        Point p = SwingUtilities.convertPoint(targetBtn.getParent(), targetBtn.getLocation(), this);
        int cellSize = targetBtn.getWidth();
        bulletX = p.x + (cellSize / 2) - 15;
        bulletY = this.YSTART;
        targetY = p.y + (cellSize / 2) - 20;

        this.currentEffect = isHit ? explosionGif : splashGif;

        this.isAnimating = true;
        this.animationTimer.start();
    }

    private void updateAnimation() {
        if (this.bulletY < this.targetY) {
            this.bulletY += SPEED;
            repaint();
        } else {
            triggerImpact();
        }
    }

    private void triggerImpact() {
        animationTimer.stop();

        Timer effectDuration = new Timer(1000, e -> {
            isAnimating = false;
            currentEffect = null;
            repaint();
            ((Timer)e.getSource()).stop();
        });
        effectDuration.setRepeats(false);
        effectDuration.start();

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!this.isAnimating && this.currentEffect == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (this.animationTimer.isRunning()) {
            if (this.bulletImg != null) {
                g2.drawImage(this.bulletImg, this.bulletX, this.bulletY, 30, 40, this);
            }
        } else if (this.currentEffect != null) {
            g2.drawImage(this.currentEffect, this.bulletX - 15, this.bulletY - 10, 60, 60, this);
        }
    }
}
