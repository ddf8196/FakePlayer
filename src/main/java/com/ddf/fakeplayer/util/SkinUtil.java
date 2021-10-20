package com.ddf.fakeplayer.util;

import java.util.Base64;

public class SkinUtil {
    public static Image decodeSkin(byte[] encoded, int width, int height) {
        Image image = new Image(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int index = (y * width + x) * 4;
                int r = encoded[index] & 0xFF;
                int g = encoded[index + 1] & 0xFF;
                int b = encoded[index + 2] & 0xFF;
                int a = encoded[index + 3] & 0xFF;
                image.setPixel(x, y, a << 24 | r << 16 | g << 8 | b);
            }
        }
        return image;
    }

    public static byte[] encodeSkin(Image image) {
        byte[] result = new byte[image.getWidth() * image.getHeight() * 4];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int index = (y * image.getWidth() + x) * 4;
                result[index] = (byte) image.getPixelR(x, y);
                result[index + 1] = (byte) image.getPixelG(x, y);
                result[index + 2] = (byte) image.getPixelB(x, y);
                result[index + 3] = (byte) image.getPixelA(x, y);
            }
        }
        return result;
    }

    public static String encodeSkinToBase64(Image image) {
        return Base64.getEncoder().encodeToString(encodeSkin(image));
    }
}
