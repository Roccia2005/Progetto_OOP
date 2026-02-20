package it.unibo.jnavy.view.components;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

/**
 * A transparent panel that sits on top of the game view to draw animations
 * like cannonball shots and impact effects.
 */
public class EffectsPanel extends JPanel {

    private static final int ANIMATION_DELAY_MS = 15;
    private static final int EFFECT_DURATION_MS = 1000;
    private static final int BULLET_SPEED = 10;

    private Image bulletImg;
    private Image explosionGif;
    private Image splashGif;

    private boolean isAnimating = false;
    private boolean bulletVisible = false;

    private int bulletX, bulletY;
    private int targetY;
    private Image currentEffect = null;

    private int targetCenterX;
    private int targetCenterY;
    private int effectRenderSize;
    private int currentBulletW;
    private int currentBulletH;

    private Runnable onImpactCallback;
    private Runnable onCompleteCallback;

    private final Timer animationTimer;

    /**
     * Constructs a new EffectsPanel.
     * The panel is set to be transparent and initializes the images used for the effects.
     */
    public EffectsPanel() {
        this.setOpaque(false);
        loadImages();
        this.animationTimer = new Timer(ANIMATION_DELAY_MS, e -> updateAnimation());
    }

    /**
     * Loads the images required for the effects from classpath resources.
     */
    private void loadImages() {
        this.bulletImg    = tryLoad("cannonball.png");
        this.explosionGif = tryLoad("explosion.gif");
        this.splashGif    = tryLoad("watersplash.gif");
    }

    /**
     * Safely loads an image from the given filename within the `resources/images/` directory.
     *
     * @param filename The name of the image file to load.
     * @return The loaded image, or null if the resource was not found.
     */
    private Image tryLoad(final String filename) {
        URL url = getClass().getResource("/images/" + filename);
        if (url == null) {
            System.err.println("Critical error: image not found: /images/" + filename + "");
            return null;
        }
        return new ImageIcon(url).getImage();
    }

    /**
     * Starts the shot animation towards a target component.
     * For multiple targets (AreaShot), the bullet will aim at the center of the bounding box.
     *
     * @param targets The list of components to aim at.
     * @param isHit If true, an explosion is shown; otherwise a splash
     * @param onImpact The callback to be invoked when the bullet impacts a target.
     * @param onComplete The callback to be invoked when the shot animation is completed.
     */
    public void startShot(List<Component> targets, boolean isHit, Runnable onImpact, Runnable onComplete) {
        if (this.isAnimating || targets.isEmpty()) return;

        this.onImpactCallback = onImpact;
        this.onCompleteCallback = onComplete;

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        Point panelLocation = this.getLocationOnScreen();

        for (Component t : targets) {
            Point loc = t.getLocationOnScreen();
            int rx = loc.x - panelLocation.x;
            int ry = loc.y - panelLocation.y;
            minX = Math.min(minX, rx);
            minY = Math.min(minY, ry);
            maxX = Math.max(maxX, rx + t.getWidth());
            maxY = Math.max(maxY, ry + t.getHeight());
        }

        this.targetCenterX = minX + (maxX - minX) / 2;
        this.targetCenterY = minY + (maxY - minY) / 2;

        int rawSize = Math.max(maxX - minX, maxY - minY);
        boolean isAreaShot = targets.size() > 1;

        this.effectRenderSize = isAreaShot ? rawSize : (int) (rawSize * 1.5);
        int baseCellWidth = targets.get(0).getWidth();
        this.currentBulletW = isAreaShot ? (int) (baseCellWidth * 0.8) : (int) (baseCellWidth * 0.4);
        this.currentBulletH = (int) (this.currentBulletW * 1.5);

        this.bulletX = targetCenterX - (currentBulletW / 2);
        this.bulletY = -currentBulletH * 2;
        this.targetY = targetCenterY - (currentBulletH / 2);

        this.currentEffect = isHit ? this.explosionGif : this.splashGif;
        this.bulletVisible = true;
        this.isAnimating   = true;

        this.animationTimer.start();
    }

    /**
     * Updates the bullet position during its flight.
     * This method is called repeatedly by the animation timer.
     */
    private void updateAnimation() {
        if (this.bulletY < this.targetY) {
            this.bulletY += BULLET_SPEED;
            repaint();
        } else {
            triggerImpact();
        }
    }

    /**
     * Handles the moment of impact. It stops the bullet, triggers the onImpact callback,
     * and starts displaying the appropriate effect.
     */
    private void triggerImpact() {
        this.bulletVisible = false;
        this.animationTimer.stop();
        if (onImpactCallback != null) {
            onImpactCallback.run();
        }

        repaint();

        Timer effectDuration = new Timer(EFFECT_DURATION_MS, e -> {
            this.isAnimating   = false;
            this.currentEffect = null;
            repaint();
            ((Timer) e.getSource()).stop();

            if (onCompleteCallback != null) {
                onCompleteCallback.run();
            }
        });
        effectDuration.setRepeats(false);
        effectDuration.start();
    }

    /**
     * Overridden methos to handle custom painting for the component.
     * It draws either the moving bullet or the impact effect.
     *
     * @param g The Graphics context to paint on.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if ((!isAnimating && currentEffect == null) || effectRenderSize == 0) return;

        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Phase 1: Draw a bullet if it's visible
        if (bulletVisible) {
            if (bulletImg != null) {
                g2.drawImage(bulletImg, bulletX, bulletY, currentBulletW, currentBulletH, this);
            } else {
                // Fallback rendering if the image failed to load
                g2.setColor(Color.RED);
                g2.fillRect(bulletX, bulletY, currentBulletW, currentBulletH);
                g2.setColor(Color.WHITE);
                g2.drawRect(bulletX, bulletY, currentBulletW, currentBulletH);
            }
        } else if (currentEffect != null) {
            // Phase 2: Draw the impact effect
            int gifSize = (int) (effectRenderSize * 1.5);
            int drawX = targetCenterX - (gifSize / 2);
            int drawY = targetCenterY - (gifSize / 2);
            g2.drawImage(currentEffect, drawX, drawY, gifSize, gifSize, this);
        }
    }
}
