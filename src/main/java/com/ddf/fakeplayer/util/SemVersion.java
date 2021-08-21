package com.ddf.fakeplayer.util;

public class SemVersion {
    private /*uint16_t*/int mMajor;
    private /*uint16_t*/int mMinor;
    private /*uint16_t*/int mPatch;
    private String mPreRelease;
    private String mBuildMeta;
    private String mFullVersionString;
    private boolean mValidVersion;
    private boolean mAnyVersion;

    public enum MatchType {
        Full,
        Partial,
        None_4
    }

    public enum ParseOption {
        AllowAnyVersion,
        NoAnyVersion
    }
}
