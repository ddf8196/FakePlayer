package com.ddf.fakeplayer.json.skin;

public class AnimatedImageData {
    private double Frames;
    private String Image;
    private int ImageHeight;
    private int ImageWidth;
    private int Type;

    public AnimatedImageData() {}

    public AnimatedImageData(double frames, String image, int imageHeight, int imageWidth, int type) {
        Frames = frames;
        Image = image;
        ImageHeight = imageHeight;
        ImageWidth = imageWidth;
        Type = type;
    }

    public double getFrames() {
        return Frames;
    }

    public void setFrames(double frames) {
        Frames = frames;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public int getImageHeight() {
        return ImageHeight;
    }

    public void setImageHeight(int imageHeight) {
        ImageHeight = imageHeight;
    }

    public int getImageWidth() {
        return ImageWidth;
    }

    public void setImageWidth(int imageWidth) {
        ImageWidth = imageWidth;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }
}