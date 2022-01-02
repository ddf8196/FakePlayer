package com.ddf.fakeplayer.nbt;

import com.ddf.fakeplayer.util.IDataInput;
import com.ddf.fakeplayer.util.IDataOutput;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.Objects;

public class DoubleTag implements Tag {
    public double data;

    public DoubleTag() {
        this.data = 0;
    }

    public DoubleTag(double data) {
        this.data = data;
    }

    @NotImplemented
    @Override
    public void write(IDataOutput dos) {

    }

    @Override
    public void load(IDataInput dis) {
        this.data = dis.readDouble();
    }

    @Override
    public Type getId() {
        return Type.Double;
    }

    @Override
    public DoubleTag copy() {
        return new DoubleTag(data);
    }

    @Override
    public boolean equals(Tag rhs) {
        if (!Tag.super.equals(rhs)) {
            return false;
        }
        return ((DoubleTag) rhs).data == this.data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleTag doubleTag = (DoubleTag) o;
        return Double.compare(doubleTag.data, data) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "DoubleTag{" +
                "data=" + data +
                '}';
    }
}
