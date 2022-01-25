package com.ddf.fakeplayer.item.recipe;

import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.item.Item;
import com.ddf.fakeplayer.item.ItemInstance;
import com.ddf.fakeplayer.util.Pair;
import com.ddf.fakeplayer.util.mc.ResourcePackManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public final class Recipes {
    ResourcePackManager mResourcePackManager;
    TreeMap<String, TreeMap<String, Recipe>> mRecipes;
    TreeMap<Recipes.FurnaceRecipeKey, ItemInstance> mFurnaceRecipes;
    boolean mInitializing;
    TreeMap<ItemInstance, HashMap<String ,Recipe>> mRecipesByOutput;
    ArrayList<Pair<Boolean, Runnable>> mListeners;

    static class FurnaceRecipeKey {
        int mID;
        String mTag;
    }

    static class Type {
        Item mItem;
        Block mBlock;
        RecipeIngredient mIngredient;
        char mC;
    }
}
