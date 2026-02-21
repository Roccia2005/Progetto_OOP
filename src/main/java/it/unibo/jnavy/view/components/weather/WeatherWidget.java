package it.unibo.jnavy.view.components.weather;

import it.unibo.jnavy.view.utilities.ImageLoader;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;

import static it.unibo.jnavy.view.utilities.ViewConstants.ACCENT_YELLOW;
import static it.unibo.jnavy.view.utilities.ViewConstants.FOG_BG_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.FOG_BORDER_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.SUNNY_BG_COLOR;

/**
 * A circular widget that displays the current weather condition using a
 * distinct icon and a colored border.
 */
public class WeatherWidget extends JPanel {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private static final int WIDGET_SIZE = 80;
    private static final int ICON_SIZE = 50;
    private static final int STROKE_WIDTH = 4;

    private final JLabel iconLabel;
    private ImageIcon sunIcon;
    private ImageIcon fogIcon;
    private Color borderColor;
    private Color backgroundColor;

    /**
     * Constructs a new {@code WeatherWidget}.
     * The widget is initialized with a default "Sunny" state and contains only
     * a large-centered icon.
     */
    public WeatherWidget() {
        this.setLayout(new GridBagLayout());
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(WIDGET_SIZE, WIDGET_SIZE));

        loadIcons();

        this.iconLabel = new JLabel();
        this.iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        this.add(iconLabel);

        updateWeather("SUNNY");
    }

    /**
     * Loads the weather icons from application resources.
     */
    private void loadIcons() {
        this.sunIcon = ImageLoader.getScaledIcon("/images/sun.png", ICON_SIZE, ICON_SIZE);
        this.fogIcon = ImageLoader.getScaledIcon("/images/fog.png", ICON_SIZE, ICON_SIZE);
    }

    /**
     * Overrides the default painting behavior to draw a colored border around the widget.
     * This method renders a filled circle with a colored border, creating the main body
     * of the widget. It uses antialiasing to improve the visual quality.
     *
     * @param g the Graphics object used for drawing.
     */
    @Override
    protected void paintComponent(final Graphics g) {
        final Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int diameter = Math.min(getWidth(), getHeight()) - (STROKE_WIDTH * 2);
        final int x = (getWidth() - diameter) / 2;
        final int y = (getHeight() - diameter) / 2;

        // Draw the semi-transparent background circle
        g2.setColor(this.backgroundColor);
        g2.fillOval(x, y, diameter, diameter);

        // Draw the accent border
        g2.setColor(this.borderColor);
        g2.setStroke(new BasicStroke(STROKE_WIDTH));
        g2.drawOval(x, y, diameter, diameter);

        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * Updates the widget's icon based on the provided weather condition name.
     *
     * @param conditionName the name of the weather condition to display.
     */
    public void updateWeather(final String conditionName) {
        switch (conditionName) {
            case "SUNNY" -> {
                this.iconLabel.setIcon(sunIcon);
                this.iconLabel.setText("");

                this.borderColor = ACCENT_YELLOW;
                this.backgroundColor = SUNNY_BG_COLOR;
                this.setToolTipText("Weather: sunny");
                break;
            }
            case "FOG" -> {
                this.iconLabel.setIcon(fogIcon);
                this.iconLabel.setText("");
                this.borderColor = FOG_BORDER_COLOR;
                this.backgroundColor = FOG_BG_COLOR;
                this.setToolTipText("Weather: fog");
                break;
            }
        }
        this.repaint();
    }
}
