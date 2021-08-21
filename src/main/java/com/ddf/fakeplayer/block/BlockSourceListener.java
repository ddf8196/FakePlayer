package com.ddf.fakeplayer.block;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.blockactor.BlockActor;

public interface BlockSourceListener {
    default void onSourceCreated(BlockSource source) {
    }

    default void onSourceDestroyed(BlockSource source) {
    }

    default void onAreaChanged(BlockSource source, final BlockPos min, final BlockPos max) {
    }

    default void onBlockChanged(BlockSource source, final BlockPos pos, /*uint32_t*/long layer, final Block block, final Block oldBlock, int updateFlags, final /*ActorBlockSyncMessage*/Object syncMsg) {
    }

    default void onBrightnessChanged(BlockSource source, final BlockPos pos) {
        this.onAreaChanged(source, pos, pos);
    }

    default void onBlockEntityChanged(BlockSource source, BlockActor te) {
    }

    default void onBlockEntityAboutToBeRemoved(BlockSource source, BlockActor te) {
    }

    default void onEntityChanged(BlockSource source, Actor entity) {
    }

    default void onBlockEvent(BlockSource source, int x, int y, int z, int b0, int b1) {
    }
}
