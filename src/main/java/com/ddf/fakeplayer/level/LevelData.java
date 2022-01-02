package com.ddf.fakeplayer.level;

import com.ddf.fakeplayer.actor.player.Abilities;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.level.gamerule.GameRules;
import com.ddf.fakeplayer.level.generator.GeneratorType;
import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.SemVersion;

@SuppressWarnings("all")
public class LevelData {
    private AdventureSettings mAdventureSettings = new AdventureSettings();
    //private WorldTemplateLevelData mWorldTemplateLevelData = new WorldTemplateLevelData();
    private GameRules mGameRules = new GameRules();
    private Abilities mDefaultAbilities = new Abilities();
    private String mLevelName = "";
    //private StorageVersion mStorageVersion = SharedConstants.CurrentStorageVersion;
    //private GameVersion mMinCompatibleClientVersion = new GameVersion(SharedConstants.MinimumCompatibleGameVersionForLevelData);
    private int mNetworkVersion;// = SharedConstants.NetworkProtocolVersion;
    private SemVersion mInventoryVersion = new SemVersion();
    private long mCurrentTick = 0;
    //private RandomSeed mSeed = 0;
    private BlockPos mSpawnPos = new BlockPos(BlockPos.MIN);
    private boolean mHasSpawnPos = false;
    private BlockPos mLimitedWorldOrigin = new BlockPos(BlockPos.MIN);
    private int mTime = 0;
    private long mLastPlayed = 0L;
    private CompoundTag mLoadedPlayerTag = new CompoundTag();
    private /*uint32_t*/long mServerTickRange = 4;
    private float mRainLevel = 0.0f;
    private int mRainTime = 0;
    private float mLightningLevel = 0.0f;
    private int mLightningTime = 0;
    private int mNetherScale = 8;
    //private GameVersion mLastOpenedWithVersion = GameVersion.current();
    private GameType mGameType = GameType.Survival;
    private Difficulty mGameDifficulty = Difficulty.Normal_0;
    private boolean mForceGameType = false;
    private boolean mSpawnMobs = this.mGameType == GameType.Survival;
    private GeneratorType mGenerator = GeneratorType.Overworld;
    private /*uint32_t*/long mWorldStartCount = -1;
    private boolean mAchievementsDisabled = false;
    //private EducationEditionOffer mEducationEditionOffer = EducationEditionOffer.None_3;
    private boolean mEducationFeaturesEnabled = false;
    private boolean mConfirmedPlatformLockedContent = false;
    private boolean mMultiplayerGameIntent = true;
    private boolean mMultiplayerGame = true;
    private boolean mLANBroadcastIntent = true;
    private boolean mLANBroadcast = true;
    //private Social.GamePublishSetting mXBLBroadcastIntent = FriendsOfFriends;
    //private Social.GamePublishSetting mXBLBroadcastMode = FriendsOfFriends;
    //private Social.GamePublishSetting mPlatformBroadcastIntent = FriendsOfFriends;
    //private Social.GamePublishSetting mPlatformBroadcastMode = FriendsOfFriends;
    private boolean mCommandsEnabled = false;
    private boolean mTexturePacksRequired = false;
    private boolean mHasLockedBehaviorPack = false;
    private boolean mHasLockedResourcePack = false;
    private boolean mIsFromLockedTemplate = false;
    private String mEducationOid = "";
    private String mEducationProductId = "";
    private boolean mUseMsaGamertagsOnly = false;
    private boolean mBonusChestEnabled = false;
    private boolean mBonusChestSpawned = false;
    private boolean mStartWithMapEnabled = false;
    private boolean mMapsCenteredToOrigin = false;
    private boolean mRequiresCopiedPackRemovalCheck = false;
    private boolean mSpawnV1Villagers = false;

//    public LevelData() {
//        SemVersion.fromString(Common.getGameSemVerString(), this.mInventoryVersion, NoAnyVersion);
//        if (ServiceLocator<AppPlatform>.get().isEduMode()) {
//            this.mEducationFeaturesEnabled = true;
//            this.mTexturePacksRequired = true;
//        }
//    }

    private void _updateLimitedWorldOrigin(final BlockPos pos) {
        if (this.mLimitedWorldOrigin.equals(BlockPos.MIN))
            this.mLimitedWorldOrigin = pos;
    }

    public final AdventureSettings getAdventureSettings() {
        return this.mAdventureSettings;
    }

    public final long getCurrentTick() {
        return this.mCurrentTick;
    }

    public final Abilities getDefaultAbilities() {
        return this.mDefaultAbilities;
    }

    public final Difficulty getGameDifficulty() {
        return this.mGameDifficulty;
    }

    public final GameType getGameType() {
        return this.mGameType;
    }

    public final GeneratorType getGenerator() {
        return this.mGenerator;
    }

    public final String getLevelName() {
        return this.mLevelName;
    }

    public final BlockPos getSpawnPos() {
        return this.mSpawnPos;
    }

    public final BlockPos getWorldCenter() {
        return this.mLimitedWorldOrigin;
    }

    public final boolean hasSpawnPos() {
        return this.mHasSpawnPos;
    }

    public final boolean hasStartWithMapEnabled() {
        return this.mStartWithMapEnabled;
    }

    @NotImplemented
    public final boolean isLegacyLevel() {
        return false;
        //return this.mGenerator == Legacy;
    }

    public final void setCurrentTick(long currentTick) {
        this.mCurrentTick = currentTick;
    }

    public final void setGameType(GameType type) {
        this.mGameType = type;
    }

    public final void setGenerator(GeneratorType generator) {
        this.mGenerator = generator;
    }

    public final void setSpawnPos(final BlockPos spawn) {
        this.mSpawnPos = spawn;
        this._updateLimitedWorldOrigin(spawn);
        this.mHasSpawnPos = true;
    }

    public void setTime(int time) {
        this.mTime = time;
    }

    public final void setLevelName(String name) {
        this.mLevelName = name;
    }

    public final void incrementTick() {
        ++this.mCurrentTick;
    }
}
