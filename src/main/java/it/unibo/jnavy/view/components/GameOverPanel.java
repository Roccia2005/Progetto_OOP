package it.unibo.jnavy.view.components;

import it.unibo.jnavy.view.utilities.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static it.unibo.jnavy.view.utilities.ViewConstants.*;

public class GameOverPanel extends JPanel {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final ImageIcon winIcon;
    private final ImageIcon loseIcon;
    private final JLabel imageLabel;

    private final JButton menuButton;
    private final JButton exitButton;

    public GameOverPanel(final ActionListener onMenu, final ActionListener onExit) {
        this.setOpaque(false);
        setLayout(new GridBagLayout());
        setVisible(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { }
        });

        this.winIcon = createScaledIcon("/images/winner.png", 400);
        this.loseIcon = createScaledIcon("/images/loser.png", 400);

        this.imageLabel = new JLabel();

        this.menuButton = createStyledButton("Back to the menu");
        this.exitButton = createStyledButton("Exit the game");

        this.menuButton.addActionListener(onMenu);
        this.exitButton.addActionListener(onExit);
    }

    public void showResult(final boolean victory) {
        this.imageLabel.setIcon(victory ? winIcon : loseIcon);
        removeAll();

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        final int bottomMargin = victory ? 40 : 15;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, bottomMargin, 0);
        add(imageLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        add(menuButton, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(exitButton, gbc);

        revalidate();
        repaint();
        setVisible(true);
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 0, 0, 230));
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private ImageIcon createScaledIcon(final String path, final int targetWidth) {
        final Image img = ImageLoader.getImage(path);
        if (img == null) {
            return null;
        }
        final int targetHeight = (int) ((double) img.getHeight(null) / img.getWidth(null) * targetWidth);
        final Image scaledImg = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    private JButton createStyledButton(final String label) {
        final JButton button = new JButton(label);
        button.setFont(new Font("Sanserif", Font.BOLD, 18));
        button.setForeground(FOREGROUND_COLOR);
        button.setBackground(new Color(30, 100, 255));
        button.setFocusPainted(false);

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(10, 40, 10, 40)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(60, 130, 255));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(30, 100, 255));
            }
        });
        return button;
    }
}
