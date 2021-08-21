package com.ddf.fakeplayer.nbt;

import java.util.Arrays;

public class ByteArrayTag implements Tag {
    public byte[] data;

    public ByteArrayTag(byte[] data) {
        this.data = data;
    }

    @Override
    public Type getId() {
        return Type.ByteArray;
    }

    @Override
    public ByteArrayTag copy() {
        return new ByteArrayTag(Arrays.copyOf(data, data.length));
    }
}
