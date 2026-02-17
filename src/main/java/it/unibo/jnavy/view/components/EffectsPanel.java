package it.unibo.jnavy.view.components;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class EffectsPanel extends JPanel {

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

    private final Timer animationTimer;
    private final int SPEED = 15;

    public EffectsPanel() {
        this.setOpaque(false);
        loadImages();
        this.animationTimer = new Timer(15, e -> updateAnimation());
    }

    private void loadImages() {
        this.bulletImg    = tryLoad("cannonball.png");
        this.explosionGif = tryLoad("explosion.gif");
        this.splashGif    = tryLoad("watersplash.gif");
    }

    private Image tryLoad(final String filename) {
        URL url = getClass().getResource("/images/" + filename);
        if (url == null) {
            System.err.println("❌ ERRORE CRITICO: Non trovo l'immagine: " + filename);
            System.err.println("   Controlla in src/main/resources/images/");
            return null;
        } else {
            System.out.println("✅ Immagine caricata correttamente: " + filename);
            return new ImageIcon(url).getImage();
        }
    }

    public void startShot(Component targetBtn, boolean isHit) {
        if (this.isAnimating) return;

        // FIX: usa getLocationOnScreen per evitare problemi di conversione
        // con JLayeredPane e null layout
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

        System.out.println("--- INIZIO SPARO ---");
        System.out.println("targetTopLeft=" + targetTopLeft);
        System.out.println("bulletX=" + bulletX + "  bulletY iniziale=" + bulletY);
        System.out.println("targetY=" + targetY + "  differenza=" + (targetY - bulletY));

        // Resetta stato precedente e imposta effetto
        this.currentEffect = isHit ? this.explosionGif : this.splashGif;
        this.bulletVisible = true;
        this.isAnimating   = true;

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
        // Nascondi il proiettile PRIMA di fermare il timer
        this.bulletVisible = false;
        this.animationTimer.stop();
        System.out.println("💥 IMPATTO! Mostro l'effetto.");

        repaint(); // disegna l'effetto (esplosione o splash)

        Timer effectDuration = new Timer(1000, e -> {
            this.isAnimating   = false;
            this.currentEffect = null;
            repaint();
            ((Timer) e.getSource()).stop();
        });
        effectDuration.setRepeats(false);
        effectDuration.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if ((!isAnimating && currentEffect == null) || targetSize == 0) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // FASE 1: PROIETTILE IN VOLO
        if (bulletVisible) {
            int bulletW = (int) (targetSize * 0.4);
            int bulletH = (int) (bulletW * 1.5);

            if (bulletImg != null) {
                g2.drawImage(bulletImg, bulletX, bulletY, bulletW, bulletH, this);
            } else {
                // Fallback grafico se l'immagine non è stata trovata
                g2.setColor(Color.RED);
                g2.fillRect(bulletX, bulletY, bulletW, bulletH);
                g2.setColor(Color.WHITE);
                g2.drawRect(bulletX, bulletY, bulletW, bulletH);
            }
        }
        // FASE 2: EFFETTO IMPATTO (esplosione o splash)
        else if (currentEffect != null) {
            g2.drawImage(currentEffect,
                    targetTopLeft.x, targetTopLeft.y,
                    targetSize, targetSize, this); // null evita il loop infinito delle GIF
        }
    }
}
