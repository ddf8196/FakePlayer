package com.ddf.fakeplayer.item;

import com.ddf.fakeplayer.item.items.AirBlockItem;

public class BedrockItems {
    public static Item mAir;

    public static void registerItems() {
        BedrockItems.mAir = new AirBlockItem("air");
        ItemRegistry.registerItem(BedrockItems.mAir);
    }
}
