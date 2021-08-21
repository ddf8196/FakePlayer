package com.ddf.fakeplayer.nbt;

public class EndTag implements Tag {
    @Override
    public Type getId() {
        return Type.End_1;
    }

    @Override
    public EndTag copy() {
        return new EndTag();
    }
}
