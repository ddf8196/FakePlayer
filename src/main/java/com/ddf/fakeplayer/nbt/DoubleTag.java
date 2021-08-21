package com.ddf.fakeplayer.nbt;

public class DoubleTag implements Tag {
    public double data;

    public DoubleTag(double data) {
        this.data = data;
    }

    @Override
    public Type getId() {
        return Type.Double;
    }

    @Override
    public DoubleTag copy() {
        return new DoubleTag(data);
    }
}
