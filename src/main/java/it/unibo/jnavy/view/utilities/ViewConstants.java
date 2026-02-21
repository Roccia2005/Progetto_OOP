package it.unibo.jnavy.view.utilities;

import java.awt.Color;

public final class ViewConstants {
    //colori
    public static final Color MENUBLUE = new Color(41, 86, 246);
    public static final Color BACKGROUND_COLOR = new Color(20, 20, 30);
    public static final Color FOREGROUND_COLOR = Color.WHITE;
    public static final Color OVERLAY_COLOR = new Color(0, 0, 0, 230);
    public static final Color BUTTON_BLUE = new Color(30, 100, 255);
    public static final Color BUTTON_HOVER_BLUE = new Color(60, 130, 255);

    //layout e finestra
    public static final int SETWIDTH = 1000;
    public static final int SETHEIGHT = 700;
    public static final int INSET_PADDING = 10;
    public static final int FLOW_HGAP = 10;
    public static final int FLOW_VGAP = 0;

    //ui
    public static final int BORDER_THICKNESS = 2;
    public static final int IMAGE_SIZE = 140;
    public static final int GAMEOVER_IMG_WIDTH = 400;
    public static final int GAMEOVER_INSET_L = 40;
    public static final int GAMEOVER_INSET_S = 15;
    public static final int BTN_PADDING_V = 10;
    public static final int BTN_PADDING_H = 40;

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

    //privato così impediamo di istanziarla
    private ViewConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
