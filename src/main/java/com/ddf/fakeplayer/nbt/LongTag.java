package com.ddf.fakeplayer.nbt;

public class LongTag implements Tag {
    public long data;

    public LongTag(long data) {
        this.data = data;
    }

    @Override
    public Type getId() {
        return Type.Int64_0;
    }

    @Override
    public LongTag copy() {
        return new LongTag(data);
    }
}
