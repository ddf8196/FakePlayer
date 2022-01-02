package com.ddf.fakeplayer.nbt;

import com.ddf.fakeplayer.util.IDataInput;
import com.ddf.fakeplayer.util.IDataOutput;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.Objects;

public class LongTag implements Tag {
    public long data;

    public LongTag() {
        this.data = 0;
    }

    public LongTag(long data) {
        this.data = data;
    }

    @NotImplemented
    @Override
    public void write(IDataOutput dos) {

    }

    @Override
    public void load(IDataInput dis) {
        this.data = dis.readLongLong();
    }

    @Override
    public Type getId() {
        return Type.Int64_0;
    }

    @Override
    public LongTag copy() {
        return new LongTag(data);
    }

    @Override
    public boolean equals(Tag rhs) {
        if (!Tag.super.equals(rhs)) {
            return false;
        }
        return ((LongTag) rhs).data == this.data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongTag longTag = (LongTag) o;
        return data == longTag.data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "LongTag{" +
                "data=" + data +
                '}';
    }
}
