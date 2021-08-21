package com.ddf.fakeplayer.nbt;

public class StringTag implements Tag {
    public String data;

    public StringTag(String data) {
        this.data = data;
    }

    @Override
    public Type getId() {
        return Type.String_1;
    }

    @Override
    public StringTag copy() {
        return new StringTag(data);
    }
}
