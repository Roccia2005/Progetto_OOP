package it.unibo.jnavy.view.components.bot;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Font;

import static it.unibo.jnavy.view.utilities.ViewConstants.*;

public class BotDifficultyPanel extends JPanel {

    public BotDifficultyPanel(final String difficultyName) {
        super(new FlowLayout(FlowLayout.CENTER, 15, 0));
        this.setOpaque(false);

        final JLabel difficultyLabel = new JLabel("Difficulty: " + difficultyName);
        difficultyLabel.setForeground(FOREGROUND_COLOR);
        difficultyLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));

        this.add(difficultyLabel);
    }
}