package com.ddf.fakeplayer.nbt;

import com.ddf.fakeplayer.util.IDataInput;
import com.ddf.fakeplayer.util.IDataOutput;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.ValueHolder;

import java.io.PrintStream;

@NotImplemented
public interface Tag {
    default void deleteChildren() {} //+2

    void write(IDataOutput dos); //+3
    void load(IDataInput dis); //+4
    String toString(); //+5
    Tag.Type getId(); //+6

    default boolean equals(final Tag rhs) {
        return this.getId() == rhs.getId();
    }
    @NotImplemented
    default void print(PrintStream out) {}
    @NotImplemented
    default void print(final String prefix, PrintStream out) {}
    Tag copy();

    static Tag newTag(Tag.Type type) {
        switch (type) {
            case End_1:
                return new EndTag();
            case Byte_0:
                return new ByteTag();
            case Short_0:
                return new ShortTag();
            case Int_2:
                return new IntTag();
            case Int64_0:
                return new LongTag();
            case Float_3:
                return new FloatTag();
            case Double:
                return new DoubleTag();
            case ByteArray:
                return new ByteArrayTag();
            case String_1:
                return new StringTag();
            case List_0:
                return new ListTag();
            case Compound:
                return new CompoundTag();
            case IntArray:
                return new IntArrayTag();
            default:
                return null;
        }
    }

    static Tag readNamedTag(IDataInput dis, ValueHolder<String> name) {
        Tag.Type type = Type.values()[dis.readByte()];
        if (type != Type.End_1) {
            name.set(dis.readString());
            Tag tag = Tag.newTag(type);
            if (tag != null) {
                tag.load(dis);
                return tag;
            } else {
                return null;
            }
        } else {
            return new EndTag();
        }
    }

    enum Type {
        End_1,
        Byte_0,
        Short_0,
        Int_2,
        Int64_0,
        Float_3,
        Double,
        ByteArray,
        String_1,
        List_0,
        Compound,
        IntArray,
    }
}
