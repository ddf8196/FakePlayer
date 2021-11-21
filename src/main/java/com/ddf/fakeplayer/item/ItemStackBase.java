package com.ddf.fakeplayer.item;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.actor.ActorCategory;
import com.ddf.fakeplayer.actor.definition.ActorDefinitionIdentifier;
import com.ddf.fakeplayer.actor.player.AbilitiesIndex;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.block.BlockLegacy;
import com.ddf.fakeplayer.block.BlockSerializationUtils;
import com.ddf.fakeplayer.block.BlockTypeRegistry;
import com.ddf.fakeplayer.item.enchant.Enchant;
import com.ddf.fakeplayer.item.enchant.EnchantUtils;
import com.ddf.fakeplayer.item.enchant.ItemEnchants;
import com.ddf.fakeplayer.level.LevelSoundEvent;
import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.nbt.ListTag;
import com.ddf.fakeplayer.nbt.Tag;
import com.ddf.fakeplayer.util.MathUtil;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("all")
public class ItemStackBase {
    public static final String TAG_CHARGED_ITEM = "chargedItem";
    public static final String TAG_ENCHANTS = "ench";
    public static final String TAG_STORE_CAN_PLACE_ON = "CanPlaceOn";
    private static final String TAG_STORE_CAN_DESTROY = "CanDestroy";

    private ItemRegistry registry;
    protected Item mItem;
    private CompoundTag mUserData;
    private Block mBlock = null;
    private short mAuxValue = 0;
    private byte mCount = 0;
    private boolean mValid = true;
    private long mPickupTime = 0;
    private boolean mShowPickUp = true;
    private ArrayList<BlockLegacy> mCanPlaceOn = new ArrayList<>();
    private /*size_t*/long mCanPlaceOnHash = mCanPlaceOn.hashCode();
    private ArrayList<BlockLegacy> mCanDestroy = new ArrayList<>();
    private /*size_t*/long mCanDestroyHash = mCanDestroy.hashCode();
    private /*unsigned long Tick*/long mBlockingTick = 0;
    private ItemInstance mChargedItem = null;

    private boolean usingNetId;
    private int netId;

    protected ItemStackBase(ItemRegistry registry) {
        this.registry = registry;
        this.init(0, 0, 0);
    }


    protected ItemStackBase(ItemRegistry registry, final int id) {
        this.registry = registry;
        this.init(id, 1, 0);
    }

    protected ItemStackBase(ItemRegistry registry, final int id, int count) {
        this.registry = registry;
        this.init(id, count, 0);
    }

    protected ItemStackBase(ItemRegistry registry, final int id, int count, int auxValue) {
        this.registry = registry;
        this.init(id, count, auxValue);
    }

    protected ItemStackBase(ItemRegistry registry, final int id, int count, int auxValue, final CompoundTag _userData) {
        this.registry = registry;
        this.init(id, count, auxValue);
        if (_userData != null)
            this.mUserData = _userData.clone();
    }

    protected ItemStackBase(ItemRegistry registry, final Item item) {
        this.registry = registry;
        this.init(item, 1, 0, null);
    }

    protected ItemStackBase(ItemRegistry registry, final Item item, int count) {
        this.registry = registry;
        this.init(item, count, 0, null);
    }

    protected ItemStackBase(ItemRegistry registry, final Item item, int count, int auxValue) {
        this.registry = registry;
        this.init(item, count, auxValue, null);
    }

    protected ItemStackBase(ItemRegistry registry, final Item item, int count, int auxValue, final CompoundTag _userData) {
        this.registry = registry;
        this.init(item, count, auxValue, _userData);
    }

