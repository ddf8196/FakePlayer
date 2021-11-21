package com.ddf.fakeplayer.level.chunk;

public enum ChunkState {
    Unloaded,
    Generating,
    Generated,
    PostProcessing,
    PostProcessed,
    CheckingForReplacementData,
    NeedsLighting,
    Lighting_0,
    Loaded
}
