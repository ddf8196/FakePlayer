package com.ddf.fakeplayer.item.items;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.block.BlockLegacy;
import com.ddf.fakeplayer.item.Item;
import com.ddf.fakeplayer.item.ItemInstance;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.item.VanillaItemTiers;

import java.util.ArrayList;
import java.util.TreeSet;

public class DiggerItem extends Item {
    private float mSpeed;
    private final Item.Tier mTier;
    private int mAttackDamage;
    private ArrayList<Block> mBlocks = new ArrayList<>();
    private TreeSet<BlockLegacy> m_bBlocks = new TreeSet<>();

    protected DiggerItem(final String name/*, int id*/, int attackDamage, final Item.Tier tier, final ArrayList<Block> blocks) {
        super(name);
        this.mSpeed = tier.getSpeed();
        this.mTier = tier;
        this.setBlocks(blocks);
        this.m_maxStackSize = 1;
        this.setMaxDamage(tier.getUses());
        this.mAttackDamage = tier.getAttackDamageBonus() + attackDamage;
    }

    @Override
    public int getAttackDamage() {
        return this.mAttackDamage;
    }

    @Override
    public float getDestroySpeed(final ItemInstance itemStack, final Block block) {
        if (!this.hasBlock(block))
            return 1.0f;
        return this.mSpeed + super.destroySpeedBonus(itemStack);
    }

    @Override
    public int getEnchantValue() {
        return this.mTier.getEnchantmentValue();
    }

    public boolean hasBlock(final Block block) {
        return this.m_bBlocks.contains(block.getLegacyBlock());
    }

    @Override
    public boolean isHandEquipped() {
        return true;
    }

    @Override
    public boolean isValidRepairItem(final ItemInstance source, final ItemInstance repairItem) {
        ItemInstance tierItem = new ItemInstance(VanillaItemTiers.getTierItem(source.getRegistry(), this.mTier));
        if (tierItem.isItem() == repairItem.isItem()) {
            if (tierItem.isItem() && tierItem.getItem() == repairItem.getItem()) {
                return true;
            } else {
                return tierItem.isBlock()
                        && repairItem.isBlock()
                        && tierItem.getLegacyBlock() == repairItem.getLegacyBlock()
                        || super.isValidRepairItem(source, repairItem);
            }
        } else {
            return false;
        }
    }

    public final void setBlocks(final ArrayList<Block> blocks) {
        for (Block block : blocks) {
            this.m_bBlocks.add(block.getLegacyBlock());
        }
    }

    @Override
    public boolean mineBlock(ItemStack itemInstance, final Block block, int x, int y, int z, Actor owner) {
        if (block.canHurtAndBreakItem())
            itemInstance.hurtAndBreak(1, owner);
        return true;
    }

}