    @NotImplemented
    protected ItemStackBase(final ItemStackBase rhs) {
        this.registry = rhs.registry;
        this.mBlock = rhs.mBlock;
        if (this.mBlock != null && rhs.mAuxValue == 0x7FFF) {
            //this.init(this.mBlock.getLegacyBlock(), rhs.mCount);
            //this.mAuxValue = 0x7FFF;
        } else {
            this.init(rhs.getId(), rhs.mCount, rhs.mAuxValue);
        }
        if (rhs.mUserData != null) {
            this.mUserData = rhs.mUserData.clone();
        }
        this._makeChargedItemFromUserData();
        this._cloneComponents(rhs);
    }

//    protected ItemStackBase(final RecipeIngredient ingredient) {
//        this.mBlock = ingredient.getBlock();
//        int id = ingredient.getItem().getId();
//        int count_ = ingredient.getStackSize();
//        this.init(id, count_, ingredient.getAuxValue());
//    }

    protected ItemStackBase(final Block block, int count, final CompoundTag _userData) {
        this.mBlock = block;
        this.init(block.getLegacyBlock(), count);
        if ( _userData != null)
            this.mUserData = _userData.clone();
    }

    protected ItemStackBase(final BlockLegacy block, int count) {
        this.mBlock = block.getDefaultState();
        this.init(block, count);
    }

//    protected ItemStackBase(final BlockLegacy block, int count, short auxValue) {
//        if ( auxValue == 0x7FFF ) {
//            this.mBlock = block.getDefaultState();
//            this.init(block, count);
//            this.mAuxValue = 0x7FFF;
//        } else {
//            this.mBlock = block.tryGetStateFromLegacyData(auxValue);
//            if (this.mBlock != null)
//                this.init(block, count);
//        }
//    }

    final boolean _setItem(int id) {
        if (registry != null) {
            this.mItem = registry.getItem(id);
        }
        if (this.mItem != null) {
            id = this.mItem.getId(this.registry);
        }
        if (this.mItem == null)
            this.mValid = id == 0;
        else
            this.mValid = true;
        if (!this.mValid) {
            this.mItem = null;
            this.mCount = 0;
            this.mAuxValue = 0;
        }
        return this.mValid;
    }

    private void _makeChargedItemFromUserData() {
        CompoundTag chargedItem;
        if (this.mUserData != null && (chargedItem = this.mUserData.getCompound(ItemStackBase.TAG_CHARGED_ITEM)) != null ) {
            this.mChargedItem = ItemInstance.fromTag(registry, chargedItem);
        } else if (this.mChargedItem != null) {
            this.mChargedItem = null;
        }
    }

    private void _initComponents() {
        this.mCanPlaceOn.clear();
        this.mCanDestroy.clear();
        this.mBlockingTick = 0;
        this._updateCompareHashes();
    }

    private void _cloneComponents(final ItemStackBase other) {
        this.mCanPlaceOn = new ArrayList<>(other.mCanPlaceOn);
        this.mCanDestroy = new ArrayList<>(other.mCanDestroy);
        this.mBlockingTick = other.mBlockingTick;
        this._updateCompareHashes();
    }

    private void _loadComponents(final CompoundTag compoundTag) {
        if (compoundTag.contains(ItemStackBase.TAG_STORE_CAN_PLACE_ON, Tag.Type.List_0) ) {
            _loadComponents_$_0(compoundTag.getList(ItemStackBase.TAG_STORE_CAN_PLACE_ON), this.mCanPlaceOn);
        }
        if (compoundTag.contains(ItemStackBase.TAG_STORE_CAN_DESTROY, Tag.Type.List_0) ) {
            _loadComponents_$_0(compoundTag.getList(ItemStackBase.TAG_STORE_CAN_DESTROY), this.mCanDestroy);
        }
        this._updateCompareHashes();
    }

    private static void _loadComponents_$_0(final ListTag listTag, ArrayList<BlockLegacy> blockList) {
        if (listTag != null) {
            for (int i = 0; i < listTag.size(); ++i) {
                String blockName = listTag.getString(i);
                BlockLegacy blockLegacy = BlockTypeRegistry.lookupByName(blockName);
                if (blockLegacy != null)
                    blockList.add(blockLegacy);
            }
        }
    }

