package com.ddf.fakeplayer.util;

public class MathUtil {
    public static float wrapDegrees(float input) {
        input = (input + 180.0f) % 360.0f;
        if (input < 0.0)
            input = input + 360.0f;
        return input - 180.0f;
    }

    public static int clamp(int value, int min, int max) {
        return Math.min(max, Math.max(min, value));
    }

    public static float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(min, value));
    }

    public static float trunc(float value) {
        if (value > 0) {
            return (float) Math.floor(value);
        } else if (value < 0) {
            return (float) Math.ceil(value);
        }
        return value;
    }
}
