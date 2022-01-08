package com.ddf.fakeplayer.item.recipe;

import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.block.BlockLegacy;
import com.ddf.fakeplayer.item.Item;
import com.ddf.fakeplayer.item.ItemDescriptor;
import com.ddf.fakeplayer.item.ItemDescriptorCount;

public class RecipeIngredient extends ItemDescriptorCount {
    public static final RecipeIngredient EMPTY_INGREDIENT = new RecipeIngredient();

    public RecipeIngredient() {
        super();
    }

    public RecipeIngredient(final Block block, /*uint16_t*/int stackSize) {
        super(block, stackSize);
    }

    public RecipeIngredient(final BlockLegacy block, /*uint16_t*/int stackSize) {
        super(block, stackSize);
    }

    public RecipeIngredient(final Item item, int auxValue, /*uint16_t*/int stackSize) {
        super(item, auxValue, stackSize);
    }

    public RecipeIngredient(final ItemDescriptor descriptor, /*uint16_t*/int stackSize) {
        super(descriptor, stackSize);
    }

    public RecipeIngredient(final RecipeIngredient a2) {
        super(a2);
    }
}