    private void _updateCompareHashes() {
        this.mCanDestroyHash = this.mCanDestroy.hashCode();
        this.mCanPlaceOnHash = this.mCanPlaceOn.hashCode();
    }

    private void _loadItem(final CompoundTag compoundTag) {
        IdPair idpair = new IdPair((short) 0, compoundTag.getShort("Damage"));
        if (compoundTag.contains("Name")) {
            Item item = ItemRegistry.lookupByName(compoundTag.getString("Name"));
            if (item != null)
                idpair.id = item.getId(registry);
        } else {
            idpair.id = compoundTag.getShort("id");
            IdPair.PromoteItemIdPair(idpair);
        }
        this._setItem(idpair.id);
        this.mAuxValue = (short) Math.max(0, idpair.aux);
        this.mCount = compoundTag.getByte("Count");
        if (compoundTag.contains("tag", Tag.Type.Compound)) {
            this.mUserData = compoundTag.getCompound("tag").clone();
        } else {
            this.mUserData = null;
        }

        this._makeChargedItemFromUserData();
        this._loadComponents(compoundTag);
        if (this.mItem != null) {
            CompoundTag block = compoundTag.getCompound("Block");
            if (block != null) {
                this.mBlock = BlockSerializationUtils.tryGetBlockFromNBT(block, null);
            } else {
                BlockLegacy blockType = this.getLegacyBlock();
                if (blockType != null) {
                    //this.mBlock = blockType.getStateFromLegacyData(this.mAuxValue);
                } else {
                    this.mBlock = null;
                }
            }
            this.mItem.readAdditionalData(this, compoundTag);
        }
    }

    private void init(int id, int count_, int aux_) {
        this.mCount = (byte) Math.max(count_, 0);
        if (this.mBlock == null)
            this.setAuxValue((short) aux_);
        if (this.registry != null && this.registry.isInitialized()) {
            this._setItem(id);
        } else if (this.registry != null) {
            this.registry.addNonInitializedItemStack(this, id);
        } else {
            this._setItem(id);
        }
        this.mPickupTime = System.nanoTime();
        if (this.mItem != null) {
            this.mItem.getLegacyBlock();
        }
        if (this.isEmptyStack())
            this.setNull();
    }

    @NotImplemented
    private void init(final Item item, int count, int auxValue, final CompoundTag userData) {
        BlockLegacy blockType = item.getLegacyBlock();
        if (blockType != null) {
//            if (item.getId(registry) >= 256) {
//                this.mBlock = blockType.tryGetStateFromLegacyData(auxValue);
//                this.init(item.getId(registry), count, auxValue);
//            } else {
//                if ( auxValue == 0x7FFF ) {
//                    this.mBlock = blockType.getDefaultState();
//                    this.init(blockType, count);
//                    this.mAuxValue = 0x7FFF;
//                } else {
//                    this.mBlock = blockType.tryGetStateFromLegacyData(auxValue);
//                    this.init(blockType, count);
//                }
//            }
        } else {
            this.init(item.getId(registry), count, auxValue);
        }
        if (userData != null)
            this.mUserData = userData.clone();
    }

    @NotImplemented
    private void init(final BlockLegacy block, int count) {
        //this.init(block.getBlockItemId(), count, 0);
    }

    public void clearChargedItem() {
        if (this.mUserData != null) {
            this.mUserData.remove(ItemStackBase.TAG_CHARGED_ITEM);
        }
        this.mChargedItem = null;
    }

    public final ItemRegistry getRegistry() {
        return this.registry;
    }

    public final short getId() {
        if (!this.mValid)
            return -1;
        if (this.mItem == null)
            return 0;
        return this.mItem.getId(registry);
    }

    public final short getAuxValue() {
        if (this.mBlock == null || this.mAuxValue == 0x7FFF)
            return this.mAuxValue;
        else
            return 0;
            //return this.mBlock.getDataDEPRECATED();
    }

