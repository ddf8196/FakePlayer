package com.ddf.fakeplayer.item;

import com.ddf.fakeplayer.block.VanillaBlockTypes;

public class VanillaItemTiers {
    public static final Item.Tier WOOD = new Item.Tier(0, 59, 2.0f, 0, 15);
    public static final Item.Tier STONE = new Item.Tier(1, 131, 4.0f, 1, 5);
    public static final Item.Tier IRON = new Item.Tier(2, 250, 6.0f, 2, 14);
    public static final Item.Tier GOLD = new Item.Tier(0, 32, 12.0f, 0, 22);
    public static final Item.Tier DIAMOND = new Item.Tier(3, 1561, 8.0f, 3, 10);
    public static final Item.Tier NETHERITE = new Item.Tier(4, 2031, 9.0f, 4, 15);

    public static ItemStack getTierItem(final Item.Tier tier) {
        switch (tier.getLevel()) {
            case 0:
                if (tier.getSpeed() == 2.0f){
                    return new ItemStack(VanillaBlockTypes.mWoodPlanks, 1);
                } else {
                    return new ItemStack(VanillaItems.mGoldIngot);
                }
            case 1:
                return new ItemStack(VanillaBlockTypes.mStone, 1);
            case 2:
                return new ItemStack(VanillaItems.mIronIngot);
            case 3:
                return new ItemStack(VanillaItems.mDiamond);
            default:
                return new ItemStack();
        }
    }
}
