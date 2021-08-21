package com.ddf.fakeplayer.nbt;

public class FloatTag implements Tag {
    public float data;

    public FloatTag(float data) {
        this.data = data;
    }

    @Override
    public Type getId() {
        return Type.Float_3;
    }

    @Override
    public FloatTag copy() {
        return new FloatTag(data);
    }
}
