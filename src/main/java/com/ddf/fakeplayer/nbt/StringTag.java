package com.ddf.fakeplayer.nbt;

import com.ddf.fakeplayer.util.IDataInput;
import com.ddf.fakeplayer.util.IDataOutput;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.Objects;

public class StringTag implements Tag {
    public String data;

    public StringTag() {
        this.data = "";
    }

    public StringTag(String data) {
        this.data = data;
    }

    @NotImplemented
    @Override
    public void write(IDataOutput dos) {

    }

    @Override
    public void load(IDataInput dis) {
        this.data = dis.readString();
    }

    @Override
    public Type getId() {
        return Type.String_1;
    }

    @Override
    public StringTag copy() {
        return new StringTag(data);
    }

    @Override
    public boolean equals(Tag rhs) {
        if (!Tag.super.equals(rhs)) {
            return false;
        }
        return Objects.equals(this.data, ((StringTag) rhs).data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringTag tag = (StringTag) o;
        return Objects.equals(data, tag.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "StringTag{" +
                "data='" + data + '\'' +
                '}';
    }
}
