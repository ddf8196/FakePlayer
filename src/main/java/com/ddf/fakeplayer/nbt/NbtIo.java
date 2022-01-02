package com.ddf.fakeplayer.nbt;

import com.ddf.fakeplayer.util.IDataInput;
import com.ddf.fakeplayer.util.ValueHolder;

public class NbtIo {
    public static CompoundTag read(IDataInput dis) {
        ValueHolder<String> name = new ValueHolder<>();
        Tag tag = Tag.readNamedTag(dis, name);
        if (tag != null && tag.getId() == Tag.Type.Compound) {
            return (CompoundTag) tag;
        } else {
            return new CompoundTag();
        }
    }
}
