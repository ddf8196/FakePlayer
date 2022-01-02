package com.ddf.fakeplayer.item;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.actor.mob.Mob;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.block.BlockLegacy;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.util.NotImplemented;

public class ItemStack extends ItemStackBase implements Cloneable {
    public static final ItemStack EMPTY_ITEM = new ItemStack(BedrockItems.mAir, 0, 0, null);
    private ItemStackNetIdVariant mNetIdVariant = new ItemStackNetIdVariant();

    public ItemStack() {
        super();
    }

    public ItemStack(final int id) {
        super(id);
    }

    public ItemStack(final int id, int count) {
        super(id, count);
    }

    public ItemStack(final int id, int count, int auxValue) {
        super(id, count, auxValue);
    }

    public ItemStack(final int id, int count, int auxValue, final CompoundTag _userData) {
        super(id, count, auxValue, _userData);
    }

    public ItemStack(Item item) {
        super(item);
    }

    public ItemStack(final Item item, int count) {
        super(item, count);
    }

    public ItemStack(final Item item, int count, int auxValue) {
        super(item, count, auxValue);
    }

    public ItemStack(final Item item, int count, int auxValue, final CompoundTag _userData) {
        super(item, count, auxValue, _userData);
    }

    public ItemStack(ItemInstance rhs) {
        super(rhs);
    }

    public ItemStack(final ItemStack rhs) {
        super(rhs);
    }

    public ItemStack(final BlockLegacy block, int count) {
        super(block, count);
    }

    public final float getDestroySpeed(final Block block) {
        if (this.mItem != null) {
            return this.mItem.getDestroySpeed(new ItemInstance(this), block);
        } else {
            return 1.0f;
        }
    }

    public final ItemStackNetIdVariant getItemStackNetIdVariant() {
        return this.mNetIdVariant;
    }

    public int getMaxUseDuration() {
        if (!this.isItem())
            return 0;
        return this.mItem.getMaxUseDuration(this);
    }

    @Override
    public void setNull() {
        super.setNull();
        if (this.mNetIdVariant != null)
            this.mNetIdVariant.set(0, 0);
    }

    public final void clientInitNetId(int netId) {
        if (netId > 0/*TypedServerNetId<ItemStackNetIdTag,int,0>::isValid()*/)
            this.mNetIdVariant.set(netId, ItemStackNetIdVariant.SERVER_NET_ID);
        else
            this.mNetIdVariant.set(0, 0);
    }

    public void releaseUsing(Player player, int durationLeft) {
        if (this.isItem()) {
            this.mItem.releaseUsing(this, player, durationLeft);
        }
    }

    public final boolean inventoryTick(Level level, Actor owner, int slot, boolean selected) {
        if (this.mItem == null) {
            return false;
        }
        return this.mItem.inventoryTick(this, level, owner, slot, selected);
    }

    public final void mineBlock(final Block block, int x, int y, int z, Mob owner) {
        if (this.isItem()) {
            this.mItem.mineBlock(this, block, x, y, z, owner);
        }
    }

    public final ItemStack use(Player player) {
        if (this.mItem == null)
            return this;
        if (player.isItemInCooldown(this.mItem.getCooldownType()))
            return this;
        return this.mItem.use(this, player);
    }

    public final boolean useOn(Actor entity, int x, int y, int z, /*uint8_t FacingID*/int face, float clickX, float clickY, float clickZ) {
        if (super.isItem()) {
//            GameRules gameRules = entity.getLevel().getGameRules();
//            int ruleType = 29; //ALLOW_DESTRUCTIVE_OBJECTS
//            if (!gameRules.hasRule(ruleType) || gameRules.getBool(ruleType) || !this.mItem.isDestructive(super.getAuxValue())) {
                return this.mItem.useOn(this, entity, x, y, z, face, clickX, clickY, clickZ);
//            }
        }
        return false;
    }

    @NotImplemented
    public final void playSoundIncrementally(Mob mob) {
    }

    @Override
    public final ItemStack clone() {
        return new ItemStack(this);
    }
}
