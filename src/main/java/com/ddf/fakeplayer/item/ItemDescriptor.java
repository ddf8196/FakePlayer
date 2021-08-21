package com.ddf.fakeplayer.item;

import com.ddf.fakeplayer.block.Block;

public class ItemDescriptor {
    private Item mItem;
    private Block mBlock;
    private short mAuxValue;
    private boolean mValid;

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
}