    public final int getIdAux() {
        short damageValue;
        if (this.isDamageableItem() )
            damageValue = this.getDamageValue();
        else
            damageValue = this.getAuxValue();
        if (this.mItem == null)
            return 0;
        return damageValue | (this.mItem.getId(registry) << 16);
    }

    public short getMaxDamage() {
        if (!this.isItem())
            return 0;
        return this.mItem.getMaxDamage();
    }

    public short getDamageValue() {
        if (!isItem())
            return 0;
        return this.getItem().getDamageValue(this.getUserData());
    }

    public final Item getItem() {
        return this.mItem;
    }

    public final Block getBlock() {
        return this.mBlock;
    }

    public final BlockLegacy getLegacyBlock() {
        if (this.mItem == null)
            return null;
        return this.mItem.getLegacyBlock();
    }

    public CompoundTag getUserData() {
        return this.mUserData;
    }

    public final long getBlockingTick() {
        return this.mBlockingTick;
    }

    public ItemInstance getChargedItem() {
        if (this.mChargedItem != null)
            return this.mChargedItem;
        else
            return ItemInstance.EMPTY_ITEM;
    }

    @NotImplemented
    public final ItemDescriptor getDescriptor() {
        if (this.mBlock != null) {
//            if (this.mAuxValue == 0x7FFF) {
//                return new ItemDescriptor(this.mBlock.getLegacyBlock());
//            } else {
//                return new ItemDescriptor(this.mBlock);
//            }
        } else if (this.mItem != null) {
//            if (this.mItem.getMaxDamage() > 0)
//                return new ItemDescriptor(this.mItem, this.getDamageValue());
//            else
//                return new ItemDescriptor(this.mItem, this.mAuxValue);
        } else {
            return new ItemDescriptor();
        }
        return null;
    }

    public ItemEnchants getEnchantsFromUserData() {
        if (this.isEnchanted() ) {
            return new ItemEnchants(this.getEnchantSlot(), this.mUserData.getList(ItemStackBase.TAG_ENCHANTS));
        } else {
            return new ItemEnchants(this.getEnchantSlot());
        }
    }

    public int getEnchantSlot() {
        if (this.mItem == null)
            return 0;
        return this.mItem.getEnchantSlot();
    }

    public byte getMaxStackSize() {
        if (this.mItem != null) {
            return this.mItem.getMaxStackSize(this.getDescriptor());
        } else {
            return -1;
        }
    }

    public byte getStackSize() {
        return this.mCount;
    }

    public final UseAnimation getUseAnimation() {
        if (!this.isItem())
            return UseAnimation.None;
        return this.mItem.getUseAnimation();
    }

    public final boolean canDestroy(final Block block) {
        for (BlockLegacy blockLegacy : this.mCanDestroy) {
            if (blockLegacy.equals(block.getLegacyBlock())) {
                return true;
            }
        }
        return false;
    }

    public boolean canDestroySpecial(final Block block) {
        if (this.isItem()) {
            return this.mItem.canDestroySpecial(block);
        }
        return false;
    }

    public final boolean canPlaceOn(final Block block) {
        for (BlockLegacy blockLegacy : this.mCanPlaceOn) {
            if (blockLegacy.equals(block.getLegacyBlock())) {
                return true;
            }
        }
        return false;
    }

    public final boolean hasFeedingAnimation() {
        UseAnimation anim = this.getUseAnimation();
        if (anim == UseAnimation.Eat || anim == UseAnimation.Drink || anim == UseAnimation.GlowStick || anim == UseAnimation.Sparkler) {
            return true;
        }
        return false;
    }

    public boolean hasChargedItem() {
        if (this.mChargedItem == null)
            return false;
        return !this.mChargedItem.isNull();
    }

    public boolean _hasComponents() {
        return !this.mCanDestroy.isEmpty() || !this.mCanPlaceOn.isEmpty();
    }

