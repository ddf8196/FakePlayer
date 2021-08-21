package com.ddf.fakeplayer.block;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.nbt.CompoundTag;

public class Block {
    private final /*uint16_t DataID*/short mData;
    private BlockLegacy mLegacyBlock;
    private CompoundTag mSerializationId;
    private int mRuntimeId;
    private boolean mHasRuntimeId;

    public Block(final /*uint16_t DataID*/short data, BlockLegacy oldBlock) {
        this.mData = data;
        this.mLegacyBlock = oldBlock;
        this.mSerializationId = new CompoundTag();
        this.mRuntimeId = 0;
        this.mHasRuntimeId = false;
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

    public final boolean canHurtAndBreakItem() {
        return this.mLegacyBlock.canHurtAndBreakItem();
    }

    public final boolean hasProperty(BlockProperty type) {
        return this.mLegacyBlock.hasProperty(type);
    }

    public final void playerDestroy(Player player, final BlockPos pos) {
        this.mLegacyBlock.playerDestroy(player, pos, this);
    }
}
