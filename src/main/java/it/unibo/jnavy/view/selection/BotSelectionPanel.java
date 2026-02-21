package it.unibo.jnavy.view.selection;

import static it.unibo.jnavy.view.utilities.ViewConstants.BACKGROUND_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.BORDER_THICKNESS;
import static it.unibo.jnavy.view.utilities.ViewConstants.BOTTOM_PANEL_HGAP;
import static it.unibo.jnavy.view.utilities.ViewConstants.BOTTOM_PANEL_VGAP;
import static it.unibo.jnavy.view.utilities.ViewConstants.CONTROL_HEIGHT_DIVISOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.CONTROL_WIDTH_DIVISOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.DESC_HEIGHT_DIVISOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.DESC_WIDTH_DIVISOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.FLOW_HGAP;
import static it.unibo.jnavy.view.utilities.ViewConstants.FLOW_VGAP;
import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;
import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_SIZE_CTRL;
import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_SIZE_DESC;
import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_SIZE_TITLE;
import static it.unibo.jnavy.view.utilities.ViewConstants.FOREGROUND_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.IMAGE_SIZE;
import static it.unibo.jnavy.view.utilities.ViewConstants.INSET_PADDING;
import static it.unibo.jnavy.view.utilities.ViewConstants.MENUBLUE;
import static it.unibo.jnavy.view.utilities.ViewConstants.SETHEIGHT;
import static it.unibo.jnavy.view.utilities.ViewConstants.SETWIDTH;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import it.unibo.jnavy.view.utilities.ImageLoader;

/**
 * A panel that allows the user to select the bot difficulty level.
 * It displays a list of available bots, their descriptions, and related images.
 */
public class BotSelectionPanel extends JPanel {

    /**
     * Serial version UID for serialization.
     */
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final DifficultySelectionListener listener;
    private final Runnable backAction;
    private JLabel imageLabel;
    private JComboBox<BotLevel> levelComboBox;
    private JTextPane descriptionArea;

    /**
     * Constructs the bot selection panel.
     *
     * @param listener callback triggered when a bot is selected
     * @param backAction callback triggered when the back button is pressed
     */
    public BotSelectionPanel(final DifficultySelectionListener listener, final Runnable backAction) {
        this.listener = listener;
        this.backAction = backAction;
        this.setLayout(new BorderLayout());
        this.setBackground(BACKGROUND_COLOR);
        initUI();
    }

