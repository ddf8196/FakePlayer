package com.ddf.fakeplayer.level.gamemode;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.container.inventory.PlayerInventoryProxy;
import com.ddf.fakeplayer.block.*;
import com.ddf.fakeplayer.container.ContainerID;
import com.ddf.fakeplayer.container.inventory.InventoryAction;
import com.ddf.fakeplayer.container.inventory.InventorySource;
import com.ddf.fakeplayer.container.inventory.transaction.*;
import com.ddf.fakeplayer.item.*;
import com.ddf.fakeplayer.network.NetworkBlockPosition;
import com.ddf.fakeplayer.util.AABB;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.ValueHolder;
import com.ddf.fakeplayer.util.Vec3;
import com.ddf.fakeplayer.util.mc.HitResult;

//@SuppressWarnings("all")
public class GameMode {
    public static final float PICKRANGE_VR_SURVIVAL = 7.0f;
    public static final float PICKRANGE_VR_CREATIVE = 12.0f;
    public static final float PICKRANGE_MOUSE = 5.6999998f;
    public static final float PICKRANGE_GAMEPAD = 5.5999999f;
    public static final float PICKRANGE_TOUCH_CREATIVE = 12.0f;
    public static final float PICKRANGE_TOUCH_SURVIVAL = 6.6999998f;
    public static final float PICKRANGE_SURVIVAL_MAXIMUM = 7.0f;
    public static final float PICKRANGE_CREATIVE_MAXIMUM = 12.0f;
    public static final float PICKRANGE_SURVIVAL_MAXIMUM_SQR = 49.0f;
    public static final float PICKRANGE_CREATIVE_MAXIMUM_SQR = 144.0f;

    private Player mPlayer;
    private BlockPos mDestroyBlockPos = new BlockPos(-1);
    private float mOldDestroyProgress = 0.0f;
    private float mDestroyProgress = 0.0f;
    private double mLastDestroyTime = 0.0d;
    private float mDistanceTravelled = 0.0f;
    private Vec3 mPlayerLastPosition = new Vec3(Vec3.ZERO);
    private BlockPos mLastBuiltBlockPosition = new BlockPos(-1);
    private boolean mLastBuildBlockWasInteractive = false;
    private boolean mLastBuildBlockWasSnappable = false;
    private float mMinPlayerSpeed = 0.5f;
    private long mLastBuildTime = 0;
    private long mNoDestroyUntil = 0;
    private long mNoDestroySoundUntil = 0;
    private long creativeDestructionTickDelay = 250;
    private long buildingTickDelay = 200;
    private long destroySoundDelay = 200;

    private boolean mHasBuildDirection = false;
    private boolean mHasLastBuiltPosition = false;
    private /*uint8_t FacingID*/int mContinueFacing;
    private BlockPos mBuildDirection = new BlockPos();
    private BlockPos mNextBuildPos = new BlockPos();

    public GameMode(Player player) {
        this.mPlayer = player;
    }

    private boolean _canUseBlock(final Block block) {
        return this.mPlayer.canUseOperatorBlocks() || !block.hasProperty(BlockProperty.OperatorBlock);
    }

    public final BlockPos getDestroyBlockPos() {
        return new BlockPos(this.mDestroyBlockPos);
    }

    public final float getDestroyProgress() {
        return this.mDestroyProgress;
    }

    @NotImplemented
    public final float getDestroyRate(final Block block) {
        float progress = this.mPlayer.getDestroyProgress(block);
//        int amp = 0;
//        if (this.mPlayer.hasEffect(MobEffect.DIG_SPEED)) {
//            amp = this.mPlayer.getEffect(MobEffect.DIG_SPEED).getAmplifier();
//        }
//        if (this.mPlayer.hasEffect(MobEffect.CONDUIT_POWER) ) {
//            amp = Math.max(amp, this.mPlayer.getEffect(MobEffect.CONDUIT_POWER).getAmplifier() + 1);
//        }
//        if (amp > 0) {
//            progress = (float) (progress * Math.pow(1.2, amp + 1));
//        }
//        if (this.mPlayer.hasEffect(MobEffect.DIG_SLOWDOWN) ) {
//            int amplifier = this.mPlayer.getEffect(MobEffect.DIG_SLOWDOWN).getAmplifier();
//            return (float) (progress * Math.pow(0.69999999, amplifier + 1));
//        }
        return progress;
    }

