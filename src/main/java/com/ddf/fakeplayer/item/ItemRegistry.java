package com.ddf.fakeplayer.item;

import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.block.BlockLegacy;
import com.ddf.fakeplayer.item.items.UnknownItem;
import com.ddf.fakeplayer.util.JsonUtil;
import com.ddf.fakeplayer.util.NotImplemented;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemRegistry {
    public static ArrayList<Item> mItemRegistry = new ArrayList<>();
    public static HashMap<String, Item> mNameToItemMap = new HashMap<>();
    public static HashMap<String, String> mItemAliasLookupMap = new HashMap<>();

    private static ThreadLocal<HashMap<Item, Integer>> itemToIdMap = ThreadLocal.withInitial(HashMap::new);
    private static ThreadLocal<HashMap<Integer, Item>> mIdToItemMap = ThreadLocal.withInitial(HashMap::new);
    private static ThreadLocal<Short> mMaxItemID = ThreadLocal.withInitial(() -> Short.MIN_VALUE);

    private ItemRegistry() {}

    public static void registerItem(Item item, Object... args) {
//        short actualId = item.getId();
//        mMaxItemID = (short) Math.max(actualId, mMaxItemID);
//        mIdToItemMap.put((int) actualId, item);
        mNameToItemMap.put(item.getFullItemName(), item);
        mItemRegistry.add(item);
    }

    @NotImplemented
    public static void registerBlockItem(Item item, BlockLegacy block) {
    }

    public static void registerAlias(String alias, String name) {
        mItemAliasLookupMap.put(alias, name);
    }

    public static Item getItem(final Block block) {
        return ItemRegistry.getItem(block.getLegacyBlock().getBlockItemId());
    }

    public static Item getItem(final BlockLegacy block) {
        return ItemRegistry.getItem(block.getBlockItemId());
    }

    public static Item getItem(final int id) {
        return mIdToItemMap.get().get(id);
    }

    public static void init(List<StartGamePacket.ItemEntry> itemEntries) {
        for (StartGamePacket.ItemEntry itemEntry : itemEntries) {
            String identifier = itemEntry.getIdentifier();
            Item item = mNameToItemMap.get(identifier);
            if (item == null) {
                item = new UnknownItem(identifier, 0xABCD1234);
                registerItem(item);
            }
            short actualId = itemEntry.getId();
            mMaxItemID.set((short) Math.max(actualId, mMaxItemID.get()));
            mIdToItemMap.get().put((int) actualId, item);
            itemToIdMap.get().put(item, (int) actualId);
        }
    }

    public static int getId(Item item) {
        return itemToIdMap.get().getOrDefault(item, 0);
    }

    public static Item lookupByName(final String inString) {
        int itemAux = 0;
        return ItemRegistry.lookupByName(itemAux, inString).outItem;
    }

    public static ItemLookupResult lookupByName(int outItemAux, final String inString) {
        if (inString.isEmpty()) {
            return new ItemLookupResult(null, 0);
        } else {
            JsonUtil.ItemParseResult result = JsonUtil.parseItem(outItemAux, inString);
            String itemName = result.outItemName;
            String itemNamespace = result.outItemNamespace;
            outItemAux = result.inoutItemAux;

            String fullName = itemNamespace + ":" + itemName;
            String blockTestName = "tile." + fullName;
            Item outItem;
            if (fullName.contains("tile.") || ItemRegistry.mNameToItemMap.containsKey(blockTestName)) {
                if (ItemRegistry.mItemAliasLookupMap.containsKey(fullName)) {
                    fullName = ItemRegistry.mItemAliasLookupMap.get(fullName);
                    result = JsonUtil.parseItem(0, fullName);
                    itemName = result.outItemName;
                    itemNamespace = result.outItemNamespace;
                }
            }
            if (ItemRegistry.mNameToItemMap.containsKey(fullName)) {
                outItem = ItemRegistry.mNameToItemMap.get(fullName);
            } else {
                if (fullName.contains("tile.")) {
                    outItem = null;
                    return new ItemLookupResult(outItem, outItemAux);
                }
                String blockTestName_0 = itemNamespace + ":tile." + itemName;
                outItem = ItemRegistry.mNameToItemMap.getOrDefault(blockTestName_0, null);
            }
            return new ItemLookupResult(outItem, outItemAux);
        }
    }

    public static class ItemLookupResult {
        public final Item outItem;
        public final int outItemAux;

        public ItemLookupResult(Item outItem, int outItemAux) {
            this.outItem = outItem;
            this.outItemAux = outItemAux;
        }
    }
}
