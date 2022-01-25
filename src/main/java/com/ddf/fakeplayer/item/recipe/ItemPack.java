package com.ddf.fakeplayer.item.recipe;

import com.ddf.fakeplayer.item.ItemDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class ItemPack {
    HashMap<ItemDescriptor, Integer> mIngredients;

    public final ArrayList<RecipeIngredient> getIngredients() {
        ArrayList<RecipeIngredient> retstr = new ArrayList<>();
        for (Map.Entry<ItemDescriptor, Integer> ingredientPair : mIngredients.entrySet()) {
            RecipeIngredient ingredient = new RecipeIngredient(ingredientPair.getKey(), ingredientPair.getValue());
            retstr.add(ingredient);
        }
        return retstr;
    }
}