    @NotImplemented
    private boolean _destroyBlockInternal(final BlockPos pos, /*uint8_t FacingID*/int face) {
        return true;
//        BlockSource region = this.mPlayer.getRegion();
//        Block oldBlock = region.getBlock(pos);
//        BlockLegacy LegacyBlock = oldBlock.getLegacyBlock();
//        if (LegacyBlock.equals(BedrockBlockTypes.mAir)) {
//            return false;
//        } else {
//            variantData = oldBlock.telemetryVariant(region, pos);
//            BlockLegacy oldBlockType = oldBlock.getLegacyBlock();
//            boolean changed = oldBlock.playerWillDestroy(this.mPlayer, pos);
//            Block newBlock = region.getBlock(pos);
//            if (changed && !newBlock.equals(oldBlock)) {
//                this.mPlayer.getLevel().broadcastDimensionEvent(this.mPlayer.getRegion(), LevelEvent.ParticlesDestroyBlock, new Vec3(pos.x, pos.y, pos.z).add(0.5f, 0.5f, 0.5f), oldBlock.getRuntimeId(), 0L);
//                MinecraftEventing.fireEventBlockBroken(this.mPlayer, oldBlock, Entity_1, variantData);
//                ClientPlayerEventCoordinator ClientPlayerEventCoordinator = Level.getClientPlayerEventCoordinator();
//                ClientPlayerEventCoordinator.sendPlayerDestroyedBlock(this.mPlayer, oldBlockType);
//                long runtimeId = this.mPlayer.getRuntimeID();
//                PlayerActionPacket packet = new PlayerActionPacket();
//                packet.setAction(PlayerActionType.STOP_BREAK);
//                packet.setRuntimeEntityId(runtimeId);
//                this.mPlayer.sendNetworkPacket(packet);
//            }
//            if (changed)
//                return !newBlock.equals(oldBlock);
//            return false;
//        }
    }

    public boolean destroyBlock(final BlockPos pos, /*FacingID*/int face) {
        boolean success = false;
        BlockSource region = this.mPlayer.getRegion();
        Player entity = this.mPlayer;
        ItemStack SelectedItem = entity.getSelectedItem();
        if (region.checkBlockDestroyPermissions(entity, pos, SelectedItem, false)) {
            ValueHolder<Boolean> successHolder = new ValueHolder<>(success);
            ItemUseInventoryTransaction transaction = new ItemUseInventoryTransaction();
            PlayerInventoryProxy supplies = this.mPlayer.getSupplies();
            supplies.createTransactionContext((container, slot, oldItem, newItem) -> {
                InventoryAction action = new InventoryAction(InventorySource.fromContainerWindowID(ContainerID.CONTAINER_ID_INVENTORY), slot, oldItem, newItem);
                this.mPlayer.getTransactionManager().addExpectedAction(action);
                transaction.getInventoryTransaction().addAction(action);
            }, () -> {
                ItemStack item = new ItemStack(SelectedItem);
                transaction.setSelectedItem(item)
                        .setSelectedSlot(supplies.getSelectedSlot().mSlot)
                        .setBlockPosition(new NetworkBlockPosition(pos))
                        .setFacing(face)
                        .setClickPosition(new Vec3(0.0f, 0.0f, 0.0f))
                        .setFromPosition(this.mPlayer.getPos())
                        .setActionType(ItemUseInventoryTransaction.ActionType.Destroy);
                Block block = region.getBlock(pos);
                if (this._canUseBlock(block) && !this.mPlayer.isCreative() || !item.toBoolean() || item.getItem().canDestroyInCreative()) {
                    successHolder.set(this._destroyBlockInternal(pos, face));
                    int v5 = this.mPlayer.getItemUseDuration();
                    boolean canDestroy = false;
                    if ( v5 <= 0 )
                        canDestroy = this.mPlayer.canDestroy(block);
                    if ( v5 <= 0 && item.toBoolean())
                        item.mineBlock(block, pos.x, pos.y, pos.z, this.mPlayer);
                    if (successHolder.get() && canDestroy && !this.mPlayer.isCreative()) {
                        //mPlayer.getLevel().getBlockEventCoordinator().sendBlockDestroyedByPlayer(mPlayer, block.getFullName(), pos);
                        block.playerDestroy(this.mPlayer, pos);
                    }
                    this.mPlayer.setSelectedItem(item);
                } else {
                    successHolder.set(false);
                }
            });
            success = successHolder.get();
            if (success && this.mPlayer.getLevel().isClientSide()) {
                this.mPlayer.swing();
                this.mPlayer.sendComplexInventoryTransaction(transaction);
            }
            return success;
        } else {
            return false;
        }
    }

