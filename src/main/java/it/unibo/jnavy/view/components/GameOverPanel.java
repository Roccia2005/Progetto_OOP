package it.unibo.jnavy.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameOverPanel extends JPanel {

    private final Image winImage;
    private final Image loseImage;
    private boolean isWin;

    private final JButton menuButton;
    private final JButton exitButton;

    public GameOverPanel(ActionListener onMenu, ActionListener onExit) {
        this.setOpaque(false);
        setLayout(new GridBagLayout());
        setVisible(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { }
        });

        this.winImage = loadImage("winner.png");
        this.loseImage = loadImage("loser.png");

        this.menuButton = createStyledButton("Back to the menu");
        this.exitButton = createStyledButton("Exit the game");

        this.menuButton.addActionListener(onMenu);
        this.exitButton.addActionListener(onExit);
    }

    private Image loadImage(final String filename) {

    }

    private JButton createStyledButton(final String label) {

    }
}
