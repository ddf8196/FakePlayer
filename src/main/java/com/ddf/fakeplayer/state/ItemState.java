package com.ddf.fakeplayer.state;

import com.ddf.fakeplayer.nbt.CompoundTag;

import java.util.function.Function;

public abstract class ItemState {
    private final int mID;
    private final int mVariationCount;
    private final String mName;
    private ItemState.StateListNode mNode;

    public ItemState(int id, final String stateName, int variationCount) {
        this.mID = id;
        this.mVariationCount = variationCount;
        this.mName = stateName;
        this.mNode = new StateListNode(this);
    }

    public static void forEachState(Function<ItemState, Boolean> a1) {
        for (ItemState.StateListNode walk = ItemState.StateListNode.mHead; walk != null; walk = walk.mNext) {
            if (!a1.apply(walk.mState)) {
                break;
            }
        }
    }

    public abstract void toNBT(CompoundTag tag, int val); //+2
    public abstract int fromNBT(final CompoundTag tag); //+3

    public final int getID() {
        return this.mID;
    }

    public final String getName() {
        return this.mName;
    }

    public static class StateListNode {
        private static ItemState.StateListNode mHead;
        private ItemState.StateListNode mNext;
        private ItemState.StateListNode mPrev;
        private ItemState mState;

        public StateListNode(ItemState state) {
            this.mNext = null;
            this.mPrev = null;
            this.mState = state;
            if (ItemState.StateListNode.mHead != null){
                ItemState.StateListNode.mHead.mPrev = this;
                this.mNext = ItemState.StateListNode.mHead;
            }
            ItemState.StateListNode.mHead = this;
        }
    }
}
