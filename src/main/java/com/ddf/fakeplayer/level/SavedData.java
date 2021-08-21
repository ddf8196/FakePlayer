package com.ddf.fakeplayer.level;

public abstract class SavedData {
    private boolean mDirty;
    private final String mId;

    public SavedData(final String id) {
        this.mDirty = false;
        this.mId = id;
    }

//    public final String getId() {
//        return mId;
//    }

    public boolean isDirty() {
        return mDirty;
    }

    public void setDirty(boolean dirty) {
        this.mDirty = dirty;
    }
}
