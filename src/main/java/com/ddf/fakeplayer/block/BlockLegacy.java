package com.ddf.fakeplayer.block;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.actor.actors.ItemActor;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.block.blocktypes.UnknownBlock;
import com.ddf.fakeplayer.blockactor.BlockActorType;
import com.ddf.fakeplayer.item.*;
import com.ddf.fakeplayer.item.enchant.Enchant;
import com.ddf.fakeplayer.item.enchant.EnchantUtils;
import com.ddf.fakeplayer.state.ItemState;
import com.ddf.fakeplayer.util.AABB;
import com.ddf.fakeplayer.util.BaseGameVersion;
import com.ddf.fakeplayer.util.Brightness;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.mc.Color;
import com.ddf.fakeplayer.util.tuple.Tuple2;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

@SuppressWarnings("all")
public class BlockLegacy {
    public static final String BLOCK_DESCRIPTION_PREFIX = "tile.";
    public static final float SIZE_OFFSET = 0.000099999997f;

    private String mDescriptionId;
    private String mRawNameId = "";
    private String mNamespace = "";
    private String mFullName = "";
    private boolean mFancy = false;
    protected BlockRenderLayer mRenderLayer = BlockRenderLayer.RENDERLAYER_OPAQUE;
    private boolean mRenderLayerCanRenderAsOpaque = false;
    protected long mProperties = BlockProperty.CubeShaped.getValue();
    private BlockActorType mBlockEntityType = BlockActorType.Undefined_6;
    private boolean mAnimatedTexture = false;
    private float mBrightnessGamma = 1.0f;
    protected float mThickness = 0.0f;
    protected boolean mCanSlide = false;
    protected boolean mCanInstatick = false;
    private boolean mIsInteraction = false;
    private float mGravity = 1.0f;
    protected final Material mMaterial;
    private Color mMapColor;
    protected float mFriction = 0.60000002f;
    private boolean mHeavy = false;
    private float mParticleQuantityScalar = 1.0f;
    private float mDestroySpeed = 0.0f;
    protected float mExplosionResistance = 0.0f;
    private CreativeItemCategory mCreativeCategory = CreativeItemCategory.All_3;
    private boolean mAllowsRunes = false;
    private boolean mCanBeBrokenFromFalling = true;
    private boolean mSolid = false;
    private boolean mPushesOutItems = true;
    private boolean mIgnoreBlockForInsideCubeRenderer = false;
    private boolean mIsTrapdoor = false;
    private boolean mIsDoor = false;
    protected float mTranslucency = 0.0f;
    private /*uchar Brightness*/int mLightBlock = 0;
    private /*uchar Brightness*/int mLightEmission = 0;
    private boolean mShouldRandomTick = false;
    private boolean mShouldRandomTickExtraLayer = false;
    private int mFlameOdds = 0;
    private int mBurnOdds = 0;
    private boolean mIsMobPiece = false;
    private boolean mCanBeExtraBlock = true;
    private boolean mCanPropagateBrightness = false;
    private /*unsigned short NewBlockID*/int mID;
    private BaseGameVersion mMinRequiredBaseGameVersion = new BaseGameVersion();
    private boolean mExperimental = false;
    private boolean mIsVanilla = true;
    //private LootComponent mLootComponent = new LootComponent();
    private AABB mVisualShape = new AABB();
    private /*unsigned int*/long mBitsUsed = 0;
    private /*unsigned int*/long mTotalBitsUsed = 0;
    public ItemStateInstance[] mStates = new ItemStateInstance[105];
    public ArrayList<Block> mBlockPermutations = new ArrayList<>();
    private Block mDefaultState = null;
    private ReentrantLock mLegacyDataLookupTableMutex = new ReentrantLock();
    private ArrayList<Long> mLegacyDataLookupTable = new ArrayList<>();

    public BlockLegacy(final String nameId, int id, final Material material) {
        if (nameId == null || nameId.isEmpty()) {
            this.mDescriptionId = "";
        } else {
            this.mDescriptionId = BlockLegacy.BLOCK_DESCRIPTION_PREFIX + nameId;
        }
        this.mMaterial = material;
        this.mMapColor = material.getColor();
        this.mID = id;
        this.mTranslucency = material.getTranslucency();
        this.setSolid(true);
        Tuple2<String, String> result = _extractBlockIdentifier(nameId, this.mNamespace, this.mRawNameId);
        this.mNamespace = result.getT1();
        this.mRawNameId = result.getT2();
        this.mFullName = this.getNamespace() + ":" + this.getRawNameId();
    }

    public static Tuple2<String, String> _extractBlockIdentifier(final String rawName, String nspace, String name) {
        int namespacePos = rawName.indexOf(":", 0);
        if (namespacePos < 0) {
            nspace = "minecraft";
            name = rawName;
        } else {
            nspace = rawName.substring(0, namespacePos);
            name = rawName.substring(namespacePos + 1);
        }
        return new Tuple2<>(nspace, name);
    }

    public BlockLegacy init() {
        if (this.mTranslucency < 0.0f)
            this.mTranslucency = this.mMaterial.getTranslucency();
        return this;
    }

    public boolean canBeSilkTouched() {
        return true;
    }

    public boolean canContainLiquid() {
        return !this.hasProperty(BlockProperty.CubeShaped);
    }

