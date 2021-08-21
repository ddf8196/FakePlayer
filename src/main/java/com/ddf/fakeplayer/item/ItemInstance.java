package com.ddf.fakeplayer.item;

import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.util.NotImplemented;

@NotImplemented
public class ItemInstance extends ItemStackBase {
    public static final ItemInstance EMPTY_ITEM = new ItemInstance(null, BedrockItems.mAir, 0, 0, null);

    public ItemInstance(ItemRegistry registry) {
        super(registry);
    }

    public ItemInstance(ItemRegistry registry, final Item item) {
        super(registry, item);
    }

    public ItemInstance(ItemRegistry registry, final Item item, int count) {
        super(registry, item, count);
    }

    public ItemInstance(ItemRegistry registry, final Item item, int count, int auxValue) {
        super(registry, item, count, auxValue);
    }

    public ItemInstance(ItemRegistry registry, final Item item, int count, int auxValue, final CompoundTag _userData) {
        super(registry, item, count, auxValue, _userData);
    }

    public ItemInstance(final ItemInstance rhs) {
        super(rhs);
    }

    public ItemInstance(ItemStack itemStack) {
        super(itemStack);
    }

    public ItemInstance(final Block block, int count, final CompoundTag _userData) {
        super(block, count, _userData);
    }

    public static ItemInstance fromTag(ItemRegistry registry, final CompoundTag tag) {
        ItemInstance ii = new ItemInstance(registry);
        ii.load(tag);
        if (ii.isItem())
            return new ItemInstance(ii);
        else
            return new ItemInstance(registry);
    }
}
