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

    private int targetSize = 0;
    private Point targetTopLeft;

    private final Timer animationTimer;
    private final int SPEED = 20;

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
        if (this.isAnimating) return;

        this.targetTopLeft = SwingUtilities.convertPoint(targetBtn.getParent(), targetBtn.getLocation(), this);
        this.targetSize = targetBtn.getWidth();
        int scaledBulletWidth = (int)(this.targetSize * 0.4);

        this.bulletX = this.targetTopLeft.x + (this.targetSize - scaledBulletWidth) / 2;
        this.bulletY = -scaledBulletWidth * 2;
        this.targetY = this.targetTopLeft.y + (this.targetSize / 2) - (scaledBulletWidth / 2);

        this.currentEffect = isHit ? this.explosionGif : this.splashGif;

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
        this.animationTimer.stop();

        Timer effectDuration = new Timer(1000, e -> {
            this.isAnimating = false;
            this.currentEffect = null;
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

        if (!this.isAnimating && this.currentEffect == null || this.targetSize == 0) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (this.animationTimer.isRunning() && this.bulletImg != null) {
            int bulletW = (int) (this.targetSize * 0.4);
            int bulletH = (int) (bulletW * 1.5);
            g2.drawImage(this.bulletImg, this.bulletX, this.bulletY, bulletW, bulletH, this);
        } else if (this.currentEffect != null) {
            g2.drawImage(this.currentEffect, this.targetTopLeft.x, this.targetTopLeft.y, this.targetSize, this.targetSize, this);
        }
    }
}
