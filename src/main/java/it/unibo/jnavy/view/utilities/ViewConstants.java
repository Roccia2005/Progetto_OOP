package it.unibo.jnavy.view.utilities;

import java.awt.Color;

/**
 * Utility class containing all the constants used across the application's View component.
 * It provides centralized definitions for colors, layout dimensions, UI scaling, and fonts
 * to ensure a consistent look and feel throughout the graphical user interface.
 */
public final class ViewConstants {
    // --- Colors ---

    /** Standard menu blue color used for backgrounds or components. */
    public static final Color MENUBLUE = new Color(41, 86, 246);

    /** Default dark background color for the application panels. */
    public static final Color BACKGROUND_COLOR = new Color(20, 20, 30);

    /** Default text and foreground color. */
    public static final Color FOREGROUND_COLOR = Color.WHITE;

    /** Default text and foreground color for errors message. */
    public static final Color TEXT_COLOR = new Color(240, 240, 255);
    public static final Color ERROR_COLOR = new Color(200, 50, 50);

    /** Semi-transparent black color used for overlay panels (e.g., game over screen). */
    public static final Color OVERLAY_COLOR = new Color(0, 0, 0, 230);

    /** Standard blue color for buttons. */
    public static final Color BUTTON_BLUE = new Color(30, 100, 255);

    /** Lighter blue color used when hovering over buttons. */
    public static final Color BUTTON_HOVER_BLUE = new Color(60, 130, 255);

    /** Accent yellow color used for weather alerts and borders. */
    public static final Color ACCENT_YELLOW = new Color(255, 200, 50);

    /** Off-white color used for subtitles and secondary text. */
    public static final Color LIGHT_TEXT_COLOR = new Color(240, 240, 255);

    /** Background color for sunny weather (semi-transparent yellowish). */
    public static final Color SUNNY_BG_COLOR = new Color(255, 250, 200, 150);

    /** Border color for fog weather (grayish). */
    public static final Color FOG_BORDER_COLOR = new Color(100, 120, 140);

    /** Background color for fog weather (semi-transparent grayish). */
    public static final Color FOG_BG_COLOR = new Color(200, 210, 220, 150);

    // --- Layout and Window ---

    /** Fixed the width of the main application window. */
    public static final int SETWIDTH = 1000;

    /** Fixed the height of the main application window. */
    public static final int SETHEIGHT = 700;

    /** Standard inset padding used in layouts (e.g., GridBagLayout). */
    public static final int INSET_PADDING = 10;

    /** Horizontal gap used in FlowLayouts. */
    public static final int FLOW_HGAP = 10;

    /** Vertical gap used in FlowLayouts. */
    public static final int FLOW_VGAP = 0;

    /** Vertical gap specifically used for bottom control panels. */
    public static final int BOTTOM_PANEL_VGAP = 30;

    /** Horizontal gap specifically used for bottom control panels. */
    public static final int BOTTOM_PANEL_HGAP = 20;

    /** Width of the panels containing row or column labels for the grid. */
    public static final int GRID_LABEL_WIDTH = 30;

    // --- UI Elements ---

    /** Standard thickness for borders. */
    public static final int BORDER_THICKNESS = 2;

    /** Standard dimension (width and height) for preview images. */
    public static final int IMAGE_SIZE = 140;

    /** Fixed width for the image displayed in the Game Over screen. */
    public static final int GAMEOVER_IMG_WIDTH = 400;

    /** Large inset padding used in the Game Over screen. */
    public static final int GAMEOVER_INSET_L = 40;

    /** Small inset padding used in the Game Over screen. */
    public static final int GAMEOVER_INSET_S = 15;

    /** Vertical inner padding for standard buttons. */
    public static final int BTN_PADDING_V = 10;

    /** Horizontal inner padding for standard buttons. */
    public static final int BTN_PADDING_H = 40;

    /** Alpha value for semi-transparent overlays. */
    public static final int OVERLAY_ALPHA = 120;

    // --- Proportions (Divisors for scaling based on window size) ---

    /** Divisor to calculate the width of the description area. */
    public static final int DESC_WIDTH_DIVISOR = 3;

    /** Divisor to calculate the height of the description area. */
    public static final int DESC_HEIGHT_DIVISOR = 12;

    /** Divisor to calculate the width of standard controls (e.g., ComboBoxes, Buttons). */
    public static final int CONTROL_WIDTH_DIVISOR = 7;

    /** Divisor to calculate the height of standard controls. */
    public static final int CONTROL_HEIGHT_DIVISOR = 16;

    /** Divisor to calculate the width of standard image labels. */
    public static final int IMG_LABEL_WIDTH_DIVISOR = 7;

    /** Divisor to calculate the height of standard image labels. */
    public static final int IMG_LABEL_HEIGHT_DIVISOR = 5;

    /** Adding to calculate the bounds width. */
    public static final int BG_WIDTH_OFFSET = 10;

    // --- Fonts ---

    /** Standard font family used throughout the application. */
    public static final String FONT_FAMILY = "SansSerif";

    /** Font size for main titles. */
    public static final int FONT_SIZE_TITLE = 36;

    /** Font size for description texts. */
    public static final int FONT_SIZE_DESC = 18;

    /** Font size for control elements (e.g., buttons, comboboxes). */
    public static final int FONT_SIZE_CTRL = 14;

    /** Bottom margin applied to titles. */
    public static final int TITLE_BOTTOM_MARGIN = 30;

    /**
     * Private constructor to prevent instantiation of this utility class.
     * Throws an exception if called via reflection.
     */
    private ViewConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
