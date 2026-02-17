package it.unibo.jnavy.view.components;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

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

    private int targetSize = 0;
    private Point targetTopLeft;

    private Runnable onImpactCallback;

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
        } else {
            System.out.println("✅ Immagine caricata correttamente: " + filename);
            return new ImageIcon(url).getImage();
        }
    }

    /**
     * Starts the shot animation towards a target component.
     *
     * @param targetBtn The component to target.
     * @param isHit If true, an explosion effect is shown on impact; otherwise a splash effect.
     * @param onImpact A callback to be executed at the moment of impact.
     */
    public void startShot(Component targetBtn, boolean isHit, Runnable onImpact) {
        if (this.isAnimating) return;
        this.onImpactCallback = onImpact;

        Point btnLocationOnScreen   = targetBtn.getLocationOnScreen();
        Point panelLocationOnScreen = this.getLocationOnScreen();
        this.targetTopLeft = new Point(
                btnLocationOnScreen.x - panelLocationOnScreen.x,
                btnLocationOnScreen.y - panelLocationOnScreen.y
        );

        this.targetSize = targetBtn.getWidth();
        int scaledBulletWidth = (int) (this.targetSize * 0.4);

        this.bulletX = this.targetTopLeft.x + (this.targetSize - scaledBulletWidth) / 2;
        this.bulletY = -scaledBulletWidth * 2;   // parte da sopra la finestra
        this.targetY = this.targetTopLeft.y + (this.targetSize / 2) - (scaledBulletWidth / 2);

        this.currentEffect = isHit ? this.explosionGif : this.splashGif;
        this.bulletVisible = true;
        this.isAnimating   = true;

        this.animationTimer.start();
    }

    /**
     * Updates the bullets position during its flight.
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

        Timer effectDuration = new Timer(1000, e -> {
            this.isAnimating   = false;
            this.currentEffect = null;
            repaint();
            ((Timer) e.getSource()).stop();
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

        if ((!isAnimating && currentEffect == null) || targetSize == 0) return;

        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Phase 1: Draw bullet if it's visible
        if (bulletVisible) {
            int bulletW = (int) (targetSize * 0.6);
            int bulletH = (int) (bulletW * 1.5);

            if (bulletImg != null) {
                g2.drawImage(bulletImg, bulletX, bulletY, bulletW, bulletH, this);
            } else {
                // Fallback rendering if the image failed to load
                g2.setColor(Color.RED);
                g2.fillRect(bulletX, bulletY, bulletW, bulletH);
                g2.setColor(Color.WHITE);
                g2.drawRect(bulletX, bulletY, bulletW, bulletH);
            }
        } else if (currentEffect != null) {
            // Phase 2: Draw the impact effect
            int gifSize = (int) (targetSize * 1.5);
            int gifOffset = (targetSize - gifSize) / 2;
            g2.drawImage(currentEffect,
                    targetTopLeft.x + gifOffset, targetTopLeft.y + gifOffset,
                    gifSize, gifSize, this);
        }
    }
}