    public boolean canHurtAndBreakItem() {
        return this.mDestroySpeed > 0.0f;
    }

    @NotImplemented
    public AABB getAABB(BlockSource a2, final BlockPos pos, final Block block, AABB bufferValue, boolean isClipping) {
        return null;
    }

    public final Block getDefaultState() {
        return this.mDefaultState;
    }

    public final float getDestroySpeed() {
        return this.mDestroySpeed;
    }

    public final String getFullName() {
        return this.mFullName;
    }

    public boolean getIgnoresDestroyPermissions(Actor entity, final BlockPos pos) {
        return false;
    }

    public final Material getMaterial() {
        return this.mMaterial;
    }

    public String getNamespace() {
        return this.mNamespace;
    }

    public String getRawNameId() {
        return this.mRawNameId;
    }

    public BlockRenderLayer getRenderLayer() {
        return this.mRenderLayer;
    }

    public int getResourceCount(Random random, final Block block, int bonusLootLevel) {
        return 1;
    }

    public ItemInstance getSilkTouchItemInstance(final Block block) {
        return new ItemInstance(block, 1, null);
    }

    public int getState(final ItemState stateType, short data) {
        ItemStateInstance blockState = this.mStates[stateType.getID()];
        if (blockState.isInitialized() )
            return blockState.get(data);
        else
            return 0;
    }

    public AABB getVisualShape(final Block block, AABB bufferAABB, boolean isClipping) {
        return this.mVisualShape;
    }

    public final short getBlockItemId() {
        if (this.mID < 0x100)
            return (short) this.mID;
        else
            return (short) (256 - this.mID - 1);
    }

    public final boolean hasProperty(BlockProperty type) {
        return (this.mProperties & type.getValue()) != BlockProperty.None_43.getValue();
    }

    public boolean isFenceBlock() {
        return false;
    }

    public boolean isThinFenceBlock() {
        return false;
    }

    public final BlockLegacy addBlockProperty(BlockProperty property) {
        this.mProperties |= property.getValue();
        return this;
    }

    public BlockLegacy setAllowsRunes(boolean interference) {
        this.mAllowsRunes = interference;
        return this;
    }

    public void setDefaultState(final Block block) {
        this.mDefaultState = block;
    }

    public BlockLegacy setDestroyTime(float destroySpeed) {
        this.mDestroySpeed = destroySpeed;
        if (5.0f * destroySpeed > this.mExplosionResistance)
            this.mExplosionResistance = 5.0f * destroySpeed;
        return this;
    }

    public void setSolid(boolean solid) {
        this.mSolid = solid;
        if (solid)
            this.mLightBlock = Brightness.MAX;
        else
            this.mLightBlock = Brightness.MIN;
        this.mPushesOutItems = solid;
    }

//    public final BlockLegacy createWeakPtr() {
//        return BlockTypeRegistry.lookupByName(this.getRawNameId());
//    }

    @NotImplemented
    public void spawnResources(BlockSource region, final BlockPos pos, final Block block, float explosionRadius, int bonusLootLevel) {
    }

    public final ItemActor popResource(BlockSource region, final BlockPos pos, final ItemInstance itemInstance) {
        if (region.getLevel().isClientSide())
            return null;
        return null;
    }

    public void playerDestroy(Player player, final BlockPos pos, final Block block) {
        ItemStack item = player.getSelectedItem();
        int bonusLootLevel = 0;
        boolean spawned = false;
        if (item.toBoolean() && item.isEnchanted()) {
            short Id = item.getId();
            if (Id != VanillaItems.mEnchanted_book.getId()) {
                if (this.canBeSilkTouched() && EnchantUtils.hasEnchant(Enchant.Type.MiningSilkTouch, item)) {
                    BlockSource region = player.getRegion();
                    this.popResource(region, pos, this.getSilkTouchItemInstance(block));
                    spawned = true;
                } else {
                    bonusLootLevel = EnchantUtils.getEnchantLevel(Enchant.Type.MiningLoot, item);
                }
            }
        }
        if (!spawned) {
            this.spawnResources(player.getRegion(), pos, block, 1.0f, bonusLootLevel);
        }
        player.causeFoodExhaustion(0.025f);
    }

    public final void createBlockPermutations(/*uint32_t*/int latestUpdaterVersion) {
        if (this instanceof UnknownBlock)
            return;
        int numStates = (int) Math.pow(2, Math.min(this.mTotalBitsUsed, 16));
        for (int i = this.mBlockPermutations.size(); i < Math.max(1, numStates); ++i) {
            this.mBlockPermutations.add(null);
        }
        Function<Integer, Boolean> isDataValid = (data) -> {
            if (data == null || data == 0)
                return true;
            for (ItemStateInstance __begin : this.mStates) {
                if (__begin != null && __begin.isInitialized() && !__begin.isValidData(data) )
                    return false;
            }
            return true;
        };
        for (int i = 0; i < numStates; ++i) {
            if (isDataValid.apply(i)) {
                Block newState = new Block((short) i, this);
                newState.buildSerializationId(latestUpdaterVersion);
                this.mBlockPermutations.set(i, newState);
            } else {
                this.mBlockPermutations.set(i, null);
            }
        }
        this.setDefaultState(this.mBlockPermutations.get(0));
    }
}
