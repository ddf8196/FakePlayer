package com.ddf.fakeplayer.nbt;

public class ByteTag implements Tag {
    public byte data;

    public ByteTag(byte data) {
        this.data = data;
    }

    @Override
    public Type getId() {
        return Type.Byte_0;
    }

    @Override
    public ByteTag copy() {
        return new ByteTag(data);
    }
}
