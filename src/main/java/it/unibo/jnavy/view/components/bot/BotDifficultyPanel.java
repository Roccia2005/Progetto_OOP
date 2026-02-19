package it.unibo.jnavy.view.components.bot;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

public class BotDifficultyPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public BotDifficultyPanel(final String difficultyName) {
        super(new FlowLayout(FlowLayout.CENTER, 15, 0));
        this.setOpaque(false);

        final JLabel difficultyLabel = new JLabel("Difficulty: " + difficultyName);
        difficultyLabel.setForeground(Color.WHITE);
        difficultyLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        this.add(difficultyLabel);
    }
}