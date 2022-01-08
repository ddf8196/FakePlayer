package com.ddf.fakeplayer.system;

import com.ddf.fakeplayer.actor.mob.effect.MobEffect;
import com.ddf.fakeplayer.blockactor.BlockActor;
import com.ddf.fakeplayer.item.BedrockItems;
import com.ddf.fakeplayer.item.ItemRegistry;
import com.ddf.fakeplayer.item.VanillaItems;
import com.ddf.fakeplayer.item.enchant.Enchant;
import com.ddf.fakeplayer.util.mc.ResourcePackManager;

public class WorldSystems {
    private static boolean mInitialized;
    protected static void init(ResourcePackManager rpm) {
        if (!WorldSystems.mInitialized) {
//            GoalDefinition.init();
//            MobEffect.initEffects(rpm);
//            Potion.initPotions();
//            Enchant.initEnchants(false);
//            ElementBlock.initElements();
//            ItemRegistry.startRegistration();
            BedrockItems.registerItems();
//            ProjectileFactory.initFactory();
//            BlockActor.initBlockEntities();
            VanillaItems.registerItems(false);
//            VanillaItems.initCreativeCategories();
//            if (rpm != null)
//                ItemRegistry.initServerData(rpm);
//            FireBlock.registerFlammableBlocks();
//            PotionBrewing.initPotionBrewing();
//            ItemRegistry.finishedRegistration();
//            SubChunkRelighter.initializeStatics();
            WorldSystems.mInitialized = true;
        }
    }
}