    public boolean useItem(ItemStack item) {
        ItemInstance itemPriorToUse = new ItemInstance(item);
        item = item.use(this.mPlayer);
        return !itemPriorToUse.matches(item);
    }

    public boolean useItemOn(ItemStack item, final BlockPos at,/*FacingID*/int face, final Vec3 hit, final Block targetBlock) {
        Vec3 v46 = new Vec3(at);
        Vec3 click = hit.subtract(v46);
        Block block = this.mPlayer.getRegion().getBlock(at);
        Block liquidBlock = this.mPlayer.getRegion().getLiquidBlock(at);
        if (block.getLegacyBlock().equals(VanillaBlockTypes.mInvisibleBedrock)) {
            return false;
        } else if (targetBlock != null && !targetBlock.equals(block)) {
            return false;
        } else {
            AABB aabb = new AABB();
            if (!block.getAABB(this.mPlayer.getRegion(), at, aabb, false).isEmpty()
                    || item.getItem() != VanillaItems.mBucket
                    || block.getMaterial().isLiquid()
                    || liquidBlock.getMaterial().isLiquid()
                    || block.canContainLiquid()) {
                if (this.mPlayer.isSneaking()
                        && this.mPlayer.getCarriedItem().toBoolean()
                        || block.getLegacyBlock().equals(BedrockBlockTypes.mAir)
                        || !block.use(this.mPlayer, at)) {
                    if (item.toBoolean()) {
                        if (!item.isBlock() || this._canUseBlock(item.getLegacyBlock().getDefaultState())) {
                            boolean isSnappableBlock = false;
                            boolean success;
                            if (item.isBlock()) {
                                isSnappableBlock = isSnappableBlock(item.getLegacyBlock());
                            }
                            this.mLastBuildBlockWasSnappable = isSnappableBlock;
                            if (this.mPlayer.isCreative()) {
                                ItemStack oldItem = new ItemStack(item);
                                success = item.useOn(this.mPlayer, at.x, at.y, at.z, face, click.x, click.y, click.z);
                                if (!success)
                                    item = oldItem;
                            } else {
                                success = item.useOn(this.mPlayer, at.x, at.y, at.z, face, click.x, click.y, click.z);
                            }
                            if (!success || item.toBoolean()) {
                                if (success)
                                    this.mPlayer.setSelectedItem(item);
                            } else {
                                PlayerInventoryProxy.SlotData selectedSlot = this.mPlayer.getSupplies().getSelectedSlot();
                                this.mPlayer.getSupplies().clearSlot(selectedSlot.mSlot, selectedSlot.mContainerId);
                            }
                            return success;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    this.mLastBuildBlockWasInteractive = block.isInteractiveBlock();
                    ItemStack selectedItem = this.mPlayer.getSelectedItem();
                    if (!this.mLastBuildBlockWasInteractive) {
                        if (selectedItem.isBlock()) {
                            this.mLastBuildBlockWasSnappable = isSnappableBlock(selectedItem.getLegacyBlock());
                        } else {
                            this.mLastBuildBlockWasSnappable = false;
                        }
                    } else {
                        this.mLastBuildBlockWasSnappable = false;
                    }
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    private static boolean isSnappableBlock(final BlockLegacy block) {
        return block.hasProperty(BlockProperty.CubeShaped)
            || block.hasProperty(BlockProperty.Stair)
            || block.hasProperty(BlockProperty.HalfSlab)
            || block.isFenceBlock()
            || block.isThinFenceBlock();
    }

    private void _releaseUsingItemInternal() {
        this.mPlayer.releaseUsingItem();
    }

    public void releaseUsingItem() {
        ItemStack item = new ItemStack(this.mPlayer.getSelectedItem());
        ItemReleaseInventoryTransaction transaction = new ItemReleaseInventoryTransaction();
        this.mPlayer.getSupplies().createTransactionContext((container, slot, oldItem, newItem) -> {
            InventoryAction action = new InventoryAction(InventorySource.fromContainerWindowID(ContainerID.CONTAINER_ID_INVENTORY), slot, oldItem, newItem);
            this.mPlayer.getTransactionManager().addExpectedAction(action);
            transaction.getInventoryTransaction().addAction(action);
        }, () -> {
            transaction.setSelectedItem(item)
                    .setSelectedSlot(mPlayer.getSupplies().getSelectedSlot().mSlot)
                    .setFromPosition(this.mPlayer.getPos())
                    .setActionType(ItemReleaseInventoryTransaction.ActionType.Release_0);
            if (this.mPlayer.isCreative() && this.mPlayer.getCarriedItem().toBoolean()) {
                Item oldItem = this.mPlayer.getCarriedItem().getItem();
                short oldAux = this.mPlayer.getCarriedItem().getAuxValue();
                this._releaseUsingItemInternal();
                if (oldItem != null) {
                    if (this.mPlayer.getCarriedItem().toBoolean() && this.mPlayer.getCarriedItem().isInstance(oldItem)) {
                        ItemStack itemStack = new ItemStack(this.mPlayer.getCarriedItem());
                        if (!itemStack.isBlock())
                            itemStack.setAuxValue(oldAux);
                        this.mPlayer.setCarriedItem(itemStack);
                    }
                }
            } else {
                this._releaseUsingItemInternal();
            }
        });
        if (this.mPlayer.getLevel().isClientSide()) {
            this.mPlayer.sendComplexInventoryTransaction(transaction);
        }
    }

    public boolean buildBlock(final BlockPos pos, /*FacingID*/int face) {
        ValueHolder<Boolean> success = new ValueHolder<>(false);
        ItemUseInventoryTransaction transaction = new ItemUseInventoryTransaction();
        this.mPlayer.getSupplies().createTransactionContext((container, slot, oldItem, newItem) -> {
            InventoryAction action = new InventoryAction(InventorySource.fromContainerWindowID(ContainerID.CONTAINER_ID_INVENTORY), slot, oldItem, newItem);
            this.mPlayer.getTransactionManager().addExpectedAction(action);
            transaction.getInventoryTransaction().addAction(action);
        }, () -> {
            ItemStack item = new ItemStack(this.mPlayer.getSelectedItem());
            ItemInstance itemInstance = new ItemInstance(item);
            transaction.setSelectedItem(item)
                    .setSelectedSlot(this.mPlayer.getSupplies().getSelectedSlot().mSlot)
                    .setBlockPosition(new NetworkBlockPosition(0, 0, 0));
            HitResult hitResult = this.mPlayer.getLevel().getHitResult();
            HitResult liquidHitResult = this.mPlayer.getLevel().getLiquidHitResult();
            if (item.toBoolean() && item.isLiquidClipItem() && liquidHitResult.isHitLiquid()) {
                BlockPos liquidBlockPos = liquidHitResult.getLiquid();
                int liquidFacing = liquidHitResult.getLiquidFacing();
                Vec3 liquidPos = liquidHitResult.getLiquidPos();
                transaction.setBlockPosition(new NetworkBlockPosition(liquidBlockPos))
                        .setTargetBlock(this.mPlayer.getRegion().getBlock(liquidBlockPos))
                        .setFacing(liquidFacing)
                        .setClickPosition(liquidPos.subtract(new Vec3(liquidBlockPos)))
                        .setFromPosition(this.mPlayer.getPos())
                        .setActionType(ItemUseInventoryTransaction.ActionType.Place_4);
                success.set(this.useItemOn(item, liquidBlockPos, liquidFacing, liquidPos, null));
            }
            if (!success.get()) {

            }
        });
        if (this.mPlayer.getLevel().isClientSide()) {
            this.mPlayer.sendComplexInventoryTransaction(transaction);
        }
        return success.get();
    }

    public final boolean baseUseItem(ItemStack item) {
        ValueHolder<Boolean> success = new ValueHolder<>(false);
        ItemUseInventoryTransaction transaction = new ItemUseInventoryTransaction();
        this.mPlayer.getSupplies().createTransactionContext((container, slot, oldItem, newItem) -> {
            InventoryAction action = new InventoryAction(InventorySource.fromContainerWindowID(ContainerID.CONTAINER_ID_INVENTORY), slot, oldItem, newItem);
            this.mPlayer.getTransactionManager().addExpectedAction(action);
            transaction.getInventoryTransaction().addAction(action);
        }, () -> {
            transaction.setSelectedItem(item)
                    .setSelectedSlot(this.mPlayer.getSupplies().getSelectedSlot().mSlot)
                    .setBlockPosition(new NetworkBlockPosition(0, 0, 0))
                    .setFacing(0xFF)
                    .setClickPosition(Vec3.ZERO)
                    .setFromPosition(this.mPlayer.getPos())
                    .setActionType(ItemUseInventoryTransaction.ActionType.Use_1);
            success.set(this.useItem(item));
            if (success.get())
                this.mPlayer.setSelectedItem(item);
        });
        if (this.mPlayer.getLevel().isClientSide()) {
            this.mPlayer.sendComplexInventoryTransaction(transaction);
        }
        return success.get();
    }

    public boolean interact(Actor entity, final Vec3 location) {
        ValueHolder<Boolean> success = new ValueHolder<>(false);
        ItemUseOnActorInventoryTransaction transaction = new ItemUseOnActorInventoryTransaction();
        this.mPlayer.getSupplies().createTransactionContext((container, slot, oldItem, newItem) -> {
            InventoryAction action = new InventoryAction(InventorySource.fromContainerWindowID(ContainerID.CONTAINER_ID_INVENTORY), slot, oldItem, newItem);
            this.mPlayer.getTransactionManager().addExpectedAction(action);
            transaction.getInventoryTransaction().addAction(action);
        }, () -> {
            transaction.setSelectedItem(this.mPlayer.getSelectedItem())
                    .setSelectedSlot(this.mPlayer.getSupplies().getSelectedSlot().mSlot)
                    .setActionType(ItemUseOnActorInventoryTransaction.ActionType.Interact_1)
                    .setEntityRuntimeId(entity.getRuntimeID())
                    .setFromPosition(this.mPlayer.getPos())
                    .setHitPosition(location);
            success.set(this.mPlayer.interact(entity, location));
        });
        if (this.mPlayer.getLevel().isClientSide()) {
            this.mPlayer.sendComplexInventoryTransaction(transaction);
        }
        return success.get();
    }

    public void tick() {
        this.mOldDestroyProgress = this.mDestroyProgress;
    }
}
