package it.unibo.jnavy.view.components.captain;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

public class CaptainNamePanel extends JPanel {
    
    private static final long serialVersionUID = 1L;

    public CaptainNamePanel(String captainName) {
        super(new FlowLayout(FlowLayout.CENTER, 15, 0));
        this.setOpaque(false);

        JLabel nameLabel = new JLabel("Captain: " + captainName);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        this.add(nameLabel);
    }
}