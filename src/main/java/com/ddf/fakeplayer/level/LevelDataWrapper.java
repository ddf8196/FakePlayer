package com.ddf.fakeplayer.level;

public class LevelDataWrapper {
    private LevelData mLevelDataFromLevel;
    private LevelData mLevelDataFromDisk;

    public LevelDataWrapper() {
        this.mLevelDataFromLevel = null;
        this.mLevelDataFromDisk = new LevelData();
    }

    public final LevelData get() {
        if ( this.mLevelDataFromLevel != null)
            return this.mLevelDataFromLevel;
        else
            return this.mLevelDataFromDisk;
    }
}
