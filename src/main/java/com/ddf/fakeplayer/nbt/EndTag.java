package com.ddf.fakeplayer.nbt;

import com.ddf.fakeplayer.util.IDataInput;
import com.ddf.fakeplayer.util.IDataOutput;
import com.ddf.fakeplayer.util.NotImplemented;

public class EndTag implements Tag {
    @NotImplemented
    @Override
    public void write(IDataOutput dos) {

    }

    @Override
    public void load(IDataInput dis) {
    }

    @Override
    public Type getId() {
        return Type.End_1;
    }

    @Override
    public EndTag copy() {
        return new EndTag();
    }
}
