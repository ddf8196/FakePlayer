package com.ddf.fakeplayer.util.mc;


import java.util.ArrayList;
import java.util.function.Function;

public final class CompoundTagUpdater {
    int mVersion;
    ArrayList<Function<CompoundTagEditHelper, Boolean>> mFilters;
    ArrayList<Function<CompoundTagEditHelper, Void>> mUpdates;
    public final int getVersion() {
        return this.mVersion;
    }
}
