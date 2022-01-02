package com.ddf.fakeplayer.item;

import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.util.NotImplemented;

@NotImplemented
public class ItemInstance extends ItemStackBase {
    public static final ItemInstance EMPTY_ITEM = new ItemInstance(BedrockItems.mAir, 0, 0, null);

    public ItemInstance() {
        super();
    }

    public ItemInstance(final Item item) {
        super(item);
    }

    public ItemInstance(final Item item, int count) {
        super(item, count);
    }

    public ItemInstance(final Item item, int count, int auxValue) {
        super(item, count, auxValue);
    }

    public ItemInstance(final Item item, int count, int auxValue, final CompoundTag _userData) {
        super(item, count, auxValue, _userData);
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

    public static ItemInstance fromTag(final CompoundTag tag) {
        ItemInstance ii = new ItemInstance();
        ii.load(tag);
        if (ii.isItem())
            return new ItemInstance(ii);
        else
            return new ItemInstance();
    }
}
