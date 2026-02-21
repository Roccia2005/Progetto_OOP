package it.unibo.jnavy.view.components.bot;

import javax.swing.JLabel;
import javax.swing.JPanel;

import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;
import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_SIZE_CTRL;
import static it.unibo.jnavy.view.utilities.ViewConstants.FOREGROUND_COLOR;

import java.awt.FlowLayout;
import java.awt.Font;

/**
 * A UI component that displays the selected difficulty level of the opponent bot.
 * This panel contains a formatted label indicating the current difficulty to the player.
 */
public final class BotDifficultyPanel extends JPanel {

    private static final int H_GAP = 15;
    private static final int V_GAP = 0;

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@code BotDifficultyPanel} with the specified difficulty name.
     *
     * @param difficultyName the string representing the bot's difficulty level to be displayed.
     */
    public BotDifficultyPanel(final String difficultyName) {
        super(new FlowLayout(FlowLayout.CENTER, H_GAP, V_GAP));
        this.setOpaque(false);

        final JLabel difficultyLabel = new JLabel("Difficulty: " + difficultyName);
        difficultyLabel.setForeground(FOREGROUND_COLOR);
        difficultyLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_CTRL));

        this.add(difficultyLabel);
    }
}
