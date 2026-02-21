package it.unibo.jnavy.view.components;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A transparent panel that sits on top of the game view to draw animations
 * like cannonball shots and impact effects.
 */
public final class EffectsPanel extends JPanel {

    private static final int ANIMATION_DELAY_MS = 15;
    private static final int EFFECT_DURATION_MS = 1000;
    private static final int BULLET_SPEED = 10;

    private static final double SINGLE_TARGET_EFFECT_SCALE = 1.5;
    private static final double AREA_BULLET_WIDTH_SCALE = 0.8;
    private static final double SINGLE_BULLET_WIDTH_SCALE = 0.4;
    private static final double BULLET_HEIGHT_SCALE = 1.5;
    private static final double GIF_SIZE_SCALE = 1.5;

    @java.io.Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EffectsPanel.class.getName());

    private transient Image bulletImg;
    private transient Image explosionGif;
    private transient Image splashGif;

    private boolean isAnimating;
    private boolean bulletVisible;

    private int bulletX;
    private int bulletY;
    private int targetY;
    private transient Image currentEffect;

    private int targetCenterX;
    private int targetCenterY;
    private int effectRenderSize;
    private int currentBulletW;
    private int currentBulletH;

    private transient Runnable onImpactCallback;
    private transient Runnable onCompleteCallback;

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
        this.bulletImg = tryLoad("cannonball.png");
        this.explosionGif = tryLoad("explosion.gif");
        this.splashGif = tryLoad("watersplash.gif");
    }

    /**
     * Safely loads an image from the given filename within the `resources/images/` directory.
     *
     * @param filename The name of the image file to load.
     * @return The loaded image, or null if the resource was not found.
     */
    private Image tryLoad(final String filename) {
        final String fullPath = "/images/" + filename;
        final URL url = getClass().getResource(fullPath);
        if (url == null) {
            LOGGER.log(Level.SEVERE, "Critical error: image not found: {0}", fullPath);
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
    public void startShot(final List<Component> targets, final boolean isHit,
                          final Runnable onImpact, final Runnable onComplete) {
        if (this.isAnimating || targets.isEmpty()) {
            return;
        }

        this.onImpactCallback = onImpact;
        this.onCompleteCallback = onComplete;

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        final Point panelLocation = this.getLocationOnScreen();

        for (final Component t : targets) {
            final Point loc = t.getLocationOnScreen();
            final int rx = loc.x - panelLocation.x;
            final int ry = loc.y - panelLocation.y;
            minX = Math.min(minX, rx);
            minY = Math.min(minY, ry);
            maxX = Math.max(maxX, rx + t.getWidth());
            maxY = Math.max(maxY, ry + t.getHeight());
        }

        this.targetCenterX = minX + (maxX - minX) / 2;
        this.targetCenterY = minY + (maxY - minY) / 2;

        final int rawSize = Math.max(maxX - minX, maxY - minY);
        final boolean isAreaShot = targets.size() > 1;

        this.effectRenderSize = isAreaShot ? rawSize : (int) (rawSize * SINGLE_TARGET_EFFECT_SCALE);
        final int baseCellWidth = targets.get(0).getWidth();
        this.currentBulletW = isAreaShot ? (int) (baseCellWidth * AREA_BULLET_WIDTH_SCALE)
                                         : (int) (baseCellWidth * SINGLE_BULLET_WIDTH_SCALE);
        this.currentBulletH = (int) (this.currentBulletW * BULLET_HEIGHT_SCALE);

        this.bulletX = targetCenterX - (currentBulletW / 2);
        this.bulletY = -currentBulletH * 2;
        this.targetY = targetCenterY - (currentBulletH / 2);

        this.currentEffect = isHit ? this.explosionGif : this.splashGif;
        this.bulletVisible = true;
        this.isAnimating = true;

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

        final Timer effectDuration = new Timer(EFFECT_DURATION_MS, e -> {
            this.isAnimating = false;
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
     * Overridden method to handle custom painting for the component.
     * It draws either the moving bullet or the impact effect.
     *
     * @param g The Graphics context to paint on.
     */
    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        if (!isAnimating && currentEffect == null || effectRenderSize == 0) {
            return;
        }

        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
            final int gifSize = (int) (effectRenderSize * GIF_SIZE_SCALE);
            final int drawX = targetCenterX - (gifSize / 2);
            final int drawY = targetCenterY - (gifSize / 2);
            g2.drawImage(currentEffect, drawX, drawY, gifSize, gifSize, this);
        }
    }
}
