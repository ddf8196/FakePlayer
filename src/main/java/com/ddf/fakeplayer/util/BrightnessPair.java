package com.ddf.fakeplayer.util;

public class BrightnessPair {
    public /*unsigned char Brightness*/int sky;
    public /*unsigned char Brightness*/int block;

    public BrightnessPair(BrightnessPair brightnessPair) {
        this.sky = brightnessPair.sky;
        this.block = brightnessPair.block;
    }

    public BrightnessPair(/*unsigned char Brightness*/int sky, /*unsigned char Brightness*/int block) {
        this.sky = sky;
        this.block = block;
    }
}