    @NotImplemented
    public final boolean hasSameAuxValue(final ItemStackBase other) {
        if (this.isNull() || other.isNull() )
            return false;
        if (this.getLegacyBlock() != null &&  other.getLegacyBlock() != null) {
//            if (this.getLegacyBlock().isAuxValueRelevantForPicking() && other.getLegacyBlock().isAuxValueRelevantForPicking()) {
//                return this.getAuxValue() == other.getAuxValue();
//            } else {
//                return true;
//            }
            return false;
        } else if (this.isDamageableItem()) {
            return this.getDamageValue() == other.getDamageValue();
        } else {
            return this.getAuxValue() == other.getAuxValue();
        }
    }

    public boolean hasUserData()
    {
        return this.mUserData != null;
    }

    public final boolean hasSameUserData(final ItemStackBase other) {
        if (this.isNull() && other.isNull())
            return true;
        if (this.isNull() || other.isNull())
            return false;
        return this.hasSameUserData(other.getUserData());
    }

    public final boolean hasSameUserData(CompoundTag otherData) {
        if ((this.mUserData == null && otherData != null) || (this.mUserData != null && otherData == null)) {
            return false;
        }
        if (this.mUserData != null) {
            return this.mUserData.equals(otherData);
        }
        return true;
    }

    public final boolean isEmptyStack() {
        return this.mCount == 0;
    }

    public final boolean isItem() {
        return this.mItem != null;
    }

    public final boolean isBlock() {
        if (this.mItem != null) {
            return this.mItem.getLegacyBlock() != null;
        }
        return false;
    }

    public final boolean isDamageableItem() {
        return this.isItem() && this.mItem.getMaxDamage() > 0;
    }

    public final boolean isDamaged() {
        if (this.isDamageableItem())
            return this.getDamageValue() > 0;
        return false;
    }

    public boolean isEnchanted() {
        if (this.mUserData != null) {
            return this.mUserData.contains(ItemStackBase.TAG_ENCHANTS, Tag.Type.List_0);
        }
        return false;
    }

    public final boolean isNull() {
        if (!this.mValid || this.mItem == BedrockItems.mAir) {
            return true;
        }
        return this.mCount == 0
                && this.mBlock == null
                && this.mAuxValue == 0
                && this.mItem == null
                && this.mUserData == null
                && !this._hasComponents();
    }

    public final boolean isValid() {
        return this.mValid;
    }

    public boolean isStackedByData() {
        if (this.isItem()) {
            return this.mItem.isStackedByData();
        }
        return false;
    }

    public final boolean isStackable(final ItemStackBase other) {
        if (this.mItem == other.mItem) {
            if (other.isStackable()) {
                if (!other.isStackedByData() || this.getAuxValue() == other.getAuxValue()) {
                    if (this.hasSameUserData(other) )
                        return this.componentsMatch(other);
                }
            }
        }
        return false;
    }

    public final boolean isStackable() {
        return this.getMaxStackSize() > 1 && (!this.isDamageableItem() || !this.isDamaged());
    }

    public final boolean isInstance(final Item i) {
        return i == this.mItem;
    }

    public final boolean isLiquidClipItem() {
        if (this.isItem()) {
            return this.mItem.isLiquidClipItem(this.getAuxValue());
        }
        return false;
    }

    public void setAuxValue(short value) {
        this.mAuxValue = (short) Math.max(0, value);
    }

    public void setUserData(CompoundTag tag) {
        this.mUserData = tag;
    }

    public void setNull() {
        this.clearChargedItem();
        this.mCount = 0;
        this.mBlock = null;
        this.mAuxValue = 0;
        this.mItem = null;
        this.mUserData = null;
        this.mChargedItem = null;
        this.mShowPickUp = true;
        this.mPickupTime = 0;
        this._initComponents();
    }

    public void set(final int inCount) {
        this.mCount = (byte) MathUtil.clamp(inCount, 0, this.getMaxStackSize());
        if (!this.toBoolean())
            this.setNull();
    }

