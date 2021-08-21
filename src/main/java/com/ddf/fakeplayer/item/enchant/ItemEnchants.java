package com.ddf.fakeplayer.item.enchant;

import com.ddf.fakeplayer.nbt.ListTag;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.ArrayList;

public class ItemEnchants {
    private int mSlot;
    private ArrayList<ArrayList<EnchantmentInstance>> mItemEnchants = new ArrayList<>(3);

    public ItemEnchants(ItemEnchants enchants) {
        this.mSlot = enchants.mSlot;
        this.mItemEnchants.addAll(enchants.mItemEnchants);
    }

    public ItemEnchants(int slot) {
        this.mSlot = slot;
        for (int i = 0; i < 3; i++) {
            mItemEnchants.add(i, new ArrayList<>());
        }
    }

    public ItemEnchants(int slot, final ListTag tag) {
        this.mSlot = slot;
        for (int i = 0; i < 3; i++) {
            mItemEnchants.add(i, new ArrayList<>());
        }
        this._fromList(tag);
    }

    @NotImplemented
    private void _fromList(final ListTag tag) {
    }

    public int hasEnchant(Enchant.Type enchantType) {
        int activation = EnchantUtils.determineActivation(enchantType);
        if (activation != Enchant.Activation._invalid.ordinal()) {
            for (EnchantmentInstance enchant : this.mItemEnchants.get(activation)) {
                if (enchant.getEnchantType() == enchantType)
                    return enchant.getEnchantLevel();
            }
        }
        return 0;
    }
}
