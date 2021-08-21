package com.ddf.fakeplayer.nbt;

import java.util.Arrays;

public class IntArrayTag implements Tag {
    public int[] data;

    public IntArrayTag(int[] data) {
        this.data = data;
    }

    @Override
    public Type getId() {
        return Type.IntArray;
    }

    @Override
    public IntArrayTag copy() {
        return new IntArrayTag(Arrays.copyOf(data, data.length));
    }
}
