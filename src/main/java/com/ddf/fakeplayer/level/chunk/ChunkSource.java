package com.ddf.fakeplayer.level.chunk;

import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.level.dimension.Dimension;
import com.ddf.fakeplayer.util.NotImplemented;

@NotImplemented
public class ChunkSource {
    int mChunkSide;
    Level mLevel;
    Dimension mDimension;
    ChunkSource mParent;
    ChunkSource mOwnedParent;
    LevelChunkBuilderData mLevelChunkBuilderData;

    public ChunkSource(ChunkSource parent) {
        this.mChunkSide = parent.getChunkSide();
        this.mLevel = parent.mLevel;
        this.mDimension = parent.mDimension;
        this.mParent = parent;
        this.mOwnedParent = null;
        this.mLevelChunkBuilderData = parent.mDimension.getLevelChunkBuilderData();
    }

    public ChunkSource(Dimension dimension, int side) {
        this.mChunkSide = side;
        if ( dimension != null)
            this.mLevel = dimension.getLevel();
        else
            this.mLevel = null;
        this.mDimension = dimension;
        this.mParent = null;
        this.mOwnedParent = null;
        this.mLevelChunkBuilderData = dimension.getLevelChunkBuilderData();
    }

    public final LevelChunk getAvailableChunk(final ChunkPos cp) {
        LevelChunk lc = this.getExistingChunk(cp);
        if (lc != null && lc.getState().get().ordinal() >= ChunkState.Loaded.ordinal()) {
            return lc;
        } else {
            return null;
        }
    }

    public final LevelChunk getGeneratedChunk(final ChunkPos cp) {
        LevelChunk lc = getExistingChunk(cp);
        if (lc != null && lc.getState().get().ordinal() >= ChunkState.Generated.ordinal()) {
            return lc;
        } else {
            return null;
        }
    }

    public LevelChunk getExistingChunk(final ChunkPos cp) {
        return null;
    }

    public final int getChunkSide() {
        return this.mChunkSide;
    }

    public final Dimension getDimension() {
        return this.mDimension;
    }

    public enum LoadMode {
        None_47,
        Deferred,
    }
}
