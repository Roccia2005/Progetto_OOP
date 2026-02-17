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
        
    }
}
