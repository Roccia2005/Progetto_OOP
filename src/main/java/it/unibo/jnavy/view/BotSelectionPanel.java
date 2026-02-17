package it.unibo.jnavy.view;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.net.URL;

public class BotSelectionPanel extends JPanel {
    private static final Color MENUBLUE = new Color(41, 86, 246);
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color FOREGROUND_COLOR = Color.WHITE;
    private static final int SETWIDTH = 1000;
    private static final int SETHEIGHT = 700;
    private static final int BORDER_THICKNESS = 2;
    private static final int IMAGE_SIZE = 140;
    private static final int INSET_PADDING = 10;
    private static final int FLOW_HGAP = 10;
    private static final int FLOW_VGAP = 0;
    private static final int DESC_WIDTH_DIVISOR = 3;
    private static final int DESC_HEIGHT_DIVISOR = 12;
    private static final int CONTROL_WIDTH_DIVISOR = 7;
    private static final int CONTROL_HEIGHT_DIVISOR = 16;
    private static final String FONT_FAMILY = "SansSerif";
    private static final int FONT_SIZE_DESC = 18;
    private static final int FONT_SIZE_CTRL = 14;

    public enum BotLevel {
        BEGINNER("Beginner", "He shoots randomly at your grid, as if blindfolded", "images/beginner.png"),
        PRO("Pro", "He learns with every shot he throws, he can become very strong", "images/pro.png"),
        SNIPER("Sniper", "He knows the position of your ships, be smart", "images/sniper.png");

        private final String label;
        private final String description;
        private final String imagePath;

        BotLevel(final String label, final String description, final String imagePath) {
            this.label = label;
            this.description = description;
            this.imagePath = imagePath;
        }

        public String getDescription() {
            return this.description;
        }
        public String getImagePath() {
            return this.imagePath;
        }

        @Override
        public String toString() {
            return this.label;
        }
    }

    @FunctionalInterface
    public interface DifficultySelectionListener {
        void difficultySelected(BotLevel level);
    }

    private final DifficultySelectionListener listener;
    private JLabel imageLabel;
    private JComboBox<BotLevel> levelComboBox;
    private JTextPane descriptionArea;

    public BotSelectionPanel(final DifficultySelectionListener listener) {
        this.listener = listener;
        this.setLayout(new GridBagLayout());
        this.setBackground(BACKGROUND_COLOR);
        initUI();
    }

    private void initUI() {
        this.setPreferredSize(new Dimension(SETWIDTH, SETHEIGHT));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(INSET_PADDING, INSET_PADDING, INSET_PADDING, INSET_PADDING);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(FOREGROUND_COLOR, BORDER_THICKNESS));
        this.add(imageLabel, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy++;
        descriptionArea = new JTextPane();
        descriptionArea.setPreferredSize(new Dimension(SETWIDTH / DESC_WIDTH_DIVISOR, SETHEIGHT / DESC_HEIGHT_DIVISOR));
        descriptionArea.setEditable(false);
        descriptionArea.setFocusable(false);
        descriptionArea.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_DESC));
        descriptionArea.setBackground(BACKGROUND_COLOR);
        descriptionArea.setForeground(FOREGROUND_COLOR);
        descriptionArea.setBorder(BorderFactory.createLineBorder(FOREGROUND_COLOR, BORDER_THICKNESS));
        descriptionArea.setBackground(MENUBLUE);

        StyledDocument doc = descriptionArea.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        this.add(descriptionArea, gbc);

        gbc.gridy++;
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, FLOW_HGAP, FLOW_VGAP));
        controlsPanel.setBackground(BACKGROUND_COLOR);

        levelComboBox = new JComboBox<>(BotLevel.values());
        levelComboBox.setPreferredSize(new Dimension(SETWIDTH / CONTROL_WIDTH_DIVISOR, SETHEIGHT / CONTROL_HEIGHT_DIVISOR));
        levelComboBox.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_CTRL));
        levelComboBox.setRenderer(new customRenderer());
        levelComboBox.setFocusable(false);
        levelComboBox.setBackground(MENUBLUE);
        levelComboBox.setForeground(FOREGROUND_COLOR);

        for (int i = 0; i < levelComboBox.getComponentCount(); i++) {
            Component c = levelComboBox.getComponent(i);
            if (c instanceof JButton) {
                c.setBackground(MENUBLUE);
                ((JButton) c).setBorder(BorderFactory.createEmptyBorder());
            }
        }
        levelComboBox.setOpaque(true);
        levelComboBox.addActionListener(e -> updatePreview());
        levelComboBox.setBorder(BorderFactory.createLineBorder(FOREGROUND_COLOR, BORDER_THICKNESS));

        JButton confirmButton = new JButton("Confirm");
        confirmButton.setPreferredSize(new Dimension(SETWIDTH / CONTROL_WIDTH_DIVISOR, SETHEIGHT / CONTROL_HEIGHT_DIVISOR));
        confirmButton.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_CTRL));
        confirmButton.setBackground(MENUBLUE);
        confirmButton.setForeground(FOREGROUND_COLOR);
        confirmButton.setBorder(BorderFactory.createLineBorder(FOREGROUND_COLOR, BORDER_THICKNESS));
        confirmButton.addActionListener(e -> {
            BotLevel selected = (BotLevel) levelComboBox.getSelectedItem();
            if (selected != null && listener != null) {
                listener.difficultySelected(selected);
            }
        });

        controlsPanel.add(levelComboBox);
        controlsPanel.add(confirmButton);
        this.add(controlsPanel, gbc);
        levelComboBox.setSelectedIndex(0);
        updatePreview();
    }

    private void updatePreview() {
        BotLevel selected = (BotLevel) levelComboBox.getSelectedItem();
        if (selected != null) {
            ImageIcon icon = loadBotImage(selected.getImagePath());
            imageLabel.setIcon(icon);
            if (icon == null) {
                imageLabel.setText("image not found");
            } else {
                imageLabel.setText("");
            }
            descriptionArea.setText(selected.getDescription());
        }
    }

    private ImageIcon loadBotImage(String path) {
        URL imgUrl = getClass().getClassLoader().getResource(path);
        if (imgUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imgUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } else {
            System.err.println("image not found at path: " + path);
            return null;
        }
    }

    private final class customRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, false);

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