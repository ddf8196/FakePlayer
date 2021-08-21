package com.ddf.fakeplayer.nbt;

public class IntTag implements Tag {
    public int data;

    public IntTag(int data) {
        this.data = data;
    }

    @Override
    public Type getId() {
        return Type.Int_2;
    }

    @Override
    public IntTag copy() {
        return new IntTag(data);
    }
}
