package com.ddf.fakeplayer.item.recipe;

import com.ddf.fakeplayer.item.Item;
import com.ddf.fakeplayer.item.ItemDescriptor;
import com.ddf.fakeplayer.item.ItemInstance;
import com.ddf.fakeplayer.item.ItemRegistry;
import com.ddf.fakeplayer.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.UUID;

public abstract class Recipe {
    String mRecipeId;
    ItemPack mMyItems;
    UUID mMyId;
    int mWidth;
    int mHeight;
    int mPriority;
    ArrayList<RecipeIngredient> mMyIngredients;
    String mTag;

    public Recipe(Recipe recipe) {
        this.mRecipeId = recipe.mRecipeId;
        this.mMyItems = recipe.mMyItems;
        this.mMyId = recipe.mMyId;
        this.mWidth = recipe.mWidth;
        this.mHeight = recipe.mHeight;
        this.mPriority = recipe.mPriority;
        this.mMyIngredients = this.mMyItems.getIngredients();
        this.mTag = recipe.mTag;
    }

    public Recipe(final String recipeId, String tag) {
        this.mRecipeId = recipeId;
        this.mMyItems = new ItemPack();
        this.mMyId = new UUID(0, 0);
        this.mWidth = 0;
        this.mHeight = 0;
        this.mMyIngredients = new ArrayList<>();
        this.mTag = tag;
    }

    private int countQuantityOfIngredient(final ItemInstance ingredient) {
        int result = 0;
        ItemDescriptor ingredientDescriptor = ingredient.getDescriptor();
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                RecipeIngredient recipeIngredient = this.getIngredient(x, y);
                if (recipeIngredient.isValid() && recipeIngredient.sameItemAndAux(ingredientDescriptor)) {
                    if (recipeIngredient.getAuxValue() == ingredient.getAuxValue()
                        || recipeIngredient.getAuxValue() == 0x7FFF) {
                        result += recipeIngredient.getStackSize();
                    }
                }
            }
        }
        return result;
    }

    public abstract RecipeIngredient getIngredient(int x, int y);

    public UUID getId() { //+10
        return this.mMyId;
    }

    public ItemPack getItemPack() { //+11
        return this.mMyItems;
    }

    public boolean isMultiRecipe() { //+12
        return false;
    }

    public boolean itemsMatch(final ItemDescriptor lhs, final ItemDescriptor rhs) { //+13
        if (!lhs.isValid() && !rhs.isValid())
            return true;
        if (lhs.isValid() && rhs.isValid())
            return lhs.sameItemAndAux(rhs);
        return false;
    }

    public boolean itemsMatch(final ItemDescriptor lhs, final ItemDescriptor rhs, final CompoundTag rhsTag) { //+14
        if (!lhs.isValid() && rhs.getId() == -1 && rhsTag == null)
            return true;
        if (!lhs.isValid() || rhs.getId() == -1 && rhsTag == null)
            return false;
        return lhs.getId() == rhs.getId() && rhs.getItem() != null && lhs.sameItemAndAux(rhs);
    }

    public boolean itemsMatch(final ItemDescriptor lhs, int rhsId, int rhsAux, final CompoundTag rhsTag) { //+15
        if (lhs.isValid() || rhsId != -1 || rhsTag != null) {
            if (lhs.isValid() && (rhsId != -1 || rhsTag != null)) {
                if (lhs.getId() == rhsId) {
                    Item item = ItemRegistry.getItem(rhsId);
                    if (item == null) {
                        return false;
                    } else {
                        ItemDescriptor rhs = new ItemDescriptor(item, rhsAux);
                        return lhs.sameItemAndAux(rhs);
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}
