package com.ddf.fakeplayer.nbt;

import com.ddf.fakeplayer.util.IDataInput;
import com.ddf.fakeplayer.util.IDataOutput;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class ListTag implements Tag, Iterable<Tag> {
    private ArrayList<Tag> mList = new ArrayList<>();
    private Tag.Type mType = Type.End_1;

    public final int size() {
        return this.mList.size();
    }

    public final Tag get(int index) {
        return mList.get(index);
    }

    public final void add(Tag tag) {
        this.mList.add(tag);
        if (this.mType == Type.End_1) {
            this.mType = tag.getId();
        }
    }

    public final Type getType() {
        return this.mType;
    }

    public final String getString(int index) {
        Tag tag = this.get(index);
        if (tag.getId() == Type.String_1) {
            return ((StringTag) tag).data;
        }
        return null;
    }

    @NotImplemented
    @Override
    public void write(IDataOutput dos) {

    }

    @Override
    public void load(IDataInput dis) {
        this.mType = Tag.Type.values()[dis.readByte()];
        int size = dis.readInt();
        this.mList.clear();
        if (size > 0 && this.mType != Type.End_1) {
            for (int i = 0; i < size && dis.numBytesLeft() != 0; ++i) {
                Tag tag = Tag.newTag(this.mType);
                if (tag != null) {
                    tag.load(dis);
                    this.mList.add(tag);
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public Type getId() {
        return Type.List_0;
    }

    @Override
    public ListTag copy() {
        ListTag listTag = new ListTag();
        for (Tag tag : this) {
            listTag.add(tag.copy());
        }
        return listTag;
    }

    @Override
    public Iterator<Tag> iterator() {
        return this.mList.iterator();
    }

    @Override
    public boolean equals(Tag rhs) {
        if (!Tag.super.equals(rhs))
            return false;
        ListTag tag = (ListTag) rhs;
        return this.mType == tag.mType && Objects.equals(this.mList, tag.mList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListTag tag = (ListTag) o;
        return Objects.equals(mList, tag.mList) && mType == tag.mType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mList, mType);
    }
}
