package com.ddf.fakeplayer.util.mc;

public class Pos {
    int x, y, z;

    public Pos() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Pos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final Pos add(int o) {
        return new Pos(o + this.x, o + this.y, o + this.z);
    }

    public final Pos subtract(final Pos rhs) {
        return new Pos(this.x - rhs.x, this.y - rhs.y, this.z - rhs.z);
    }
}
