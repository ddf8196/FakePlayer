package com.ddf.fakeplayer.level.chunk;

import com.ddf.fakeplayer.level.dimension.Dimension;
import com.ddf.fakeplayer.util.NotImplemented;

import java.lang.ref.WeakReference;
import java.util.HashMap;

@NotImplemented
public class NetworkChunkSource extends ChunkSource {
    private HashMap<ChunkPos, WeakReference<LevelChunk>> mChunkMap;

    public NetworkChunkSource(Dimension dimension) {
        super(dimension, 16);
        this.mChunkMap = new HashMap<>();
    }
}
