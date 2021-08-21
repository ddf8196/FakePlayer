package com.ddf.fakeplayer.nbt;

import com.ddf.fakeplayer.util.NotImplemented;

import java.io.PrintStream;

@NotImplemented
public interface Tag {
    default void deleteChildren() {}

    //void write(IDataOutput dos);
    //void load(IDataInput dis);
    String toString();
    Tag.Type getId();

    default boolean equals(final Tag rhs) {
        return this.getId() == rhs.getId();
    }
    @NotImplemented
    default void print(PrintStream out) {}
    @NotImplemented
    default void print(final String prefix, PrintStream out) {}
    Tag copy();

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
