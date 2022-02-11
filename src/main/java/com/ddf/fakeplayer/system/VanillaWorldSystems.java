package com.ddf.fakeplayer.system;

import com.ddf.fakeplayer.block.*;
import com.ddf.fakeplayer.block.blocktypes.UnknownBlock;
import com.ddf.fakeplayer.state.VanillaStates;
import com.ddf.fakeplayer.util.DataConverter;
import com.ddf.fakeplayer.util.ProtocolVersionUtil;
import com.ddf.fakeplayer.util.mc.CompoundTagUpdaterContext;
import com.ddf.fakeplayer.util.mc.ResourcePackManager;
import com.ddf.fakeplayer.util.mc.VanillaBlockUpdater;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.protocol.bedrock.v475.Bedrock_v475;

import java.util.concurrent.atomic.AtomicInteger;

public class VanillaWorldSystems extends WorldSystems {

    private static AtomicInteger _initCount = new AtomicInteger(0);
    public static boolean init(ResourcePackManager resourcePackManager, /*BlockDefinitionGroup*/Object blockDefinitionGroup) {
        if (_initCount.incrementAndGet() != 1 )
            return false;
        Material.initMaterials();
        VanillaStates.registerStates();
        BedrockBlockTypes.registerBlocks();
        VanillaBlockTypes.registerBlocks();
        //Register unknown blocks
        for (NbtMap nbtMap : ProtocolVersionUtil.getBlockPalette(Bedrock_v475.V475_CODEC)) {
            NbtMap block = nbtMap.getCompound("block");
            String name = block.getString("name");
            int id = nbtMap.getInt("id");
            BlockLegacy blockLegacy = BlockTypeRegistry.lookupByName(name);
            if (blockLegacy == null)
                BlockTypeRegistry.registerBlock(new UnknownBlock(name, id, DataConverter.compoundTag(block)));
            else if (blockLegacy instanceof UnknownBlock)
                ((UnknownBlock) blockLegacy).addState(DataConverter.compoundTag(block));
        }
//        if (blockDefinitionGroup != null)
//            blockDefinitionGroup.registerBlocks();
        VanillaBlockUpdater.initialize();
        BlockTypeRegistry.prepareBlocks(VanillaBlockUpdater.get().latestVersion());
        BedrockBlocks.assignBlocks();
        VanillaBlocks.assignBlocks();
        WorldSystems.init(resourcePackManager);
        return true;
    }
}
