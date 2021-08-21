package com.ddf.fakeplayer.nbt;

public class ShortTag implements Tag {
    public short data;

    public ShortTag(short data) {
        this.data = data;
    }

    @Override
    public Type getId() {
        return Type.Short_0;
    }

    @Override
    public ShortTag copy() {
        return new ShortTag(data);
    }
}
