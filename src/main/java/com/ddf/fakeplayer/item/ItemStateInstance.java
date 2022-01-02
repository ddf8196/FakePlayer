package com.ddf.fakeplayer.item;

import com.ddf.fakeplayer.state.ItemState;
import com.ddf.fakeplayer.util.NotImplemented;

@NotImplemented
public final class ItemStateInstance {
    private /*uint32_t*/int mMaxBits;
    private /*uint32_t*/int mStartBit;
    private /*uint32_t*/int mNumBits;
    private /*uint32_t*/int mVariationCount;
    private /*uint32_t*/int mMask;
    private boolean mInitialized;
    private ItemState mState;

    public ItemStateInstance() {
        this.mMaxBits = 16;
        this.mStartBit = 0;
        this.mNumBits = 0;
        this.mVariationCount = 0;
        this.mMask = 0;
        this.mInitialized = false;
        this.mState = null;
    }

    public int initState(/*uint32_t* */int startBit, /*uint32_t*/int numBits, /*uint32_t*/int variationCount, final ItemState state) {
        if (!this.mInitialized) {
            this.mNumBits = numBits;
            this.mStartBit = this.mNumBits + startBit - 1;
            this.mInitialized = true;
            startBit += numBits;
            this.mMask = (0xFFFF << ((this.mMaxBits & 0xFF) - (this.mNumBits & 0xFF))) >> ((this.mMaxBits & 0xFF) - 1 - (this.mStartBit & 0xFF));
            this.mVariationCount = variationCount;
            this.mState = state;
        }
        return startBit;
    }

    public final int get(final short data) {
        return (data >> ((this.mStartBit & 0xFF) - (this.mNumBits & 0xFF) + 1)) & (0xFFFF >> ((this.mMaxBits & 0xFF) - (this.mNumBits & 0xFF)));
    }

    public final ItemState getState() {
        return this.mState;
    }

    public final boolean isInitialized() {
        return this.mInitialized;
    }

    public boolean isValidData(/*uint32_t*/int data) {
        return (this.mMask & data) >> ((this.mStartBit & 0xFF) - (this.mNumBits & 0xFF) + 1) < this.mVariationCount;
    }
}
