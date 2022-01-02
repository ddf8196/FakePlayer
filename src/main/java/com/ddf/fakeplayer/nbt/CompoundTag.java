package com.ddf.fakeplayer.nbt;

import com.ddf.fakeplayer.util.IDataInput;
import com.ddf.fakeplayer.util.IDataOutput;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.ValueHolder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class CompoundTag implements Tag, Iterable<Map.Entry<String, Tag>>, Cloneable {
    private final HashMap<String, Tag> mTags = new HashMap<>();

    public final boolean remove(String name) {
        return this.mTags.containsKey(name);
    }

    public final boolean contains(String name) {
        return mTags.containsKey(name);
    }

    public final boolean contains(String name, Tag.Type type) {
        Tag tag = this.get(name);
        if (tag != null)
            return tag.getId() == type;
        return false;
    }

    public final Tag get(String name) {
        return this.mTags.get(name);
    }

    public final boolean getBoolean(String name) {
        Tag tag = this.get(name);
        if (tag != null && tag.getId() == Type.Byte_0) {
            return ((ByteTag) tag).data != 0;
        }
        return false;
    }

    public final byte getByte(String name) {
        Tag tag = this.get(name);
        if (tag != null && tag.getId() == Type.Byte_0) {
            return ((ByteTag) tag).data;
        }
        return 0;
    }

    public final short getShort(String name) {
        Tag tag = this.get(name);
        if (tag != null && tag.getId() == Type.Short_0) {
            return ((ShortTag) tag).data;
        }
        return 0;
    }

    public final int getInt(String name) {
        Tag tag = this.get(name);
        if (tag != null && tag.getId() == Type.Int_2) {
            return ((IntTag) tag).data;
        }
        return 0;
    }

    public final float getFloat(String name) {
        Tag tag = this.get(name);
        if (tag != null && tag.getId() == Type.Float_3) {
            return ((FloatTag) tag).data;
        }
        return 0;
    }

    public final long getLong(String name) {
        Tag tag = this.get(name);
        if (tag != null && tag.getId() == Type.Int64_0) {
            return ((LongTag) tag).data;
        }
        return 0;
    }

    public final double getDouble(String name) {
        Tag tag = this.get(name);
        if (tag != null && tag.getId() == Type.Double) {
            return ((DoubleTag) tag).data;
        }
        return 0;
    }

    public final byte[] getByteArray(String name) {
        Tag tag = this.get(name);
        if (tag != null && tag.getId() == Type.ByteArray) {
            return ((ByteArrayTag) tag).data;
        }
        return null;
    }

    public final int[] getIntArray(String name) {
        Tag tag = this.get(name);
        if (tag != null && tag.getId() == Type.IntArray) {
            return ((IntArrayTag) tag).data;
        }
        return null;
    }

    public final String getString(String name) {
        Tag tag = this.get(name);
        if (tag != null && tag.getId() == Type.String_1) {
            return ((StringTag) tag).data;
        }
        return null;
    }

    public final ListTag getList(String name) {
        Tag tag = this.get(name);
        if (tag != null && tag.getId() == Type.List_0) {
            return (ListTag) tag;
        }
        return null;
    }

    public final CompoundTag getCompound(String name) {
        Tag tag = this.get(name);
        if (tag != null && tag.getId() == Type.Compound) {
            return (CompoundTag) tag;
        }
        return null;
    }

    public final Tag put(String name, Tag tag) {
        return this.mTags.put(name, tag);
    }

    public final void putBoolean(String name, boolean value) {
        this.mTags.put(name, new ByteTag((byte) (value ? 1 : 0)));
    }

    public final void putByte(String name, byte value) {
        this.mTags.put(name, new ByteTag(value));
    }

    public final void putShort(String name, short value) {
        this.mTags.put(name, new ShortTag(value));
    }

    public final void putInt(String name, int value) {
        this.mTags.put(name, new IntTag(value));
    }

    public final void putFloat(String name, float value) {
        this.mTags.put(name, new FloatTag(value));
    }

    public final void putLong(String name, long value) {
        this.mTags.put(name, new LongTag(value));
    }

    public final void putDouble(String name, double value) {
        this.mTags.put(name, new DoubleTag(value));
    }

    public final void putByteArray(String name, byte[] value) {
        this.mTags.put(name, new ByteArrayTag(value));
    }

    public final void putIntArray(String name, int[] value) {
        this.mTags.put(name, new IntArrayTag(value));
    }

    public final void putString(String name, String value) {
        this.mTags.put(name, new StringTag(value));
    }

    public final void putList(String name, ListTag value) {
        this.mTags.put(name, value);
    }

    public final void putCompound(String name, CompoundTag value) {
        this.mTags.put(name, value);
    }

    public final Map<String, Tag> rawView() {
        return this.mTags;
    }

    public final int size() {
        return this.mTags.size();
    }

    public final void clear() {
        this.mTags.clear();
    }

    @Override
    public final CompoundTag clone() {
        CompoundTag compoundTag = new CompoundTag();
        for (Map.Entry<String, Tag> entry : this) {
            compoundTag.mTags.put(entry.getKey(), entry.getValue().copy());
        }
        return compoundTag;
    }

    @NotImplemented
    @Override
    public void write(IDataOutput dos) {

    }

    @Override
    public void load(IDataInput dis) {
        this.mTags.clear();
        ValueHolder<String> name = new ValueHolder<>();
        while (dis.numBytesLeft() != 0) {
            Tag tag = Tag.readNamedTag(dis, name);
            if (tag == null || tag.getId() == Type.End_1)
                break;
            this.put(name.get(), tag);
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Type getId() {
        return Type.Compound;
    }

    @Override
    public CompoundTag copy() {
        return clone();
    }

    @Override
    public Iterator<Map.Entry<String, Tag>> iterator() {
        return this.mTags.entrySet().iterator();
    }

    @Override
    public boolean equals(Tag rhs) {
        if (!Tag.super.equals(rhs))
            return false;
        return Objects.equals(this.mTags, ((CompoundTag) rhs).mTags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundTag tag = (CompoundTag) o;
        return Objects.equals(mTags, tag.mTags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mTags);
    }
}
