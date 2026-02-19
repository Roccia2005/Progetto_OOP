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

    public void showResult(final boolean victory) {
        this.isWin = victory;
        removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridy = 0;
        add(Box.createVerticalStrut(350), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        add(menuButton, gbc);

        gbc.gridy = 2;
        add(exitButton, gbc);

        revalidate();
        repaint();
        setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, getWidth(), getHeight());

        Image imgToDraw = isWin ? winImage : loseImage;

        if (imgToDraw != null) {
            int targetW = 400;
            int targetH = (int) ((double)imgToDraw.getHeight(null) / imgToDraw.getWidth(null) * targetW);

            int x = (getWidth() - targetW) / 2;
            int y = (getHeight() / 2) - targetH - 50;

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(imgToDraw, x, y, targetW, targetH, this);
        }
    }

    private Image loadImage(final String filename) {

    }

    private JButton createStyledButton(final String label) {

    }

}
