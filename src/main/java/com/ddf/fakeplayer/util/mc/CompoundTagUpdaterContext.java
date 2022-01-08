package com.ddf.fakeplayer.util.mc;

import java.util.ArrayList;

public class CompoundTagUpdaterContext {
    private byte mUpdaterVersion;
    private ArrayList<CompoundTagUpdater> mUpdaters = new ArrayList<>();
    private boolean mIsSorted;

    public int latestVersion() {
        if (this.mUpdaters.isEmpty())
            return 0;
        return this.mUpdaters.get(this.mUpdaters.size() - 1).getVersion();
    }
}
