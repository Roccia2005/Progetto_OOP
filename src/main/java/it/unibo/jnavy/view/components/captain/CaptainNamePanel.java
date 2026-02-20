package it.unibo.jnavy.view.components.captain;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Font;

import static it.unibo.jnavy.view.utilities.ViewConstants.*;

public class CaptainNamePanel extends JPanel {

    public CaptainNamePanel(String captainName) {
        super(new FlowLayout(FlowLayout.CENTER, 15, 0));
        this.setOpaque(false);

        JLabel nameLabel = new JLabel("Captain: " + captainName);
        nameLabel.setForeground(FOREGROUND_COLOR);
        nameLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));

        this.add(nameLabel);
    }
}