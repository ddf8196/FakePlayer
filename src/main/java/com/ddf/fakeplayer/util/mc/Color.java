package com.ddf.fakeplayer.util.mc;

public class Color {
    public static final Color GREY = new Color(0.5f, 0.5f, 0.5f, 1.0f);
    public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Color RED = new Color(1.0f, 0.0f, 0.0f, 1.0f);
    public static final Color GREEN = new Color(0.0f, 1.0f, 0.0f, 1.0f);
    public static final Color BLUE = new Color(0.0f, 0.0f, 1.0f, 1.0f);
    public static final Color YELLOW = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    public static final Color ORANGE = new Color(0.85000002f, 0.5f, 0.2f, 1.0f);
    public static final Color PURPLE = new Color(1.0f, 0.0f, 1.0f, 1.0f);
    public static final Color CYAN = new Color(0.0f, 1.0f, 1.0f, 1.0f);
    public static final Color PINK = Color.fromARGB(0xFFF38BAA);
    public static final Color NIL = new Color(0.0f, 0.0f, 0.0f, 0.0f);
    public static final Color SHADE_DOWN = Color.fromIntensity(0.5f, 1.0f);
    public static final Color SHADE_UP = Color.fromIntensity(1.0f, 1.0f);
    public static final Color SHADE_NORTH_SOUTH = Color.fromIntensity(0.80000001f, 1.0f);
    public static final Color SHADE_WEST_EAST = Color.fromIntensity(0.60000002f, 1.0f);
    public static final Color MINECOIN_GOLD = new Color(0.87f, 0.83999997f, 0.02f, 1.0f);

    public static final Color[] COLORS = {
            Color.fromARGB(0xFF000000),
            Color.fromARGB(0xFF0000AA),
            Color.fromARGB(0xFF00AA00),
            Color.fromARGB(0xFF00AAAA),
            Color.fromARGB(0xFFAA0000),
            Color.fromARGB(0xFFAA00AA),
            Color.fromARGB(0xFFFFAA00),
            Color.fromARGB(0xFFAAAAAA),
            Color.fromARGB(0xFF555555),
            Color.fromARGB(0xFF5555FF),
            Color.fromARGB(0xFF55FF55),
            Color.fromARGB(0xFF55FFFF),
            Color.fromARGB(0xFFFF5555),
            Color.fromARGB(0xFFFF55FF),
            Color.fromARGB(0xFFFFFF55),
            Color.fromARGB(0xFFFFFFFF),
            new Color(0.87f, 0.83999997f, 0.02f, 1.0f),
    };
    public float r, g, b, a;

    public Color(float r_, float g_, float b_, float a_) {
        this.r = r_;
        this.g = g_;
        this.b = b_;
        this.a = a_;
    }

    public static Color fromRGB(int col) {
        return Color.from255Range((col << 16) & 0xFF, (col << 8) & 0xFF, col & 0xFF, 255);
    }

    public static Color fromARGB(int col) {
        return Color.from255Range((col << 16) & 0xFF, (col << 8) & 0xFF, col & 0xFF, (col << 24) & 0xFF);
    }

    public static Color fromIntensity(float I, float a) {
        return new Color(I, I, I, a);
    }

    public static Color from255Range(int r, int g, int b, int a) {
        return  new Color((float)r / 255.0f, (float)g / 255.0f, (float)b / 255.0f, (float)a / 255.0f);
    }
}
