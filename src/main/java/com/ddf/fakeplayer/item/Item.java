package com.ddf.fakeplayer.item;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.block.BlockLegacy;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.item.component.CameraItemComponent;
import com.ddf.fakeplayer.item.component.FoodItemComponent;
import com.ddf.fakeplayer.item.component.SeedItemComponent;
import com.ddf.fakeplayer.item.enchant.Enchant;
import com.ddf.fakeplayer.item.enchant.EnchantUtils;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.util.JsonUtil;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.Vec3;
import com.ddf.fakeplayer.util.tuple.Tuple4;

import java.util.ArrayList;
import java.util.function.Function;

@SuppressWarnings("all")
public class Item {
    public static final String ICON_DESCRIPTION_PREFIX = "item.";
    public static final String TAG_DAMAGE = "Damage";
    public static boolean mAllowExperimental;
    public static Object mCreativeGroupInfo;
    public static Object mCreativeGroups;
    public static Object mCreativeList;
    public static Object mCreativeListMutex;
    public static Object mCreativeListStack;
    public static Object mGenerateDenyParticleEffect;
    public static Object mInCreativeGroup;
    public static Object mInvalidTextureUVCoordinateSet;
    public static Object mItemTextureItems;
    public static Object mWorldBaseGameVersion;

    protected byte m_maxStackSize = 64;
    private String m_textureAtlasFile = "atlas.items";
    private int m_frameCount = 1;
    private boolean m_animatesInToolbar = false;
    private boolean mIsMirroredArt = false;
    protected UseAnimation mUseAnim = UseAnimation.None;
    String mHoverTextColorFormat = null;
    //TextureUVCoordinateSet mIconTexture;
    //TextureAtlasItem mIconAtlas;
    private boolean mUsesRenderingAdjustment = false;
    private Vec3 mRenderingAdjTrans = new Vec3(0.0f);
    private Vec3 mRenderingAdjRot = new Vec3(0.0f);
    private float mRenderingAdjScale = 1.0f;
    //private short mId;
    private String mDescriptionId;
    private String mRawNameId;
    private String mNamespace = "minecraft";
    private String mFullName;
    protected short mMaxDamage = 0;
    private boolean mIsGlint = true;
    private boolean mHandEquipped = true;
    private boolean mIsStackedByData = true;
    private boolean mRequiresWorldBuilder = true;
    private boolean mExplodable = true;
    private boolean mShouldDespawn = true;
    private boolean mAllowOffhand = true;
    private boolean mIgnoresPermissions = true;
    private boolean mExperimental = true;
    private int mMaxUseDuration = 0;
    //private BaseGameVersion mMinRequiredBaseGameVersion = new BaseGameVersion();
    private BlockLegacy mLegacyBlock;
    private CreativeItemCategory mCreativeCategory = CreativeItemCategory.Items;
    private Item mCraftingRemainingItem;
    private FoodItemComponent mFoodComponent;
    private SeedItemComponent mSeedComponent;
    private CameraItemComponent mCameraComponent;
    private ArrayList<Function<Void, Void>> mOnResetBAIcallbacks = new ArrayList<>();

    public Item(String nameId/*, short id*/) {
        //this.mId = id;
        this.mDescriptionId = Item.ICON_DESCRIPTION_PREFIX + nameId;
        this.mRawNameId = nameId;
        int auxValue = 0;
        Tuple4<Boolean, String, String, Integer> result = JsonUtil.parseItem(this.mRawNameId, this.mNamespace, auxValue, nameId);
        this.mRawNameId = result.getT2();
        this.mNamespace = result.getT3();
        auxValue = result.getT4();
        this.mFullName = getNameSpace() + ":" + mRawNameId;
    }

    public boolean _calculatePlacePos(ItemInstance instance, Actor entity, /*uint8_t FacingID*/int face, BlockPos pos) {
        return true;
    }

    public boolean _calculatePlacePos(ItemStack instance, Actor entity, /*uint8_t FacingID*/int face, BlockPos pos) {
        return true;
    }

