package com.ddf.fakeplayer.nbt;

import com.ddf.fakeplayer.util.IDataInput;
import com.ddf.fakeplayer.util.IDataOutput;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.Objects;

public class ShortTag implements Tag {
    public short data;

    public ShortTag() {
        this.data = 0;
    }

    public ShortTag(short data) {
        this.data = data;
    }

    @NotImplemented
    @Override
    public void write(IDataOutput dos) {

    }

    @Override
    public void load(IDataInput dis) {
        this.data = dis.readShort();
    }

    @Override
    public Type getId() {
        return Type.Short_0;
    }

    @Override
    public ShortTag copy() {
        return new ShortTag(data);
    }

    @Override
    public boolean equals(Tag rhs) {
        if (!Tag.super.equals(rhs)) {
            return false;
        }
        return ((ShortTag) rhs).data == this.data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShortTag shortTag = (ShortTag) o;
        return data == shortTag.data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "ShortTag{" +
                "data=" + data +
                '}';
    }
}
