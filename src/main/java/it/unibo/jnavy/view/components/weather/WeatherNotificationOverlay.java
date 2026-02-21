package it.unibo.jnavy.view.components.weather;

import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import static it.unibo.jnavy.view.utilities.ViewConstants.ACCENT_YELLOW;
import static it.unibo.jnavy.view.utilities.ViewConstants.BACKGROUND_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.FONT_FAMILY;
import static it.unibo.jnavy.view.utilities.ViewConstants.LIGHT_TEXT_COLOR;
import static it.unibo.jnavy.view.utilities.ViewConstants.OVERLAY_ALPHA;

/**
 * A transient, semi-transparent overlay that displays weather changes.
 * This component appears when the weather changes and disappears automatically
 * after a few seconds.
 */
public class WeatherNotificationOverlay extends JComponent {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private static final int DISPLAY_DURATION_MS = 3000;
    private static final int PADDING = 40;
    private static final int CORNER_RADIUS = 30;
    private static final int BOX_HEIGHT = 120;
    private static final float BORDER_STOKE = 3.0f;

    private static final int TITLE_FONT_SIZE = 28;
    private static final int SUBTITLE_FONT_SIZE = 22;
    private static final int TITLE_Y_OFFSET = 50;
    private static final int SUBTITLE_Y_OFFSET = 90;

    private String title = "";
    private String subtitle = "";
    private final Timer timer;

    /**
     * Constructs a new WeatherNotificationOverlay.
     * Initializing the timer to automatically hide the overlay after 3 seconds.
     */
    public WeatherNotificationOverlay() {
        this.timer = new Timer(DISPLAY_DURATION_MS, e -> {
            title = "";
            subtitle = "";
            repaint();
        });
        this.timer.setRepeats(false);
        this.setOpaque(false);
    }

    /**
     * Displays the overlay with the specified weather name.
     * The notification becomes visible and the auto-hide timer starts.
     *
     * @param weatherName the name of the current weather condition to display in the subtitle.
     */
    public void showWeatherAlert(final String weatherName) {
        this.title = "Attention! Weather changed.";
        this.subtitle = "Current weather: " + weatherName;

        this.timer.restart();
        this.setVisible(true);
        this.repaint();
    }

    /**
     * Overrides the default paintComponent method to draw a semi-transparent overlay with the weather name.
     *
     * @param g the Graphics object used for drawing.
     */
    @Override
    protected void paintComponent(final Graphics g) {
        if (title.isEmpty()) {
            return;
        }

        final Graphics2D g2 = (Graphics2D) g;

        // Enable anti-aliasing for smoother rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        final Font titleFont = new Font(FONT_FAMILY, Font.BOLD, TITLE_FONT_SIZE);
        final Font subFont = new Font(FONT_FAMILY, Font.PLAIN, SUBTITLE_FONT_SIZE);

        final FontMetrics fmTitle = g2.getFontMetrics(titleFont);
        final FontMetrics fmSub = g2.getFontMetrics(subFont);

        // Calculate dimensions of the overlay box
        final int textWidth = Math.max(fmTitle.stringWidth(title), fmSub.stringWidth(subtitle));
        final int boxWidth = textWidth + (PADDING * 2);

        final int boxX = (getWidth() - boxWidth) / 2;
        final int boxY = (getHeight() - BOX_HEIGHT) / 2;

        // Draw the semi-transparent background
        g2.setColor(new Color(BACKGROUND_COLOR.getRed(), BACKGROUND_COLOR.getGreen(), BACKGROUND_COLOR.getBlue(), OVERLAY_ALPHA));
        g2.fillRoundRect(boxX, boxY, boxWidth, BOX_HEIGHT, CORNER_RADIUS, CORNER_RADIUS);

        // Draw the border
        g2.setColor(ACCENT_YELLOW);
        g2.setStroke(new BasicStroke(BORDER_STOKE));
        g2.drawRoundRect(boxX, boxY, boxWidth, BOX_HEIGHT, CORNER_RADIUS, CORNER_RADIUS);

        // Draw the title text
        g2.setFont(titleFont);
        g2.setColor(LIGHT_TEXT_COLOR);
        final int titleX = boxX + (boxWidth - fmTitle.stringWidth(title)) / 2;
        g2.drawString(title, titleX, boxY + TITLE_Y_OFFSET);

        // Draw the subtitle text
        g2.setFont(subFont);
        g2.setColor(LIGHT_TEXT_COLOR);
        final int subX = boxX + (boxWidth - fmSub.stringWidth(subtitle)) / 2;
        g2.drawString(subtitle, subX, boxY + SUBTITLE_Y_OFFSET);
    }
}
