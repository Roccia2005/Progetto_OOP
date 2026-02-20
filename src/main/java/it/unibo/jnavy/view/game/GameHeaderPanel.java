package it.unibo.jnavy.view.game;

import static it.unibo.jnavy.view.utilities.ViewConstants.BACKGROUND_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GameHeaderPanel extends JPanel {

    private final JLabel statusLabel;

    public GameHeaderPanel(final Runnable onSaveAction) {
        super(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        this.setBackground(BACKGROUND_COLOR);

        final JPanel centerTitlePanel = new JPanel(new GridLayout(2, 1));
        centerTitlePanel.setOpaque(false);

        final JLabel titleLabel = new JLabel("J-NAVY", SwingConstants.CENTER);
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 24)); 
        titleLabel.setForeground(Color.WHITE);

        this.statusLabel = new JLabel("Your Turn", SwingConstants.CENTER);
        this.statusLabel.setFont(new Font(FONT_FAMILY, Font.ITALIC, 16));
        this.statusLabel.setForeground(Color.WHITE);

        centerTitlePanel.add(titleLabel);
        centerTitlePanel.add(this.statusLabel);

        final JPanel savePanel = createSavePanel(onSaveAction);

        final JPanel ghostPanel = new JPanel();
        ghostPanel.setOpaque(false);
        ghostPanel.setPreferredSize(savePanel.getPreferredSize());

        this.add(ghostPanel, BorderLayout.WEST);
        this.add(centerTitlePanel, BorderLayout.CENTER);
        this.add(savePanel, BorderLayout.EAST);
    }

    public void setStatus(final String text, final Color color) {
        this.statusLabel.setText(text);
        this.statusLabel.setForeground(color);
    }

    private JPanel createSavePanel(final Runnable onSaveAction) {
        final JButton saveButton = new JButton("Save Game");
        saveButton.setFocusPainted(false);
        saveButton.setContentAreaFilled(false);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        saveButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        saveButton.addActionListener(e -> {
            if (onSaveAction != null) {
                onSaveAction.run();
            }
        });

        final JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        savePanel.setOpaque(false);
        savePanel.add(saveButton);
        return savePanel;
    }
}
