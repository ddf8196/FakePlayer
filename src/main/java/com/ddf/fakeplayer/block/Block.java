package com.ddf.fakeplayer.block;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.item.ItemStateInstance;
import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.state.ItemState;
import com.ddf.fakeplayer.util.AABB;
import com.ddf.fakeplayer.util.NotImplemented;

public class Block {
    private final /*uint16_t DataID*/short mData;
    private BlockLegacy mLegacyBlock;
    public CompoundTag mSerializationId;
    private int mRuntimeId;
    private boolean mHasRuntimeId;

    public Block(final /*uint16_t DataID*/short data, BlockLegacy oldBlock) {
        this.mData = data;
        this.mLegacyBlock = oldBlock;
        this.mSerializationId = new CompoundTag();
        this.mRuntimeId = 0;
        this.mHasRuntimeId = false;
    }

    public final boolean canContainLiquid() {
        return this.mLegacyBlock.canContainLiquid();
    }

    public final boolean canHurtAndBreakItem() {
        return this.mLegacyBlock.canHurtAndBreakItem();
    }

    public final AABB getAABB(BlockSource regionDEPRECATED, final BlockPos pos, AABB bufferValue, boolean isClipping) {
        return this.mLegacyBlock.getAABB(regionDEPRECATED, pos, this, bufferValue, isClipping);
    }

    public final Block getDefaultState() {
        return this.mLegacyBlock.getDefaultState();
    }

    public final float getDestroySpeed() {
        return this.mLegacyBlock.getDestroySpeed();
    }

    public BlockRenderLayer getRenderLayer() {
        return this.mLegacyBlock.getRenderLayer();
    }

    public final BlockLegacy getLegacyBlock() {
        return this.mLegacyBlock;
    }

    public final Material getMaterial() {
        return this.mLegacyBlock.getMaterial();
    }

    public final boolean getIgnoresDestroyPermissions(Actor entity, final BlockPos pos) {
        return this.mLegacyBlock.getIgnoresDestroyPermissions(entity, pos);
    }

    public int getState(final ItemState stateType) {
        return this.mLegacyBlock.getState(stateType, this.mData);
    }

    public final boolean hasProperty(BlockProperty type) {
        return this.mLegacyBlock.hasProperty(type);
    }

    @NotImplemented
    public final boolean isInteractiveBlock() {
        return false;
//        return this.mLegacyBlock.isInteractiveBlock();
    }

    public final void setRuntimeId(final int runtimeId) {
        this.mRuntimeId = runtimeId;
        this.mHasRuntimeId = true;
    }

    public final void buildSerializationId(/*uint32_t*/int latestUpdaterVersion) {
        this.mSerializationId.clear();
        this.mSerializationId.putString("name", this.mLegacyBlock.getFullName());
        this.mSerializationId.putInt("version", latestUpdaterVersion);
        CompoundTag tag = new CompoundTag();
        for (ItemStateInstance __begin : this.mLegacyBlock.mStates) {
            if (__begin != null && __begin.isInitialized()) {
                ItemState state = __begin.getState();
                state.toNBT(tag, this.getState(state));
            }
        }
        this.mSerializationId.put("states", tag);
    }

    @NotImplemented
    public boolean use(Player player, final BlockPos pos) {
        return false;
        //return this.mLegacyBlock.use(player, pos);
    }

    public final void playerDestroy(Player player, final BlockPos pos) {
        this.mLegacyBlock.playerDestroy(player, pos, this);
    }
}