    public ItemStackBase set(final ItemStackBase rhs) {
        this.mCount = rhs.mCount;
        this.mAuxValue = rhs.mAuxValue;
        this.mItem = rhs.mItem;
        this.mBlock = rhs.mBlock;
        this.mValid = rhs.mValid;
        this.mPickupTime = rhs.mPickupTime;
        if (rhs.mUserData != null) {
            this.mUserData = rhs.mUserData.clone();
        } else {
            this.mUserData = null;
        }
        this._makeChargedItemFromUserData();
        this._cloneComponents(rhs);
        return this;
    }

    public final void setStackSize(final byte inCount) {
        this.set(inCount);
    }

    public void setDamageValue(short newDamage) {
        if (this.isItem()) {
            this.getItem().setDamageValue(this, newDamage);
        }
    }

    public final void setBlockingTick(long blockingTick) {
        this.mBlockingTick = blockingTick;
    }

    public boolean isUsingNetId() {
        return usingNetId;
    }

    public void setUsingNetId(boolean usingNetId) {
        this.usingNetId = usingNetId;
    }

    public int getNetId() {
        return netId;
    }

    public void setNetId(int netId) {
        this.netId = netId;
    }

    public void load(final CompoundTag compoundTag) {
        this._loadItem(compoundTag);
        if (this.mItem != null) {
            this.mItem.fixupOnLoad(this);
        }
    }

    public void remove(int inCount) {
        this.set(this.mCount - inCount);
    }

    public void startCoolDown(Player player) {
        if (this.isItem() && player != null) {
            player.startCooldown(this.mItem);
        }
    }

    public boolean matches(final ItemStackBase other) {
        return this.matchesItem(other) && this.mCount == other.mCount;
    }

    public boolean matchesItem(final ItemStackBase other) {
        return this.mItem == other.mItem
                && this.mAuxValue == other.mAuxValue
                && (this.mBlock == null || this.mBlock == other.mBlock)
                && this.hasSameUserData(other)
                && this.componentsMatch(other)
                && this.matchesChargedItem(other);
    }

    public final boolean componentsMatch(final ItemStackBase other) {
        return this.mCanDestroyHash == other.mCanDestroyHash
                && this.mCanPlaceOnHash == other.mCanPlaceOnHash
                && this.mBlockingTick == other.mBlockingTick;
    }

    public boolean matchesChargedItem(final ItemStackBase other) {
        boolean result = false;
        boolean lhsHasChargedItem = this.hasChargedItem();
        boolean rhsHasChargedItem = other.hasChargedItem();
        if (lhsHasChargedItem && rhsHasChargedItem) {
            result = this.getChargedItem().matches(other.getChargedItem());
        } else if (!lhsHasChargedItem && !rhsHasChargedItem) {
            result = true;
        }
        return result;
    }

    public final void hurtAndBreak(int deltaDamage, Actor owner) {
        if (this.isDamageableItem() && (owner == null || !owner.hasCategory(ActorCategory.Player_1) || ((Player) owner).mAbilities.getBool(AbilitiesIndex.Instabuild))) {
            if (this.mUserData == null || !this.mUserData.getBoolean("Unbreakable")) {
                int unbreaking = EnchantUtils.getEnchantLevel(Enchant.Type.MiningDurability, this);
                int chance = this.mItem.getDamageChance(unbreaking);
                if (unbreaking <= 0 || ThreadLocalRandom.current().nextInt(100) < chance ){
                    short damageAmount = this.getDamageValue();
                    this.setDamageValue((short) (deltaDamage + damageAmount));
                    if (deltaDamage + damageAmount > this.getMaxDamage()) {
                        this.remove(1);
                        if (owner != null)
                            owner.getLevel().playSound(LevelSoundEvent.Break, owner.getStateVectorComponent().getPos(), -1, new ActorDefinitionIdentifier(), false, false);
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemStackBase that = (ItemStackBase) o;
        return this.matches(that);
    }

    public final boolean toBoolean() {
        return this.isValid()
                && this.isItem()
                && !this.isNull()
                && !this.isEmptyStack();
    }
}
