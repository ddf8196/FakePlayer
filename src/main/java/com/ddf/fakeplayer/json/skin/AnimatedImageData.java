package com.ddf.fakeplayer.json.skin;

public class AnimatedImageData {
    private double Frames;
    private String Image;
    private int ImageHeight;
    private int ImageWidth;
    private int Type;
    private int AnimationExpression = 0;// 自 618 协议 1.20.30 或更远版本被加入

    public AnimatedImageData() {}

    public AnimatedImageData(double frames, String image, int imageHeight, int imageWidth, int type, int animationExpression) {
        Frames = frames;
        Image = image;
        ImageHeight = imageHeight;
        ImageWidth = imageWidth;
        Type = type;
        AnimationExpression = animationExpression;
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
    public int getAnimationExpression() {
        return AnimationExpression;
    }

    public void setAnimationExpression(int animationExpression) {
        AnimationExpression = animationExpression;
    }

}