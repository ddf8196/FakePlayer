package com.ddf.fakeplayer.nbt;

import com.ddf.fakeplayer.util.IDataInput;
import com.ddf.fakeplayer.util.IDataOutput;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.Arrays;

public class IntArrayTag implements Tag {
    public int[] data;

    public IntArrayTag() {
        this.data = new int[0];
    }

    public IntArrayTag(int[] data) {
        this.data = data;
    }

    @NotImplemented
    @Override
    public void write(IDataOutput dos) {

    }

    @Override
    public void load(IDataInput dis) {
        int nSize = dis.readInt();
        if (nSize > 0) {
           this.data = new int[nSize];
            for (int i = 0; i < nSize && dis.numBytesLeft() != 0; ++i) {
                this.data[i] = dis.readInt();
            }
        }
    }

    @Override
    public Type getId() {
        return Type.IntArray;
    }

    @Override
    public IntArrayTag copy() {
        return new IntArrayTag(Arrays.copyOf(data, data.length));
    }

    @Override
    public boolean equals(Tag rhs) {
        if (!Tag.super.equals(rhs)) {
            return false;
        }
        return Arrays.equals(((IntArrayTag) rhs).data, this.data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntArrayTag that = (IntArrayTag) o;
        return Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}
