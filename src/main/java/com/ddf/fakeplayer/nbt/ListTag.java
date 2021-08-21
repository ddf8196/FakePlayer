package com.ddf.fakeplayer.nbt;

import com.ddf.fakeplayer.util.NotImplemented;

import java.util.ArrayList;
import java.util.Iterator;

@NotImplemented
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
}
