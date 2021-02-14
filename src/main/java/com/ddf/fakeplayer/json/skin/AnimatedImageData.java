package com.ddf.fakeplayer.json.skin;

public class AnimatedImageData {
    public double Frames;
    public String Image;
    public int ImageHeight;
    public int ImageWidth;
    public int Type;

    public AnimatedImageData() {
    }

    public AnimatedImageData(double frames, String image, int imageHeight, int imageWidth, int type) {
        Frames = frames;
        Image = image;
        ImageHeight = imageHeight;
        ImageWidth = imageWidth;
        Type = type;
    }
}