package com.ddf.fakeplayer.nbt;

import com.ddf.fakeplayer.util.IDataInput;
import com.ddf.fakeplayer.util.IDataOutput;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.Objects;

public class IntTag implements Tag {
    public int data;

    public IntTag() {
        this.data = 0;
    }

    public IntTag(int data) {
        this.data = data;
    }

    @NotImplemented
    @Override
    public void write(IDataOutput dos) {

    }

    @Override
    public void load(IDataInput dis) {
        this.data = dis.readInt();
    }

    @Override
    public Type getId() {
        return Type.Int_2;
    }

    @Override
    public IntTag copy() {
        return new IntTag(data);
    }

    @Override
    public boolean equals(Tag rhs) {
        if (!Tag.super.equals(rhs)) {
            return false;
        }
        return ((IntTag) rhs).data == this.data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntTag intTag = (IntTag) o;
        return data == intTag.data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "IntTag{" +
                "data=" + data +
                '}';
    }
}
