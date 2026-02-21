package it.unibo.jnavy.view.components.bot;

import javax.swing.JLabel;
import javax.swing.JPanel;

import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;
import static it.unibo.jnavy.view.utilities.ViewConstants.FOREGROUND_COLOR;

import java.awt.FlowLayout;
import java.awt.Font;

public class BotDifficultyPanel extends JPanel {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    public BotDifficultyPanel(final String difficultyName) {
        super(new FlowLayout(FlowLayout.CENTER, 15, 0));
        this.setOpaque(false);

        final JLabel difficultyLabel = new JLabel("Difficulty: " + difficultyName);
        difficultyLabel.setForeground(FOREGROUND_COLOR);
        difficultyLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));

        this.add(difficultyLabel);
    }
}