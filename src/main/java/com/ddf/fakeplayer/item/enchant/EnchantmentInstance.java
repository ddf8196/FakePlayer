package com.ddf.fakeplayer.item.enchant;

public class EnchantmentInstance {
    private Enchant.Type mEnchantType;
    private int mLevel;

    public EnchantmentInstance() {
        this.mEnchantType = Enchant.Type.InvalidEnchantment;
        this.mLevel = 0;
    }

    public EnchantmentInstance(Enchant.Type enchantType, int level) {
        this.mEnchantType = enchantType;
        this.mLevel = level;
    }

    public Enchant.Type getEnchantType() {
        return this.mEnchantType;
    }

    public int getEnchantLevel() {
        return this.mLevel;
    }

    public void setEnchantType(Enchant.Type enchantType) {
        this.mEnchantType = enchantType;
    }

    public void setEnchantLevel(int level) {
        this.mLevel = level;
    }
}
