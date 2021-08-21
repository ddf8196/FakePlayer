package com.ddf.fakeplayer.item;

import com.ddf.fakeplayer.block.BlockLegacy;
import com.ddf.fakeplayer.item.items.UnknownItem;
import com.ddf.fakeplayer.util.JsonUtil;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.Pair;
import com.ddf.fakeplayer.util.tuple.Tuple2;
import com.ddf.fakeplayer.util.tuple.Tuple4;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemRegistry {
    public static ArrayList<Item> mItemRegistry = new ArrayList<>();
    public static HashMap<String, Item> mNameToItemMap = new HashMap<>();
    public static HashMap<String, String> mItemAliasLookupMap = new HashMap<>();

    private HashMap<Integer, Item> mIdToItemMap = new HashMap<>();
    private HashMap<Item, Integer> itemToIdMap = new HashMap<>();
    private short mMaxItemID = Short.MIN_VALUE;

    private ArrayList<Pair<Integer, ItemStackBase>> nonInitializedItemStacks = new ArrayList<>();
    private boolean initialized = false;

    public static void registerItem(Item item) {
        mNameToItemMap.put(item.getFullItemName(), item);
        mItemRegistry.add(item);
    }

    @NotImplemented
    public static void registerBlockItem(Item item, BlockLegacy block) {
    }

    public static void registerAlias(String alias, String name) {
        mItemAliasLookupMap.put(alias, name);
    }

    public Item getItem(int id) {
        return mIdToItemMap.get(id);
    }

    public int getId(Item item) {
        Integer id = itemToIdMap.get(item);
        if (id != null) {
            return id;
        } else {
            return 0;
        }
    }

    public void addNonInitializedItemStack(ItemStackBase itemStack, int id) {
        if (initialized)
            return;
        nonInitializedItemStacks.add(new Pair<>(id, itemStack));
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void initialize(List<StartGamePacket.ItemEntry> itemEntries) {
        if (initialized)
            return;
        for (StartGamePacket.ItemEntry itemEntry : itemEntries) {
            String identifier = itemEntry.getIdentifier();
            int id = itemEntry.getId();
            mMaxItemID = (short) Math.max(id, mMaxItemID);
            Item item = mNameToItemMap.get(identifier);
            if (item == null) {
                item = new UnknownItem(identifier);
            }
            mIdToItemMap.put(id, item);
            itemToIdMap.put(item, id);
        }

        for (Pair<Integer, ItemStackBase> pair : nonInitializedItemStacks) {
            pair.getValue()._setItem(pair.getKey());
        }
        nonInitializedItemStacks.clear();
        nonInitializedItemStacks = null;
        initialized = true;
    }

    public static Item lookupByName(final String inString) {
        int itemAux = 0;
        return ItemRegistry.lookupByName(itemAux, inString).getT1();
    }

    public static Tuple2<Item, Integer> lookupByName(int outItemAux, final String inString) {
        Item outItem = null;
        if (inString.isEmpty()) {
            return new Tuple2<>(null, 0);
        } else {
            String itemName = null;
            String itemNamespace = null;
            Tuple4<Boolean, String, String, Integer> result = JsonUtil.parseItem(itemName, itemNamespace, outItemAux, inString);
            itemName = result.getT2();
            itemNamespace = result.getT3();
            outItemAux = result.getT4();
            String fullName = itemNamespace + ":" + itemName;
            String blockTestName = "tile." + fullName;
            if (fullName.indexOf("tile.", 0) != -1 || ItemRegistry.mNameToItemMap.containsKey(blockTestName)) {
                if (ItemRegistry.mItemAliasLookupMap.containsKey(fullName)) {
                    fullName = ItemRegistry.mItemAliasLookupMap.get(fullName);
                    int tempAux = 0;
                    result = JsonUtil.parseItem(itemName, itemNamespace, tempAux, fullName);
                    itemName = result.getT2();
                    itemNamespace = result.getT3();
                    tempAux = result.getT4();
                }
                if (ItemRegistry.mNameToItemMap.containsKey(fullName)) {
                    outItem = ItemRegistry.mNameToItemMap.get(fullName);
                } else {
                    if (fullName.indexOf("tile.", 0) != -1) {
                        outItem = null;
                        return new Tuple2<>(outItem, outItemAux);
                    }
                    String blockTestName_0 = itemNamespace + ":tile." + itemName;
                    outItem = ItemRegistry.mNameToItemMap.getOrDefault(blockTestName_0, null);
                }
            }
        }
        return new Tuple2<>(outItem, outItemAux);
    }
}
