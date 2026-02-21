package it.unibo.jnavy.view.components.captain;

import javax.swing.JLabel;
import javax.swing.JPanel;

import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;
import static it.unibo.jnavy.view.utilities.ViewConstants.FOREGROUND_COLOR;

import java.awt.FlowLayout;
import java.awt.Font;

public class CaptainNamePanel extends JPanel {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    public CaptainNamePanel(final String captainName) {
        super(new FlowLayout(FlowLayout.CENTER, 15, 0));
        this.setOpaque(false);

        final JLabel nameLabel = new JLabel("Captain: " + captainName);
        nameLabel.setForeground(FOREGROUND_COLOR);
        nameLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));

        this.add(nameLabel);
    }
}