package com.ddf.fakeplayer.level.dimension;

import com.ddf.fakeplayer.level.LevelListener;

public class Weather implements LevelListener {
    //private PerlinSimplexNoise mNoise = new PerlinSimplexNoise(0xB047, 1);
    private int mTick = 0;
    private float mORainLevel = 0.0f;
    private float mRainLevel = 0.0f;
    private float mTargetRainLevel = 0.0f;
    private float mOLightningLevel = 0.0f;
    private float mLightningLevel = 0.0f;
    private float mTargetLightningLevel = 0.0f;
    private float mFogLevel = 0.0f;
    private int mSkyFlashTime = 0;
    private Dimension mDimension;

    public Weather(Dimension d) {
        this.mDimension = d;
//        this.mDimension.getLevel().addListener(this);
//        LevelData levelData = this.mDimension.getLevel().getLevelData();
//        float rainLevel = levelData.getRainLevel();
//        this.setRainLevel(rainLevel);
//        float lightningLevel = levelData.getLightningLevel();
//        this.setLightningLevel(lightningLevel);
    }
}

