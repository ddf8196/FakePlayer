package com.ddf.fakeplayer.actor;

public class DataItem<T> implements Cloneable {
    private final DataItemType mType;
    private final /*unsigned short*/int mId;
    private boolean mDirty;
    private T mData;

    public DataItem(DataItemType type, /*unsigned short*/int id, final T data) {
        this.mType = type;
        this.mId = id;
        this.mDirty = true;
        this.mData = data;
    }

    public final /*unsigned short*/int getId() {
        return this.mId;
    }

    public final DataItemType getType() {
        return this.mType;
    }

    public final boolean isDirty() {
        return this.mDirty;
    }

    public final void setDirty(boolean dirty) {
        this.mDirty = dirty;
    }

    public boolean isDataEqual(final DataItem<T> rhs) {
        return this.mType == rhs.mType;
    }

    public T getData() {
        return mData;
    }

    public boolean getFlag(long flag) {
        if (this.mData instanceof Byte) {
            return ((1 << flag) & (Byte) this.mData) != 0;
        } else if (this.mData instanceof Long) {
            return ((1L << flag) & (Long) this.mData) != 0;
        }
        return false;
    }

    public void setData(T data) {
        this.mData = data;
    }

    @Override
    public DataItem<T> clone() {
        return null;
    }
}