    @NotImplemented
    public boolean _checkUseOnPermissions(Actor entity, ItemInstance item, final /*uint8_t FacingID*/int face, final BlockPos pos) {
        //BlockSource blockSource = entity.getRegion();
        //return blockSource.checkBlockPermissions(entity, pos, face, new ItemStack(item), false);
        return false;
    }

    @NotImplemented
    public boolean _checkUseOnPermissions(Actor entity, ItemStack item, final /*uint8_t FacingID*/int face, final BlockPos pos) {
        //BlockSource blockSource = entity.getRegion();
        //return blockSource.checkBlockPermissions(entity, pos, face, item, false);
        return false;
    }

    public BlockLegacy getLegacyBlock() {
        return this.mLegacyBlock;
    }

    public short getId(ItemRegistry registry) {
        //return this.mId;
        return (short) registry.getId(this);
    }

    public final String getFullItemName() {
        return this.mFullName;
    }

    public final String getNameSpace() {
        return mNamespace;
    }

    public final String getRawNameId() {
        return this.mRawNameId;
    }

    public int getAttackDamage() {
        return 0;
    }

    public short getMaxDamage() {
        return this.mMaxDamage;
    }

    public short getDamageValue(final CompoundTag userData) {
        if (userData == null || !userData.contains(Item.TAG_DAMAGE))
            return 0;
        return (short) userData.getInt(Item.TAG_DAMAGE);
    }

    public int getDamageChance(int unbreaking) {
        return 100 / (unbreaking + 1);
    }

    public float getDestroySpeed(final ItemInstance itemStack, final Block block) {
        return 1.0f;
    }

    public int getMaxUseDuration(final ItemInstance instance) {
        return this.mMaxUseDuration;
    }

    public int getMaxUseDuration(final ItemStack instance) {
        return this.mMaxUseDuration;
    }

    public int getEnchantSlot() {
        return 0;
    }

    public int getEnchantValue() {
        return 0;
    }

    @NotImplemented
    public CooldownType getCooldownType() {
        if (this.mFoodComponent == null)
            return CooldownType.TypeNone;
        return this.mFoodComponent.getCoolDownType();
    }

    public int getCooldownTime() {
        if (this.mFoodComponent == null)
            return 0;
        return this.mFoodComponent.getCoolDownTime();
    }

    public byte getMaxStackSize(final ItemDescriptor item) {
        return this.m_maxStackSize;
    }

    public final UseAnimation getUseAnimation() {
        return this.mUseAnim;
    }

    public boolean canDestroyInCreative() {
        return true;
    }

    public boolean canDestroySpecial(final Block block) {
        return false;
    }

    public boolean isDye() {
        return false;
    }

    public boolean isStackedByData() {
        return this.mIsStackedByData;
    }

    public boolean isHandEquipped() {
        return this.mHandEquipped;
    }

    public boolean isValidRepairItem(final ItemInstance source, final ItemInstance repairItem) {
        return false;
    }

    public Item setMaxStackSize(byte max) {
        this.m_maxStackSize = max;
        return this;
    }

    public Item setMaxDamage(int maxDamage) {
        this.mMaxDamage = (short) maxDamage;
        return this;
    }

    public void setDamageValue(ItemStackBase stack, short newDamage) {
        CompoundTag userData;
        if (stack.hasUserData()) {
            userData = stack.getUserData().clone();
        } else {
            userData = new CompoundTag();
        }
        userData.putInt(Item.TAG_DAMAGE, newDamage);
        stack.setUserData(userData);
    }

    @NotImplemented
    public Item setIcon(final String name, int id) {
        return null;
    }

