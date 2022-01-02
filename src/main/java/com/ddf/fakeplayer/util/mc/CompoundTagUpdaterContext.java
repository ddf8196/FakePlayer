package com.ddf.fakeplayer.util.mc;

import java.util.ArrayList;

public class CompoundTagUpdaterContext {
    byte mUpdaterVersion;
    ArrayList<CompoundTagUpdater> mUpdaters;
    boolean mIsSorted;

    public int latestVersion() {
        if (this.mUpdaters.isEmpty())
            return 0;
        return this.mUpdaters.get(this.mUpdaters.size() - 1).getVersion();
    }
}
