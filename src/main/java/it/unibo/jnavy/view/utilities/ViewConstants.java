package it.unibo.jnavy.view.utilities;

import java.awt.Color;

public final class ViewConstants {
    //colori
    public static final Color MENUBLUE = new Color(41, 86, 246);
    public static final Color BACKGROUND_COLOR = new Color(20, 20, 30);
    public static final Color FOREGROUND_COLOR = Color.WHITE;

    //layout e finestra
    public static final int SETWIDTH = 1000;
    public static final int SETHEIGHT = 700;
    public static final int INSET_PADDING = 10;
    public static final int FLOW_HGAP = 10;
    public static final int FLOW_VGAP = 0;
    public static final int BOTTOM_PANEL_VGAP = 30;
    public static final int BOTTOM_PANEL_HGAP = 20;

    //ui
    public static final int BORDER_THICKNESS = 2;
    public static final int IMAGE_SIZE = 140;

    //proporzioni
    public static final int DESC_WIDTH_DIVISOR = 3;
    public static final int DESC_HEIGHT_DIVISOR = 12;
    public static final int CONTROL_WIDTH_DIVISOR = 7;
    public static final int CONTROL_HEIGHT_DIVISOR = 16;
    public static final int IMG_LABEL_WIDTH_DIVISOR = 7;
    public static final int IMG_LABEL_HEIGHT_DIVISOR = 5;

    //font
    public static final String FONT_FAMILY = "SansSerif";
    public static final int FONT_SIZE_TITLE = 36;
    public static final int FONT_SIZE_DESC = 18;
    public static final int FONT_SIZE_CTRL = 14;
    public static final int TITLE_BOTTOM_MARGIN = 30;

    //privato così impediamo di istanziarla
    private ViewConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
