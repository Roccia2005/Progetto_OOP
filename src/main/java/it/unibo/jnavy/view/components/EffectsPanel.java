package it.unibo.jnavy.view.components;

import javax.swing.*;
import java.awt.*;

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

    public EffectsPanel() {
        this.setOpaque(false);

        this.bulletImg = loadImage("cannonball.png");
        this.explosionGif = loadImage("explosion.gif");
        this.splashGif = loadImage("watersplash.gif");

        this.animationTimer = new Timer(15, e -> updateAnimation());
    }
}
