package com.ddf.fakeplayer.util;

import ar.com.hjg.pngj.*;

import java.io.*;

public class Image {
    private int width;
    private int height;
    private int[] data;

    public static int a(int pixel) {
        return pixel >> 24 & 0xFF;
    }

    public static int r(int pixel) {
        return pixel >> 16 & 0xFF;
    }

    public static int g(int pixel) {
        return pixel >> 8 & 0xFF;
    }

    public static int b(int pixel) {
        return pixel & 0xFF;
    }

    public static int a(int pixel, int a) {
        return pixel & 0x00FFFFFF | a << 24;
    }

    public static int r(int pixel, int r) {
        return pixel & 0xFF00FFFF | r << 16 & 0xFF0000;
    }

    public static int g(int pixel, int g) {
        return pixel & 0xFFFF00FF | g << 8 & 0xFF00;
    }

    public static int b(int pixel, int b) {
        return pixel & 0xFFFFFF00 | b & 0xFF;
    }

    public Image() {}

    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        data = new int[width * height];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPixel(int x, int y) {
        return data[y * width + x];
    }

    public int getPixelA(int x, int y) {
        return a(data[y * width + x]);
    }
    public int getPixelR(int x, int y) {
        return r(data[y * width + x]);
    }
    public int getPixelG(int x, int y) {
        return g(data[y * width + x]);
    }
    public int getPixelB(int x, int y) {
        return b(data[y * width + x]);
    }

    public void setPixel(int x, int y, int pixel) {
        data[y * width + x] = pixel;
    }

    public void setPixelA(int x, int y, int a) {
        data[y * width + x] = a(data[y * width + x], a);
    }

    public void setPixelR(int x, int y, int r) {
        data[y * width + x] = r(data[y * width + x], r);
    }

    public void setPixelG(int x, int y, int g) {
        data[y * width + x] = g(data[y * width + x], g);
    }

    public void setPixelB(int x, int y, int b) {
        data[y * width + x] = b(data[y * width + x], b);
    }

    public void draw(int x, int y, Image image) {
        int w = width, h = height;

        if (x + image.width > width) {
            w = x + image.width;
        }
        if (y + image.height > height) {
            h = y + image.height;
        }
        if (w != width || h != height) {
            clip(0, 0, w, h);
        }
        for (int x1 = 0; x1 < image.width; x1++) {
            for (int y1 = 0; y1 < image.height; y1++) {
                setPixel(x1 + x, y1 + y, image.getPixel(x1, y1));
            }
        }
    }

    public void clip(int x, int y, int width, int height) {
        int[] newData = new int[width * height];
        for (int x1 = 0; x1 < width && x1 + x < this.width; x1++) {
            for (int y1 = 0; y1 < height && y1 + y < this.height; y1++) {
                newData[y1 * width + x1] = data[(y1 + y) * this.width + x1 + x];
            }
        }
        this.width = width;
        this.height = height;
        this.data = newData;
    }

    public boolean load(File file) {
        try {
            return load(new FileInputStream(file), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean load(InputStream stream, boolean shouldCloseStream) {
        PngReader reader = new PngReader(stream, shouldCloseStream);
        width = reader.imgInfo.cols;
        height = reader.imgInfo.rows;
        data = new int[width * height];
        for (int row = 0; row < reader.imgInfo.rows; row++) {
            IImageLine line = reader.readRow(row);
            for (int col = 0; col < reader.imgInfo.cols; col++) {
                if (reader.imgInfo.alpha) {
                    data[row * width + col] = ImageLineHelper.getPixelARGB8(line, col);
                } else {
                    data[row * width + col] = 0xFF000000 | ImageLineHelper.getPixelRGB8(line, col);
                }
            }
        }
        reader.end();
        return true;
    }

    public boolean save(File file) {
        try {
            return save(new FileOutputStream(file), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean save(OutputStream stream, boolean shouldCloseStream) {
        ImageInfo info = new ImageInfo(width, height, 8, true, false, false);
        PngWriter writer = new PngWriter(stream, info);
        writer.setShouldCloseStream(shouldCloseStream);
        for (int row = 0; row < height; row++) {
            ImageLineInt line = new ImageLineInt(info);
            for (int col = 0; col < width; col++) {
                ImageLineHelper.setPixelRGBA8(line, col, getPixel(col, row));
            }
            writer.writeRow(line);
        }
        writer.end();
        return true;
    }
}
