package it.unibo.jnavy.view;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.net.URL;

public class CapSelectionPanel extends JPanel {
    private static final Color MENUBLUE = new Color(41, 86, 246);
    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30);
    private static final Color FOREGROUND_COLOR = Color.WHITE;
    private static final int SETWIDTH = 1000;
    private static final int SETHEIGHT = 700;
    private static final int BORDER_THICKNESS = 2;
    private static final int IMAGE_SCALE_SIZE = 140;
    private static final int INSET_PADDING = 10;
    private static final int FLOW_HGAP = 10;
    private static final int FLOW_VGAP = 0;
    private static final int IMG_LABEL_WIDTH_DIVISOR = 7;
    private static final int IMG_LABEL_HEIGHT_DIVISOR = 5;
    private static final int DESC_WIDTH_DIVISOR = 2;
    private static final int DESC_HEIGHT_DIVISOR = 12;
    private static final int CONTROL_WIDTH_DIVISOR = 7;
    private static final int CONTROL_HEIGHT_DIVISOR = 16;
    private static final String FONT_FAMILY = "SansSerif";
    private static final int FONT_SIZE_TITLE = 36;
    private static final int FONT_SIZE_DESC = 18;
    private static final int FONT_SIZE_CTRL = 14;

    public enum CaptainAbility {
        ENGINEER("Engineer", "He can repair a piece of any ship as long as a certain number of turns pass", "images/engineer.png"),
        GUNNER("Gunner", "He can fire a multiple shot capable of hitting a 4-cell area of the opponent's grid", "images/gunner.png"),
        SONAROFFICER("SonarOfficer", "He can reveal information about a specific cell on opponent's grid", "images/sonarofficer.png");

        private final String label;
        private final String description;
        private final String imagePath;

        CaptainAbility(final String label, final String description, final String imagePath) {
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
    public interface AbilitySelectionListener {
        void abilitySelected(CaptainAbility level);
    }

    private final AbilitySelectionListener listener;
    private JLabel imageLabel;
    private JComboBox<CaptainAbility> levelComboBox;
    private JTextPane descriptionArea;

    public CapSelectionPanel(final AbilitySelectionListener listener) {
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

        JLabel titleLabel = new JLabel("Choose the captain of your fleet");
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_TITLE));
        titleLabel.setForeground(FOREGROUND_COLOR);
        gbc.insets = new Insets(INSET_PADDING, INSET_PADDING, 30, INSET_PADDING);
        this.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(INSET_PADDING, INSET_PADDING, INSET_PADDING, INSET_PADDING);
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(SETWIDTH / IMG_LABEL_WIDTH_DIVISOR, SETHEIGHT / IMG_LABEL_HEIGHT_DIVISOR));
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
        descriptionArea.setFont(new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE_DESC));
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

        levelComboBox = new JComboBox<>(CaptainAbility.values());
        levelComboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(MENUBLUE);
                g.fillRect(bounds.x, bounds.y, bounds.width + 10, bounds.height);
            }

            @Override
            protected JButton createArrowButton() {
                JButton btn = new javax.swing.plaf.basic.BasicArrowButton(
                    javax.swing.plaf.basic.BasicArrowButton.SOUTH,
                    MENUBLUE,       // botton background color
                    MENUBLUE,       // shadow color, the same to uniform that to the background
                    FOREGROUND_COLOR, // arrow color
                    MENUBLUE        // highlight color
                );
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setContentAreaFilled(false);
                return btn;
            }
        });
        levelComboBox.setPreferredSize(new Dimension(SETWIDTH / CONTROL_WIDTH_DIVISOR, SETHEIGHT / CONTROL_HEIGHT_DIVISOR));
        levelComboBox.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_CTRL));
        levelComboBox.setRenderer(new customRenderer());
        levelComboBox.setFocusable(false);
        levelComboBox.setBackground(MENUBLUE);
        levelComboBox.setForeground(FOREGROUND_COLOR);

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
            CaptainAbility selected = (CaptainAbility) levelComboBox.getSelectedItem();
            if (selected != null && listener != null) {
                listener.abilitySelected(selected);
            }
        });

        controlsPanel.add(levelComboBox);
        controlsPanel.add(confirmButton);
        this.add(controlsPanel, gbc);
        levelComboBox.setSelectedIndex(0);
        updatePreview();
    }

    private void updatePreview() {
        CaptainAbility selected = (CaptainAbility) levelComboBox.getSelectedItem();
        if (selected != null) {
            ImageIcon icon = loadCapImage(selected.getImagePath());
            imageLabel.setIcon(icon);
            if (icon == null) {
                imageLabel.setText("image not found");
            } else {
                imageLabel.setText("");
            }
            descriptionArea.setText(selected.getDescription());
        }
    }

    private ImageIcon loadCapImage(String path) {
        URL imgUrl = getClass().getClassLoader().getResource(path);
        if (imgUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imgUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(IMAGE_SCALE_SIZE, IMAGE_SCALE_SIZE, Image.SCALE_SMOOTH);
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