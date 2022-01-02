package com.ddf.fakeplayer.block;

public class BedrockBlocks {
    public static Block mAir;

    public static void assignBlocks() {
        BedrockBlocks.mAir = BedrockBlockTypes.mAir.getDefaultState();
    }
}
