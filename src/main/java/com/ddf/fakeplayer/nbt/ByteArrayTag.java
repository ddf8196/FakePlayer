package com.ddf.fakeplayer.nbt;

import com.ddf.fakeplayer.util.IDataInput;
import com.ddf.fakeplayer.util.IDataOutput;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.Arrays;

public class ByteArrayTag implements Tag {
    public byte[] data;

    public ByteArrayTag() {
        this.data = new byte[0];
    }

    public ByteArrayTag(byte[] data) {
        this.data = data;
    }

    @NotImplemented
    @Override
    public void write(IDataOutput dos) {

    }

    @Override
    public void load(IDataInput dis) {
        int size = dis.readInt();
        if (size > 0) {
            size = Math.min(size, dis.numBytesLeft());
            this.data = new byte[size];
            dis.readBytes(this.data, 0, size);
        }
    }

    @Override
    public Type getId() {
        return Type.ByteArray;
    }

    @Override
    public ByteArrayTag copy() {
        return new ByteArrayTag(Arrays.copyOf(data, data.length));
    }

    @Override
    public boolean equals(Tag rhs) {
        if (!Tag.super.equals(rhs)) {
            return false;
        }
        return Arrays.equals(((ByteArrayTag) rhs).data, this.data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteArrayTag that = (ByteArrayTag) o;
        return Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}
