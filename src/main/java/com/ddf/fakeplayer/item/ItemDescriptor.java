package com.ddf.fakeplayer.item;

import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.block.BlockLegacy;
import com.ddf.fakeplayer.util.MathUtil;

public class ItemDescriptor {
    private Item mItem;
    private Block mBlock;
    private short mAuxValue;
    private boolean mValid;

    public ItemDescriptor() {
        this.mItem = null;
        this.mBlock = null;
        this.mAuxValue = 0;
        this.mValid = false;
    }

    public ItemDescriptor(final Block block) {
        this.mAuxValue = 0;
        this.mValid = true;
        Item other = ItemRegistry.getItem(block);
        this.mItem = other;
        this.mBlock = block;
        this.mAuxValue = 0;
        this.mValid = this.mItem != null;
        if (!this.mValid) {
            this.mItem = null;
            this.mAuxValue = 0;
            this.mBlock = null;
        }
    }

    public ItemDescriptor(final BlockLegacy block) {
        this.mAuxValue = 0;
        this.mValid = true;
        this.mAuxValue = 0x7FFF;
        Item other = ItemRegistry.getItem(block);
        this.mItem = other;
        this.mBlock = block.getDefaultState();
        this.mValid = this.mItem != null;
        if (!this.mValid) {
            this.mItem = null;
            this.mAuxValue = 0;
            this.mBlock = null;
        }
    }

    public ItemDescriptor(final Item item, int auxValue) {
        this(item.getId(), auxValue);
    }

    public ItemDescriptor(int id, int auxValue) {
        this.mItem = null;
        this.mAuxValue = 0;
        this.mValid = true;
        Item other = ItemRegistry.getItem(id);
        this.mItem = other;
        this.mBlock = null;
        this.setAuxValue((short) auxValue);
        this.mValid = this.mItem != null || id == 0;
        if (!this.mValid) {
            this.mItem = null;
            this.mAuxValue = 0;
            this.mBlock = null;
        }
    }

    public ItemDescriptor(final ItemDescriptor a2) {
        this.mItem = a2.mItem;
        this.mBlock = a2.mBlock;
        this.mAuxValue = a2.mAuxValue;
        this.mValid = a2.mValid;
    }

    public final Item getItem() {
        return this.mItem;
    }

    public final short getId() {
        if (!this.mValid)
            return -1;
        if (this.mItem == null)
            return 0;
        return this.mItem.getId();
    }

    public final short getAuxValue() {
        if (this.mBlock == null || this.mAuxValue == 0x7FFF )
            return this.mAuxValue;
        else
            return this.mBlock.getDataDEPRECATED();
    }

    public final Block getBlock() {
        return this.mBlock;
    }

    public final BlockLegacy getLegacyBlock() {
        if (this.mItem == null)
            return null;
        return this.mItem.getLegacyBlock();
    }

    public final boolean isValid() {
        return this.mValid;
    }

    public final void setAuxValue(short auxValue) {
        this.mAuxValue = (short) MathUtil.clamp(auxValue, 0, Short.MAX_VALUE);
    }

    public final boolean sameItem(final ItemDescriptor b) {
        if (this.mItem != null) {
            return this.mItem == b.mItem;
        }
        return false;
    }

    public final boolean sameItemAndAux(final ItemStack itemStack) {
        boolean result = this.mItem == itemStack.getItem();
        if (this.mAuxValue != 0x7FFF && itemStack.getAuxValue() != 0x7FFF) {
            if ( this.mBlock != null) {
                result = this.mBlock == itemStack.getBlock() && result;
            } else if ( result && itemStack.isDamageableItem()) {
                result = this.mAuxValue == itemStack.getDamageValue() && result;
            } else {
                result = this.mAuxValue == itemStack.getAuxValue() && result;
            }
        }
        return result;
    }

    public final boolean sameItemAndAux(final ItemDescriptor b) {
        boolean result = this.sameItem(b);
        if ( this.mAuxValue != 0x7FFF && b.mAuxValue != 0x7FFF) {
            if ( this.mBlock != null)
                return this.mBlock == b.mBlock && result;
            else
                return this.mAuxValue == b.mAuxValue && result;
        }
        return result;
    }
}
