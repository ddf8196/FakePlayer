package com.ddf.fakeplayer.block;

import com.ddf.fakeplayer.block.blocktypes.AirBlock;

public class BedrockBlockTypes {
    public static BlockLegacy mAir = new AirBlock("air", 0, new Material(MaterialType.Air, Material.Settings.Gas, 0.0f));

    public static void registerBlocks() {
//        BedrockBlockTypes.mAir = /*.createWeakPtr()*/;
        BedrockBlockTypes.mAir = new AirBlock("air", 0, Material.getMaterial(MaterialType.Air))
                .setDestroyTime(-1.0f)
                .setAllowsRunes(true)
                .addBlockProperty(BlockProperty.CanBeBuiltOver);
        BlockTypeRegistry.registerBlock(BedrockBlockTypes.mAir);
    }
}
