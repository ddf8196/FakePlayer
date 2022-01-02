package com.ddf.fakeplayer.block;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.actor.ActorType;
import com.ddf.fakeplayer.actor.player.AbilitiesIndex;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.level.chunk.ChunkBlockPos;
import com.ddf.fakeplayer.level.chunk.ChunkPos;
import com.ddf.fakeplayer.level.chunk.ChunkSource;
import com.ddf.fakeplayer.level.chunk.LevelChunk;
import com.ddf.fakeplayer.level.dimension.Dimension;
import com.ddf.fakeplayer.util.AABB;
import com.ddf.fakeplayer.util.BrightnessPair;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.Vec3;

import java.util.ArrayList;

@SuppressWarnings("all")
public class BlockSource {
    private Thread mOwnerThreadID;
    private final boolean mAllowUnpopulatedChunks;
    private final boolean mPublicSource;
    private Level mLevel;
    private ChunkSource mChunkSource;
    private Dimension mDimension;
    private final /*Height*/short mMaxHeight;
    private ArrayList<BlockFetchResult> mTempBlockFetchResult = new ArrayList<>();
    private BlockPos mPlaceChunkPos = new BlockPos(ChunkPos.INVALID, 0);
    private ArrayList<BlockSourceListener> mListeners = new ArrayList<>();
    private ChunkPos mLastChunkPos = ChunkPos.INVALID;
    private LevelChunk mLastChunk = null;
    //private BlockTickingQueue mRandomTickQueue = null;
    //private BlockTickingQueue mTickQueue = null;
    private BrightnessPair mDefaultBrightness;
    //private ActorList mTempEntityList = new ArrayList<>();
    //private BlockActorList mTempBlockEntityList = new ArrayList<>();
    private ArrayList<AABB> mTempCubeList = new ArrayList<>();

    public BlockSource(Level level, Dimension dimension, ChunkSource source, boolean publicSource, boolean allowUnpopulatedChunks) {
        this.mOwnerThreadID = Thread.currentThread();
        this.mAllowUnpopulatedChunks = allowUnpopulatedChunks;
        this.mPublicSource = publicSource;
        this.mLevel = level;
        this.mChunkSource = source;
        this.mDimension = dimension;
        this.mMaxHeight = dimension.getHeight();
        this.mDefaultBrightness = dimension.getDefaultBrightness();
        if (publicSource) {
            this.addListener(level);
            for (BlockSourceListener mListener : this.mListeners) {
                mListener.onSourceCreated(this);
            }
        }
    }

    private boolean _getBlockPermissions(final BlockPos pos, boolean currentState) {
        if (pos.y >= this.mMaxHeight || this.hasBorderBlock(new BlockPos(pos))) {
            return false;
        } else {
            BlockPos posCheck = new BlockPos(pos);
            while (posCheck.y >= 0) {
                Block block = this.getBlock(posCheck);
                Material Material = block.getMaterial();
                if (Material.isType(MaterialType.Allow)) {
                    return true;
                }
                if (block.getMaterial().isType(MaterialType.Deny_0)) {
                    return false;
                }
                posCheck = new BlockPos(posCheck.x, posCheck.y - 1, posCheck.z);
            }
            return currentState;
        }
    }

    public final Level getLevel() {
        return this.mLevel;
    }

    public final boolean hasBlock(final BlockPos pos) {
        LevelChunk lc = this.getChunkAt(pos);
        if (lc != null && !lc.getPosition().equals(ChunkPos.INVALID)) {
            return !lc.isReadOnly();
        }
        return false;
    }

    @NotImplemented
    public void addListener(BlockSourceListener listener) {
    }

    public final boolean checkBlockDestroyPermissions(Actor entity, final BlockPos pos, final ItemStack item, boolean generateParticle) {
        Block block = this.getBlock(pos);
        boolean v6 = true;
        if (!this.mLevel.getAdventureSettings().immutableWorld)
            v6 = entity.isAdventure();
        boolean currentPermissions = !(v6);
        if (item != null && item.toBoolean() && entity.isAdventure()) {
            currentPermissions = item.canDestroy(block);
        }
        if (entity.isWorldBuilder() || block.getIgnoresDestroyPermissions(entity, pos) && currentPermissions) {
            return true;
        } else {
            boolean canMine = true;
            if (entity.hasType(ActorType.Player_0))
                canMine = ((Player) entity).canUseAbility(AbilitiesIndex.Mine);
            boolean permission = this._getBlockPermissions(pos, currentPermissions);
            if (permission && !block.hasProperty(BlockProperty.RequiresWorldBuilder) && canMine){
                return true;
            } else {
                if (generateParticle) {
                    Vec3 generateAt = new Vec3(pos.x + 0.5f, pos.y + 0.5f, pos.z + 0.5f);
                    this.mLevel.denyEffect(generateAt);
                }
                return false;
            }
        }
    }

    public final Block getBlock(final BlockPos pos) {
        if (pos.y < 0 || pos.y >= this.mMaxHeight)
            return BedrockBlocks.mAir;
        LevelChunk c = this.getChunk(new ChunkPos(pos));
        if (c == null)
            return BedrockBlocks.mAir;
        return c.getBlock(new ChunkBlockPos(pos));
    }

    public final Block getExtraBlock(final BlockPos p) {
        if ( p.y < 0 )
            return BedrockBlocks.mAir;
        if ( p.y >= this.mMaxHeight )
        return BedrockBlocks.mAir;
        ChunkPos pos = new ChunkPos(p);
        LevelChunk c = this.getChunk(pos);
        if (c == null)
            return BedrockBlocks.mAir;
        ChunkBlockPos localPos = new ChunkBlockPos(p);
        return c.getExtraBlock(localPos);
    }

    public final Block getLiquidBlock(final BlockPos p) {
        Block extraBlock = this.getExtraBlock(p);
        if (!extraBlock.equals(BedrockBlocks.mAir))
            return extraBlock;
        else
            return this.getBlock(p);
    }

    public final LevelChunk getChunk(final ChunkPos pos) {
        if (this.mLastChunk != null && this.mLastChunkPos.equals(pos))
            return this.mLastChunk;
        if (this.mAllowUnpopulatedChunks)
            this.mLastChunk = this.mChunkSource.getGeneratedChunk(pos);
        else
            this.mLastChunk = this.mChunkSource.getAvailableChunk(pos);
        if (this.mLastChunk != null)
            this.mLastChunkPos = this.mLastChunk.getPosition();
        else
            this.mLastChunkPos = ChunkPos.INVALID;
        return this.mLastChunk;
    }

    public final LevelChunk getChunkAt(final BlockPos pos) {
        return this.getChunk(new ChunkPos(pos));
    }

    public final boolean hasBorderBlock(final BlockPos pos) {
        LevelChunk chunk = this.getChunkAt(pos);
        if (chunk != null) {
            return chunk.getBorder(new ChunkBlockPos(pos));
        } else {
            return false;
        }
    }
}