    public Item setMaxUseDuration(int maxUseDuration) {
        this.mMaxUseDuration = maxUseDuration;
        return this;
    }

//    public String appendFormattedHovertext(final ItemStackBase stack, Level level, String hovertext, final boolean showCategory) {
//        boolean enchanted = stack.isEnchanted();
//        String format;
//        if (this.mHoverTextColorFormat != null) {
//            format = this.mHoverTextColorFormat);
//        } else if (stack.isEnchantingBook()) {
//            format = ColorFormat.YELLOW);
//        } else if (enchanted || stack.isMusicDiscItem()) {
//            format = ColorFormat.AQUA;
//        } else {
//            format = ColorFormat.WHITE;
//        }
//        hovertext += format + stack.getHoverName();
//        if (showCategory) {
//            String categoryName = stack.getCategoryName();
//            if (categoryName != null && !categoryName.isEmpty()) {
//                hovertext += "\n" + ColorFormat.BLUE + categoryName + ColorFormat.RESET);
//            }
//        }
//        if (enchanted) {
//            ArrayList<String> enchantingText = stack.getEnchantsFromUserData().getEnchantNames();
//            hovertext += ColorFormat.GRAY;
//            for (String str : enchantingText) {
//                hovertext += "\n" + str);
//            }
//            hovertext += ColorFormat.RESET);
//        }
//        return hovertext;
//    }

    @NotImplemented
    public ItemStack use(ItemStack itemStack, Player player) {
        boolean isPlantable = this.mSeedComponent != null;
        if (this.mFoodComponent != null){
            //if (!isPlantable || !this.mSeedComponent.isPlanting()) {
            //    this.mFoodComponent.use(itemStack, player);
            //}
        }
        if (this.mCameraComponent != null) {
            //this.mCameraComponent.use(itemStack, player);
        }
        Actor playerRider = player.getRide();
        if (playerRider != null) {
            //BoostableComponent boostable = playerRider.tryGetComponent<BoostableComponent>(BoostableComponent.class);
            //if (boostable != null)
            //    boostable.onItemInteract(playerRider, itemStack, player);
        }
        itemStack.startCoolDown(player);
        return itemStack;
    }

    @NotImplemented
    public void releaseUsing(ItemInstance itemInstance, Player player, int durationLeft) {
        if (this.mCameraComponent != null) {
            ItemStack itemStack = new ItemStack(itemInstance);
            //this.mCameraComponent.releaseUsing(itemStack, player, durationLeft);
            //itemInstance.set(new ItemInstance(itemStack));
        }
    }

    @NotImplemented
    public void releaseUsing(ItemStack itemStack, Player player, int durationLeft) {
        if (this.mCameraComponent != null){
            //this.mCameraComponent.releaseUsing(itemStack, player, durationLeft);
        }
    }

    public boolean mineBlock(ItemStack instance, final Block block, int x, int y, int z, Actor owner) {
        if (block.canHurtAndBreakItem())
            instance.hurtAndBreak(2, owner);
        return true;
    }

    public final float destroySpeedBonus(final ItemInstance inst) {
        if (!inst.toBoolean())
            return 1.0f;
        int efficiency = EnchantUtils.getEnchantLevel(Enchant.Type.MiningEfficiency, inst);
        if (efficiency > 0)
            return (efficiency * efficiency) + 1.0f;
        else
            return 0.0f;
    }

    public void fixupOnLoad(ItemStackBase stack) {
        if (this.getMaxDamage() > 0 ) {
            if (!stack.hasUserData() || !stack.getUserData().contains(Item.TAG_DAMAGE)) {
                short damage = stack.getAuxValue();
                this.setDamageValue(stack, damage);
                stack.setAuxValue((short) 0);
            }
        }
    }

    public void readAdditionalData(ItemStackBase stack, final CompoundTag tag) {
    }

    public boolean inventoryTick(ItemStack itemStack, Level level, Actor owner, int slot, boolean selected) {
        return false;
    }

    public static class Tier {
        private final int mLevel;
        private final int mUses;
        private final float mSpeed;
        private final int mDamage;
        private final int mEnchantmentValue;

        public Tier(int level, int uses, float speed, int damage, int enchant) {
            this.mLevel = level;
            this.mUses = uses;
            this.mSpeed = speed;
            this.mDamage = damage;
            this.mEnchantmentValue = enchant;
        }

        public final int getAttackDamageBonus() {
            return this.mDamage;
        }

        public final int getEnchantmentValue() {
            return this.mEnchantmentValue;
        }

        public final int getLevel() {
            return this.mLevel;
        }

        public final float getSpeed() {
            return this.mSpeed;
        }

        public final int getUses() {
            return this.mUses;
        }
    }
}