    /**
     * Initializes the user interface components of the panel.
     */
    private void initUI() {
        this.setPreferredSize(new Dimension(SETWIDTH, SETHEIGHT));

        final JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(INSET_PADDING, INSET_PADDING, INSET_PADDING, INSET_PADDING);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        final JLabel title = new JLabel("Choose your opponent");
        title.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_TITLE));
        title.setForeground(FOREGROUND_COLOR);
        gbc.insets = new Insets(INSET_PADDING, INSET_PADDING, BOTTOM_PANEL_VGAP, INSET_PADDING);
        centerPanel.add(title, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(INSET_PADDING, INSET_PADDING, INSET_PADDING, INSET_PADDING);
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(FOREGROUND_COLOR, BORDER_THICKNESS));
        centerPanel.add(imageLabel, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy++;
        descriptionArea = new JTextPane();
        descriptionArea.setPreferredSize(new Dimension(SETWIDTH / DESC_WIDTH_DIVISOR, SETHEIGHT / DESC_HEIGHT_DIVISOR));
        descriptionArea.setEditable(false);
        descriptionArea.setFocusable(false);
        descriptionArea.setFont(new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE_DESC));
        descriptionArea.setBackground(BACKGROUND_COLOR);
        descriptionArea.setForeground(FOREGROUND_COLOR);
        descriptionArea.setBorder(BorderFactory.createLineBorder(FOREGROUND_COLOR, BORDER_THICKNESS));
        descriptionArea.setBackground(MENUBLUE);

        final StyledDocument doc = descriptionArea.getStyledDocument();
        final SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        centerPanel.add(descriptionArea, gbc);

        gbc.gridy++;
        final JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, FLOW_HGAP, FLOW_VGAP));
        controlsPanel.setBackground(BACKGROUND_COLOR);

        levelComboBox = new JComboBox<>(BotLevel.values());
        levelComboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {

            @Override
            public void paintCurrentValueBackground(final Graphics g, final Rectangle bounds, final boolean hasFocus) {
                g.setColor(MENUBLUE);
                g.fillRect(bounds.x, bounds.y, bounds.width + 10, bounds.height);
            }

            @Override
            protected JButton createArrowButton() {
                final JButton btn = new javax.swing.plaf.basic.BasicArrowButton(
                    javax.swing.plaf.basic.BasicArrowButton.SOUTH,
                    MENUBLUE,
                    MENUBLUE,
                    FOREGROUND_COLOR,
                    MENUBLUE
                );
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setContentAreaFilled(false);
                return btn;
            }
        });
        levelComboBox.setPreferredSize(new Dimension(SETWIDTH / CONTROL_WIDTH_DIVISOR, SETHEIGHT / CONTROL_HEIGHT_DIVISOR));
        levelComboBox.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_CTRL));
        levelComboBox.setRenderer(new CustomRenderer());
        levelComboBox.setFocusable(false);
        levelComboBox.setBackground(MENUBLUE);
        levelComboBox.setForeground(FOREGROUND_COLOR);

        levelComboBox.setOpaque(true);
        levelComboBox.addActionListener(e -> updatePreview());
        levelComboBox.setBorder(BorderFactory.createLineBorder(FOREGROUND_COLOR, BORDER_THICKNESS));

        final JButton confirmButton = new JButton("Confirm");
        confirmButton.setFocusPainted(false);
        confirmButton.setPreferredSize(new Dimension(SETWIDTH / CONTROL_WIDTH_DIVISOR, SETHEIGHT / CONTROL_HEIGHT_DIVISOR));
        confirmButton.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_CTRL));
        confirmButton.setBackground(MENUBLUE);
        confirmButton.setForeground(FOREGROUND_COLOR);
        confirmButton.setBorder(BorderFactory.createLineBorder(FOREGROUND_COLOR, BORDER_THICKNESS));
        confirmButton.addActionListener(e -> {
            final BotLevel selected = (BotLevel) levelComboBox.getSelectedItem();
            if (selected != null && listener != null) {
                listener.difficultySelected(selected);
            }
        });

        controlsPanel.add(levelComboBox);
        controlsPanel.add(confirmButton);
        centerPanel.add(controlsPanel, gbc);

        final JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, BOTTOM_PANEL_HGAP, BOTTOM_PANEL_VGAP));
        bottomPanel.setBackground(BACKGROUND_COLOR);

        final JButton backButton = new JButton("Back");
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(SETWIDTH / CONTROL_WIDTH_DIVISOR, SETHEIGHT / CONTROL_HEIGHT_DIVISOR));
        backButton.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_CTRL));
        backButton.setBackground(MENUBLUE);
        backButton.setForeground(FOREGROUND_COLOR);
        backButton.setBorder(BorderFactory.createLineBorder(FOREGROUND_COLOR, BORDER_THICKNESS));
        backButton.addActionListener(e -> {
            if (this.backAction != null) {
                this.backAction.run();
            }
        });
        bottomPanel.add(backButton);

        this.add(centerPanel, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);

        levelComboBox.setSelectedIndex(0);
        updatePreview();
    }

    /**
     * Updates the image and description based on the currently selected bot level.
     */
    private void updatePreview() {
        final BotLevel selected = (BotLevel) levelComboBox.getSelectedItem();
        if (selected != null) {
            final ImageIcon icon = ImageLoader.getScaledIcon(selected.getImagePath(), IMAGE_SIZE, IMAGE_SIZE);
            imageLabel.setIcon(icon);
            if (icon == null) {
                imageLabel.setText("image not found");
            } else {
                imageLabel.setText("");
            }
            descriptionArea.setText(selected.getDescription());
        }
    }

    /**
     * Represents the available bot difficulty levels with associated metadata.
     */
    public enum BotLevel {
        BEGINNER("Beginner", "He shoots randomly at your grid, as if blindfolded", "/images/beginner.png"),
        PRO("Pro", "He learns with every shot he throws, he can become very strong", "/images/pro.png"),
        SNIPER("Sniper", "He knows the position of your ships, be smart", "/images/sniper.png");

        private final String label;
        private final String description;
        private final String imagePath;

        BotLevel(final String label, final String description, final String imagePath) {
            this.label = label;
            this.description = description;
            this.imagePath = imagePath;
        }

        /**
         * @return the description of the bot level
         */
        public String getDescription() {
            return this.description;
        }

        /**
         * @return the path to the image representing the bot
         */
        public String getImagePath() {
            return this.imagePath;
        }

        @Override
        public String toString() {
            return this.label;
        }
    }

    /**
     * Functional interface for handling bot difficulty selection events.
     */
    @FunctionalInterface
    public interface DifficultySelectionListener {
        /**
         * Called when a bot difficulty level is selected.
         *
         * @param level the selected {@link BotLevel}
         */
        void difficultySelected(BotLevel level);
    }

    /**
     * Custom renderer for the ComboBox to match the game's UI theme.
     */
    private final class CustomRenderer extends DefaultListCellRenderer {

        /**
         * Serial version UID for serialization.
         */
        @java.io.Serial
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(final JList<?> list, final Object value,
            final int index, final boolean isSelected, final boolean cellHasFocus) {
            final JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
                index, isSelected, false);

            label.setHorizontalAlignment(DefaultListCellRenderer.CENTER);

            if (index == -1) {
                label.setBackground(MENUBLUE);
                label.setForeground(FOREGROUND_COLOR);
            } else if (isSelected) {
                label.setBackground(MENUBLUE);
                label.setForeground(FOREGROUND_COLOR);
            } else {
                label.setBackground(BACKGROUND_COLOR);
                label.setForeground(FOREGROUND_COLOR);
            }
            label.setOpaque(true);
            return label;
        }
    }
}
