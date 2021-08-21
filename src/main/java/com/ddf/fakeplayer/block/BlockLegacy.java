package com.ddf.fakeplayer.block;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.actor.actors.ItemActor;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.blockactor.BlockActorType;
import com.ddf.fakeplayer.item.*;
import com.ddf.fakeplayer.item.enchant.Enchant;
import com.ddf.fakeplayer.item.enchant.EnchantUtils;
import com.ddf.fakeplayer.util.AABB;
import com.ddf.fakeplayer.util.BaseGameVersion;
import com.ddf.fakeplayer.util.Brightness;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.tuple.Tuple2;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("all")
public class BlockLegacy {
    public static final String BLOCK_DESCRIPTION_PREFIX = "tile.";
    public static final float SIZE_OFFSET = 0.000099999997f;

    private String mDescriptionId;
    private String mRawNameId = "";
    private String mNamespace = "";
    private String mFullName = "";
    private boolean mFancy = false;
    private BlockRenderLayer mRenderLayer = BlockRenderLayer.RENDERLAYER_OPAQUE;
    private boolean mRenderLayerCanRenderAsOpaque = false;
    private long mProperties = BlockProperty.CubeShaped.getValue();
    private BlockActorType mBlockEntityType = BlockActorType.Undefined_6;
    private boolean mAnimatedTexture = false;
    private float mBrightnessGamma = 1.0f;
    private float mThickness = 0.0f;
    private boolean mCanSlide = false;
    private boolean mCanInstatick = false;
    private boolean mIsInteraction = false;
    private float mGravity = 1.0f;
    private final Material mMaterial;
    //private Color mMapColor;
    private float mFriction = 0.60000002f;
    private boolean mHeavy = false;
    private float mParticleQuantityScalar = 1.0f;
    private float mDestroySpeed = 0.0f;
    private float mExplosionResistance = 0.0f;
    private CreativeItemCategory mCreativeCategory = CreativeItemCategory.All_3;
    private boolean mAllowsRunes = false;
    private boolean mCanBeBrokenFromFalling = true;
    private boolean mSolid = false;
    private boolean mPushesOutItems = true;
    private boolean mIgnoreBlockForInsideCubeRenderer = false;
    private boolean mIsTrapdoor = false;
    private boolean mIsDoor = false;
    private float mTranslucency = 0.0f;
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
    private ItemStateInstance[] mStates = new ItemStateInstance[105];
    private ArrayList<Block> mBlockPermutations = new ArrayList<>();
    private Block mDefaultState = null;
    private ReentrantLock mLegacyDataLookupTableMutex = new ReentrantLock();
    private ArrayList<Long> mLegacyDataLookupTable = new ArrayList<>();

    @NotImplemented
    public BlockLegacy(final String nameId, int id, final Material material) {
        if (nameId.isEmpty()) {
            this.mDescriptionId = "";
        } else {
            this.mDescriptionId = BlockLegacy.BLOCK_DESCRIPTION_PREFIX + nameId;
        }
        this.mMaterial = material;
        //this.mMapColor = material.getColor();
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

    public final Block getDefaultState() {
        return this.mDefaultState;
    }

    public final float getDestroySpeed() {
        return this.mDestroySpeed;
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

    public boolean canBeSilkTouched() {
        return true;
    }

    public boolean canHurtAndBreakItem() {
        return this.mDestroySpeed > 0.0f;
    }

    public final boolean hasProperty(BlockProperty type) {
        return (this.mProperties & type.getValue()) != BlockProperty.None_43.getValue();
    }

    public void setSolid(boolean solid) {
        this.mSolid = solid;
        if (solid)
            this.mLightBlock = Brightness.MAX;
        else
            this.mLightBlock = Brightness.MIN;
        this.mPushesOutItems = solid;
    }

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
            if (Id != VanillaItems.mEnchanted_book.getId(player.getLevel().getItemRegistry())) {
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
}
