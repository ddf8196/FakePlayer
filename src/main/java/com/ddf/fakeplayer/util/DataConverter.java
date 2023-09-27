package com.ddf.fakeplayer.util;

import com.ddf.fakeplayer.actor.ActorDataIDs;
import com.ddf.fakeplayer.actor.ActorFlags;
import com.ddf.fakeplayer.actor.DataItemType;
import com.ddf.fakeplayer.actor.player.PlayerRespawnState;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.container.ContainerID;
import com.ddf.fakeplayer.container.inventory.InventoryAction;
import com.ddf.fakeplayer.container.inventory.InventorySource;
import com.ddf.fakeplayer.container.inventory.transaction.*;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.level.GameType;
import com.ddf.fakeplayer.level.dimension.ChangeDimensionRequest;
import com.ddf.fakeplayer.nbt.*;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NbtList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleBlockDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataType;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityFlag;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryActionData;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.ChangeDimensionPacket;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.cloudburstmc.protocol.bedrock.packet.RespawnPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataConverter {
    public static Vec3 vec3(Vector3f vector3f) {
        return new Vec3(vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }

    public static Vec2 vec2(Vector3f vector3f) {
        return new Vec2(vector3f.getX(), vector3f.getY());
    }

    public static Vec2 vec2(Vector2f vector2f) {
        return new Vec2(vector2f.getX(), vector2f.getY());
    }

    public static Vector3f vector3f(Vec3 vec3) {
        return Vector3f.from(vec3.x, vec3.y, vec3.z);
    }

    public static Vector3f vector3f(Vec2 vec2, float z) {
        return Vector3f.from(vec2.x, vec2.y, z);
    }

    public static BlockPos blockPos(Vector3i vector3i) {
        return new BlockPos(vector3i.getX(), vector3i.getY(), vector3i.getZ());
    }

    public static Vector3i vector3i(BlockPos blockPos) {
        return Vector3i.from(blockPos.x, blockPos.y, blockPos.z);
    }

    public static ItemStack itemStack(ItemData itemData) {
        ItemStack itemStack = new ItemStack(itemData.getDefinition().getRuntimeId(), itemData.getCount(), itemData.getDamage(), compoundTag(itemData.getTag()));
        itemStack.setBlockingTick(itemData.getBlockingTicks());
        itemStack.clientInitNetId(itemData.getNetId());
        return itemStack;
    }

    public static ItemData itemData(ItemStack itemStack) {
        return ItemData.builder()
                .definition(new SimpleItemDefinition("", itemStack.getId(), false))
                //.id(itemStack.getId())
                .damage(itemStack.getAuxValue())
                .count(itemStack.getStackSize())
                .tag(nbtMap(itemStack.getUserData()))
                //.canPlace()
                //.canBreak()
                .blockingTicks(itemStack.getBlockingTick())
                .usingNetId(itemStack.getItemStackNetIdVariant().hasAnyValidId())
                .netId(itemStack.getItemStackNetIdVariant().toInt())
                .build();
    }

    public static InventoryTransactionPacket inventoryTransactionPacket(ComplexInventoryTransaction complexInventoryTransaction) {
        InventoryTransactionPacket packet = new InventoryTransactionPacket();
        Map<InventorySource, ArrayList<InventoryAction>> actions = complexInventoryTransaction.getInventoryTransaction().getAllActions();
        if (actions != null) {
            actions.forEach((inventorySource, inventoryActions) ->
                    inventoryActions.forEach(action ->
                            packet.getActions().add(inventoryActionData(action))));
        }
        switch (complexInventoryTransaction.getType()) {
            case NormalTransaction: {
                packet.setTransactionType(InventoryTransactionType.NORMAL);
            }
            break;
            case InventoryMismatch_0: {
                packet.setTransactionType(InventoryTransactionType.INVENTORY_MISMATCH);
            }
            break;
            case ItemUseTransaction: {
                ItemUseInventoryTransaction transaction = (ItemUseInventoryTransaction) complexInventoryTransaction;
                packet.setTransactionType(InventoryTransactionType.ITEM_USE);
                packet.setItemInHand(itemData(transaction.getSelectedItem()));
                packet.setHotbarSlot(transaction.getSelectedSlot());
                packet.setBlockPosition(vector3i(transaction.getBlockPosition()));
                packet.setBlockFace(transaction.getFacing());
                packet.setClickPosition(vector3f(transaction.getClickPosition()));
                packet.setPlayerPosition(vector3f(transaction.getFromPosition()));
                packet.setActionType(transaction.getActionType().ordinal());
                packet.setBlockDefinition(new SimpleBlockDefinition("", transaction.getTargetBlockId(), NbtMap.EMPTY));
            }
            break;
            case ItemUseOnEntityTransaction: {
                ItemUseOnActorInventoryTransaction transaction = (ItemUseOnActorInventoryTransaction) complexInventoryTransaction;
                packet.setTransactionType(InventoryTransactionType.ITEM_USE_ON_ENTITY);
                packet.setItemInHand(itemData(transaction.getSelectedItem()));
                packet.setHotbarSlot(transaction.getSelectedSlot());
                packet.setActionType(transaction.getActionType().ordinal());
                packet.setRuntimeEntityId(transaction.getEntityRuntimeId());
                packet.setPlayerPosition(vector3f(transaction.getFromPosition()));
                packet.setClickPosition(vector3f(transaction.getHitPosition()));
            }
            break;
            case ItemReleaseTransaction: {
                ItemReleaseInventoryTransaction transaction = (ItemReleaseInventoryTransaction) complexInventoryTransaction;
                packet.setTransactionType(InventoryTransactionType.ITEM_RELEASE);
                packet.setItemInHand(itemData(transaction.getSelectedItem()));
                packet.setHotbarSlot(transaction.getSelectedSlot());
                packet.setHeadPosition(vector3f(transaction.getFromPosition()));
                packet.setActionType(transaction.getActionType().ordinal());
            }
        }
        return packet;
    }

    public static ComplexInventoryTransaction complexInventoryTransaction(InventoryTransactionPacket inventoryTransactionPacket) {
        InventoryTransaction transaction = new InventoryTransaction();
        ComplexInventoryTransaction.Type type = null;
        switch (inventoryTransactionPacket.getTransactionType()) {
            case NORMAL: {
                type = ComplexInventoryTransaction.Type.NormalTransaction;
                for (InventoryActionData actionData : inventoryTransactionPacket.getActions()) {
                    transaction.addAction(inventoryAction(actionData));
                }
            }
            break;
            case INVENTORY_MISMATCH: {
                type = ComplexInventoryTransaction.Type.InventoryMismatch_0;
            }
            break;
            case ITEM_USE: {
                type = ComplexInventoryTransaction.Type.ItemUseTransaction;
            }
            break;
            case ITEM_USE_ON_ENTITY: {
                type = ComplexInventoryTransaction.Type.ItemUseOnEntityTransaction;
            }
            break;
            case ITEM_RELEASE: {
                type = ComplexInventoryTransaction.Type.ItemReleaseTransaction;
            }
            break;
        }
        return ComplexInventoryTransaction.fromType(type, transaction);
    }

    public static InventoryActionData inventoryActionData(InventoryAction inventoryAction) {
        return new InventoryActionData(
                inventorySource(inventoryAction.getSource()),
                inventoryAction.getSlot(),
                itemData(inventoryAction.getFromItem()),
                itemData(inventoryAction.getToItem()));
    }

    public static InventoryAction inventoryAction(InventoryActionData inventoryActionData) {
        return new InventoryAction(
                inventorySource(inventoryActionData.getSource()),
                inventoryActionData.getSlot(),
                itemStack(inventoryActionData.getFromItem()),
                itemStack(inventoryActionData.getToItem()));
    }

    public static InventorySource inventorySource(org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventorySource inventorySource) {
        switch (inventorySource.getType()) {
            case INVALID:
                return InventorySource.fromInvalid();
            case CONTAINER:
                return InventorySource.fromContainerWindowID(ContainerID.getByValue(inventorySource.getContainerId()));
            case GLOBAL:
                return InventorySource.fromGlobalInventory();
            case WORLD_INTERACTION:
                return InventorySource.fromWorldInteraction(InventorySource.InventorySourceFlags.values()[inventorySource.getFlag().ordinal()]);
            case CREATIVE:
                return InventorySource.fromCreativeInventory();
            case UNTRACKED_INTERACTION_UI:
                return InventorySource.fromUntrackedInteractionUI(ContainerID.getByValue(inventorySource.getContainerId()));
            case NON_IMPLEMENTED_TODO:
                return InventorySource.fromNONIMPLEMENTEDTODO(ContainerID.getByValue(inventorySource.getContainerId()));
            default:
                return null;
        }
    }

    public static org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventorySource inventorySource(InventorySource inventorySource) {
        switch (inventorySource.getType()) {
            case InvalidInventory:
                return org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventorySource.fromInvalid();
            case ContainerInventory:
                return org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventorySource.fromContainerWindowId(inventorySource.getContainerId().getValue());
            case GlobalInventory:
                return org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventorySource.fromGlobalInventory();
            case WorldInteraction:
                return org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventorySource.fromWorldInteraction(org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventorySource.Flag.values()[inventorySource.getFlags().ordinal()]);
            case CreativeInventory:
                return org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventorySource.fromCreativeInventory();
            case UntrackedInteractionUI:
                return org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventorySource.fromUntrackedInteractionUI(inventorySource.getContainerId().getValue());
            case NonImplementedFeatureTODO:
                return org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventorySource.fromNonImplementedTodo(inventorySource.getContainerId().getValue());
            default:
                return null;
        }
    }

    @SuppressWarnings("rawtypes")
    public static NbtType nbtType(Tag.Type type) {
        if (type == null) {
            return NbtType.END;
        }
        switch (type) {
            default:
            case End_1:
                return NbtType.END;
            case Byte_0:
                return NbtType.BYTE;
            case Short_0:
                return NbtType.SHORT;
            case Int_2:
                return NbtType.INT;
            case Float_3:
                return NbtType.FLOAT;
            case Int64_0:
                return NbtType.LONG;
            case Double:
                return NbtType.DOUBLE;
            case ByteArray:
                return NbtType.BYTE_ARRAY;
            case IntArray:
                return NbtType.INT_ARRAY;
            case String_1:
                return NbtType.STRING;
            case List_0:
                return NbtType.LIST;
            case Compound:
                return NbtType.COMPOUND;
        }
    }

    @SuppressWarnings("unchecked")
    public static NbtMap nbtMap(CompoundTag compoundTag) {
        if (compoundTag == null) {
            return null;
        }
        NbtMapBuilder builder = NbtMap.builder();
        for (Map.Entry<String, Tag> entry : compoundTag) {
            switch (entry.getValue().getId()) {
                case End_1:
                    break;
                case Byte_0:
                    builder.putByte(entry.getKey(), ((ByteTag) entry.getValue()).data);
                    break;
                case Short_0:
                    builder.putShort(entry.getKey(), ((ShortTag) entry.getValue()).data);
                    break;
                case Int_2:
                    builder.putInt(entry.getKey(), ((IntTag) entry.getValue()).data);
                    break;
                case Float_3:
                    builder.putFloat(entry.getKey(), ((FloatTag) entry.getValue()).data);
                    break;
                case Int64_0:
                    builder.putLong(entry.getKey(), ((LongTag) entry.getValue()).data);
                    break;
                case Double:
                    builder.putDouble(entry.getKey(), ((DoubleTag) entry.getValue()).data);
                    break;
                case String_1:
                    builder.putString(entry.getKey(), ((StringTag) entry.getValue()).data);
                    break;
                case List_0:
                    builder.putList(entry.getKey(), nbtType(((ListTag) entry.getValue()).getType()), nbtList((ListTag) entry.getValue()));
                    break;
                case Compound:
                    builder.putCompound(entry.getKey(), nbtMap((CompoundTag) entry.getValue()));
                    break;
            }
        }
        return builder.build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static NbtList nbtList(ListTag listTag) {
        if (listTag == null) {
            return null;
        }
        List list = new ArrayList();
        switch (listTag.getType()) {
            default:
            case End_1:
                break;
            case Byte_0:
                for (Tag tag : listTag) {
                    list.add(((ByteTag) tag).data);
                }
                break;
            case Short_0:
                for (Tag tag : listTag) {
                    list.add(((ShortTag) tag).data);
                }
                break;
            case Int_2:
                for (Tag tag : listTag) {
                    list.add(((IntTag) tag).data);
                }
                break;
            case Float_3:
                for (Tag tag : listTag) {
                    list.add(((FloatTag) tag).data);
                }
                break;
            case Int64_0:
                for (Tag tag : listTag) {
                    list.add(((LongTag) tag).data);
                }
                break;
            case Double:
                for (Tag tag : listTag) {
                    list.add(((DoubleTag) tag).data);
                }
                break;
            case ByteArray:
                for (Tag tag : listTag) {
                    list.add(((ByteArrayTag) tag).data);
                }
                break;
            case IntArray:
                for (Tag tag : listTag) {
                    list.add(((IntArrayTag) tag).data);
                }
                break;
            case String_1:
                for (Tag tag : listTag) {
                    list.add(((StringTag) tag).data);
                }
                break;
            case List_0:
                for (Tag tag : listTag) {
                    list.add(nbtList((ListTag) tag));
                }
                break;
            case Compound:
                for (Tag tag : listTag) {
                    list.add(nbtMap((CompoundTag) tag));
                }
                break;
        }
        return new NbtList(nbtType(listTag.getType()), list);
    }

    @SuppressWarnings("rawtypes")
    public static CompoundTag compoundTag(NbtMap nbtMap) {
        if (nbtMap == null) {
            return null;
        }
        CompoundTag compoundTag = new CompoundTag();
        for (Map.Entry<String, Object> entry : nbtMap.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Boolean) {
                compoundTag.putBoolean(name, (Boolean) value);
                continue;
            }
            if (value instanceof Byte) {
                compoundTag.putByte(name, (Byte) value);
                continue;
            }
            if (value instanceof Short) {
                compoundTag.putShort(name, (Short) value);
                continue;
            }
            if (value instanceof Integer) {
                compoundTag.putInt(name, (Integer) value);
                continue;
            }
            if (value instanceof Float) {
                compoundTag.putFloat(name, (Float) value);
                continue;
            }
            if (value instanceof Long) {
                compoundTag.putLong(name, (Long) value);
                continue;
            }
            if (value instanceof Double) {
                compoundTag.putDouble(name, (Double) value);
                continue;
            }
            if (value instanceof byte[]) {
                compoundTag.putByteArray(name, (byte[]) value);
                continue;
            }
            if (value instanceof int[]) {
                compoundTag.putIntArray(name, (int[]) value);
                continue;
            }
            if (value instanceof String) {
                compoundTag.putString(name, (String) value);
                continue;
            }
            if (value instanceof NbtList) {
                compoundTag.putList(name, listTag((NbtList) value));
                continue;
            }
            if (value instanceof NbtMap) {
                compoundTag.putCompound(name, compoundTag((NbtMap) value));
            }
        }
        return compoundTag;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ListTag listTag(NbtList list) {
        ListTag listTag = new ListTag();
        switch (list.getType().getEnum()) {
            default:
            case END:
                break;
            case BYTE:
                for (Byte value : (NbtList<Byte>) list) {
                    listTag.add(new ByteTag(value));
                }
                break;
            case SHORT:
                for (Short value : (NbtList<Short>) list) {
                    listTag.add(new ShortTag(value));
                }
                break;
            case INT:
                for (Integer value : (NbtList<Integer>) list) {
                    listTag.add(new IntTag(value));
                }
                break;
            case FLOAT:
                for (Float value : (NbtList<Float>) list) {
                    listTag.add(new FloatTag(value));
                }
                break;
            case LONG:
                for (Long value : (NbtList<Long>) list) {
                    listTag.add(new LongTag(value));
                }
                break;
            case DOUBLE:
                for (Double value : (NbtList<Double>) list) {
                    listTag.add(new DoubleTag(value));
                }
                break;
            case BYTE_ARRAY:
                for (byte[] value : (NbtList<byte[]>) list) {
                    listTag.add(new ByteArrayTag(value));
                }
                break;
            case INT_ARRAY:
                for (int[] value : (NbtList<int[]>) list) {
                    listTag.add(new IntArrayTag(value));
                }
                break;
            case STRING:
                for (String value : (NbtList<String>) list) {
                    listTag.add(new StringTag(value));
                }
                break;
            case LIST:
                for (NbtList value : (NbtList<NbtList>) list) {
                    listTag.add(listTag(value));
                }
                break;
            case COMPOUND:
                for (NbtMap value : (NbtList<NbtMap>) list) {
                    listTag.add(compoundTag(value));
                }
                break;
        }
        return listTag;
    }

    public static GameType gameType(org.cloudburstmc.protocol.bedrock.data.GameType gameType) {
        return GameType.getByValue(gameType.ordinal());
    }

    public static org.cloudburstmc.protocol.bedrock.data.GameType gameType(GameType gameType) {
        if (gameType == GameType.Undefined) {
            return null;
        }
        return org.cloudburstmc.protocol.bedrock.data.GameType.from(gameType.getValue());
    }

    public static PlayerRespawnState playerRespawnState(RespawnPacket.State respawnState) {
        return PlayerRespawnState.values()[respawnState.ordinal()];
    }

    public static RespawnPacket.State respawnState(PlayerRespawnState playerRespawnState) {
        return RespawnPacket.State.values()[playerRespawnState.ordinal()];
    }

    public static ChangeDimensionRequest dimensionRequest(ChangeDimensionPacket changeDimensionPacket) {
        return new ChangeDimensionRequest(
                        0,
                        changeDimensionPacket.getDimension(),
                        vec3(changeDimensionPacket.getPosition()),
                        true,
                        changeDimensionPacket.isRespawn());
    }

    private static final Map<EntityDataType<?>, ActorDataIDs> ENTITY_DATA_TYPE_ACTOR_DATA_IDS_MAP = new HashMap<EntityDataType<?>, ActorDataIDs>() {
        {
            put(EntityDataTypes.FLAGS, ActorDataIDs.FLAGS);
            put(EntityDataTypes.STRUCTURAL_INTEGRITY, ActorDataIDs.STRUCTURAL_INTEGRITY);
            put(EntityDataTypes.VARIANT, ActorDataIDs.VARIANT);
            put(EntityDataTypes.COLOR, ActorDataIDs.COLOR_INDEX);
            put(EntityDataTypes.NAME, ActorDataIDs.NAME);
//            put(EntityDataTypes.OWNER, ActorDataIDs.OWNER);
//            put(EntityDataTypes.TARGET, ActorDataIDs.TARGET);
            put(EntityDataTypes.AIR_SUPPLY, ActorDataIDs.AIR_SUPPLY);
            put(EntityDataTypes.EFFECT_COLOR, ActorDataIDs.EFFECT_COLOR);
            put(EntityDataTypes.EFFECT_AMBIENCE, ActorDataIDs.EFFECT_AMBIENCE);
            put(EntityDataTypes.JUMP_DURATION, ActorDataIDs.JUMP_DURATION);
//            put(EntityDataTypes.HURT, ActorDataIDs.HURT);
//            put(EntityDataTypes.HURT_DIR, ActorDataIDs.HURT_DIR);
            put(EntityDataTypes.ROW_TIME_LEFT, ActorDataIDs.ROW_TIME_LEFT);
            put(EntityDataTypes.ROW_TIME_RIGHT, ActorDataIDs.ROW_TIME_RIGHT);
            put(EntityDataTypes.VALUE, ActorDataIDs.VALUE);
//            put(EntityDataTypes.DISPLAY_TILE_RUNTIME_ID, ActorDataIDs.DISPLAY_TILE_RUNTIME_ID);
            put(EntityDataTypes.DISPLAY_OFFSET, ActorDataIDs.DISPLAY_OFFSET);
            put(EntityDataTypes.CUSTOM_DISPLAY, ActorDataIDs.CUSTOM_DISPLAY);
//            put(EntityDataTypes.SWELL, ActorDataIDs.SWELL);
            put(EntityDataTypes.OLD_SWELL, ActorDataIDs.OLD_SWELL);
//            put(EntityDataTypes.SWELL_DIR, ActorDataIDs.SWELL_DIR);
            put(EntityDataTypes.CHARGE_AMOUNT, ActorDataIDs.CHARGE_AMOUNT);
//            put(EntityDataTypes.CARRY_BLOCK_RUNTIME_ID, ActorDataIDs.CARRY_BLOCK_RUNTIME_ID);
            put(EntityDataTypes.CLIENT_EVENT, ActorDataIDs.CLIENT_EVENT);
            put(EntityDataTypes.USING_ITEM, ActorDataIDs.USING_ITEM);
            put(EntityDataTypes.PLAYER_FLAGS, ActorDataIDs.PLAYER_FLAGS);
            put(EntityDataTypes.PLAYER_INDEX, ActorDataIDs.PLAYER_INDEX);
            put(EntityDataTypes.BED_POSITION, ActorDataIDs.BED_POSITION);
//            put(EntityDataTypes.X_POWER, ActorDataIDs.X_POWER);
//            put(EntityDataTypes.Y_POWER, ActorDataIDs.Y_POWER);
//            put(EntityDataTypes.Z_POWER, ActorDataIDs.Z_POWER);
            put(EntityDataTypes.AUX_POWER, ActorDataIDs.AUX_POWER);
//            put(EntityDataTypes.FISHX, ActorDataIDs.FISHX);
//            put(EntityDataTypes.FISHZ, ActorDataIDs.FISHZ);
//            put(EntityDataTypes.FISHANGLE, ActorDataIDs.FISHANGLE);
            put(EntityDataTypes.LEASH_HOLDER, ActorDataIDs.LEASH_HOLDER);
            put(EntityDataTypes.SCALE, ActorDataIDs.SCALE);
            put(EntityDataTypes.HAS_NPC, ActorDataIDs.HAS_NPC);
            put(EntityDataTypes.NPC_DATA, ActorDataIDs.NPC_DATA);
            put(EntityDataTypes.ACTIONS, ActorDataIDs.ACTIONS);
            put(EntityDataTypes.AIR_SUPPLY_MAX, ActorDataIDs.AIR_SUPPLY_MAX);
            put(EntityDataTypes.MARK_VARIANT, ActorDataIDs.MARK_VARIANT);
            put(EntityDataTypes.CONTAINER_TYPE, ActorDataIDs.CONTAINER_TYPE);
            put(EntityDataTypes.CONTAINER_SIZE, ActorDataIDs.CONTAINER_SIZE);
            put(EntityDataTypes.CONTAINER_STRENGTH_MODIFIER, ActorDataIDs.CONTAINER_STRENGTH_MODIFIER);
//            put(EntityDataTypes.BLOCK_TARGET, ActorDataIDs.BLOCK_TARGET);
//            put(EntityDataTypes.INV, ActorDataIDs.INV);
//            put(EntityDataTypes.TARGET_A, ActorDataIDs.TARGET_A);
//            put(EntityDataTypes.TARGET_B, ActorDataIDs.TARGET_B);
//            put(EntityDataTypes.TARGET_C, ActorDataIDs.TARGET_C);
//            put(EntityDataTypes.AERIAL_ATTACK, ActorDataIDs.AERIAL_ATTACK);
            put(EntityDataTypes.WIDTH, ActorDataIDs.WIDTH);
            put(EntityDataTypes.HEIGHT, ActorDataIDs.HEIGHT);
            put(EntityDataTypes.FUSE_TIME, ActorDataIDs.FUSE_TIME);
            put(EntityDataTypes.SEAT_OFFSET, ActorDataIDs.SEAT_OFFSET);
            put(EntityDataTypes.SEAT_LOCK_RIDER_ROTATION, ActorDataIDs.SEAT_LOCK_RIDER_ROTATION);
            put(EntityDataTypes.SEAT_LOCK_RIDER_ROTATION_DEGREES, ActorDataIDs.SEAT_LOCK_RIDER_ROTATION_DEGREES);
//            put(EntityDataTypes.SEAT_ROTATION_OFFSET, ActorDataIDs.SEAT_ROTATION_OFFSET);
//            put(EntityDataTypes.DATA_RADIUS, ActorDataIDs.DATA_RADIUS);
//            put(EntityDataTypes.DATA_WAITING, ActorDataIDs.DATA_WAITING);
//            put(EntityDataTypes.DATA_PARTICLE, ActorDataIDs.DATA_PARTICLE);
//            put(EntityDataTypes.PEEK_ID, ActorDataIDs.PEEK_ID);
//            put(EntityDataTypes.ATTACH_FACE, ActorDataIDs.ATTACH_FACE);
//            put(EntityDataTypes.ATTACHED, ActorDataIDs.ATTACHED);
//            put(EntityDataTypes.ATTACH_POS, ActorDataIDs.ATTACH_POS);
//            put(EntityDataTypes.TRADE_TARGET, ActorDataIDs.TRADE_TARGET);
            put(EntityDataTypes.CAREER, ActorDataIDs.CAREER);
//            put(EntityDataTypes.HAS_COMMAND_BLOCK, ActorDataIDs.HAS_COMMAND_BLOCK);
//            put(EntityDataTypes.COMMAND_NAME, ActorDataIDs.COMMAND_NAME);
//            put(EntityDataTypes.LAST_COMMAND_OUTPUT, ActorDataIDs.LAST_COMMAND_OUTPUT);
//            put(EntityDataTypes.TRACK_COMMAND_OUTPUT, ActorDataIDs.TRACK_COMMAND_OUTPUT);
//            put(EntityDataTypes.CONTROLLING_SEAT_INDEX, ActorDataIDs.CONTROLLING_SEAT_INDEX);
            put(EntityDataTypes.STRENGTH, ActorDataIDs.STRENGTH);
            put(EntityDataTypes.STRENGTH_MAX, ActorDataIDs.STRENGTH_MAX);
//            put(EntityDataTypes.DATA_SPELL_CASTING_COLOR, ActorDataIDs.DATA_SPELL_CASTING_COLOR);
            put(EntityDataTypes.DATA_LIFETIME_TICKS, ActorDataIDs.DATA_LIFETIME_TICKS);
//            put(EntityDataTypes.POSE_INDEX, ActorDataIDs.POSE_INDEX);
//            put(EntityDataTypes.DATA_TICK_OFFSET, ActorDataIDs.DATA_TICK_OFFSET);
            put(EntityDataTypes.NAMETAG_ALWAYS_SHOW, ActorDataIDs.NAMETAG_ALWAYS_SHOW);
//            put(EntityDataTypes.COLOR_2_INDEX, ActorDataIDs.COLOR_2_INDEX);
            put(EntityDataTypes.NAME_AUTHOR, ActorDataIDs.NAME_AUTHOR);
            put(EntityDataTypes.SCORE, ActorDataIDs.SCORE);
//            put(EntityDataTypes.BALLOON_ANCHOR, ActorDataIDs.BALLOON_ANCHOR);
            put(EntityDataTypes.PUFFED_STATE, ActorDataIDs.PUFFED_STATE);
//            put(EntityDataTypes.BUBBLE_TIME, ActorDataIDs.BUBBLE_TIME);
//            put(EntityDataTypes.AGENT, ActorDataIDs.AGENT);
            put(EntityDataTypes.SITTING_AMOUNT, ActorDataIDs.SITTING_AMOUNT);
            put(EntityDataTypes.SITTING_AMOUNT_PREVIOUS, ActorDataIDs.SITTING_AMOUNT_PREVIOUS);
            put(EntityDataTypes.EATING_COUNTER, ActorDataIDs.EATING_COUNTER);
//            put(EntityDataTypes.FLAGS2, ActorDataIDs.FLAGS2);
            put(EntityDataTypes.LAYING_AMOUNT, ActorDataIDs.LAYING_AMOUNT);
            put(EntityDataTypes.LAYING_AMOUNT_PREVIOUS, ActorDataIDs.LAYING_AMOUNT_PREVIOUS);
//            put(EntityDataTypes.DATA_DURATION, ActorDataIDs.DATA_DURATION);
//            put(EntityDataTypes.DATA_SPAWN_TIME, ActorDataIDs.DATA_SPAWN_TIME);
//            put(EntityDataTypes.DATA_CHANGE_RATE, ActorDataIDs.DATA_CHANGE_RATE);
//            put(EntityDataTypes.DATA_CHANGE_ON_PICKUP, ActorDataIDs.DATA_CHANGE_ON_PICKUP);
//            put(EntityDataTypes.DATA_PICKUP_COUNT, ActorDataIDs.DATA_PICKUP_COUNT);
            put(EntityDataTypes.INTERACT_TEXT, ActorDataIDs.INTERACT_TEXT);
            put(EntityDataTypes.TRADE_TIER, ActorDataIDs.TRADE_TIER);
            put(EntityDataTypes.MAX_TRADE_TIER, ActorDataIDs.MAX_TRADE_TIER);
            put(EntityDataTypes.TRADE_EXPERIENCE, ActorDataIDs.TRADE_EXPERIENCE);
            put(EntityDataTypes.SKIN_ID, ActorDataIDs.SKIN_ID);
            put(EntityDataTypes.SPAWNING_FRAMES, ActorDataIDs.SPAWNING_FRAMES);
            put(EntityDataTypes.COMMAND_BLOCK_TICK_DELAY, ActorDataIDs.COMMAND_BLOCK_TICK_DELAY);
            put(EntityDataTypes.COMMAND_BLOCK_EXECUTE_ON_FIRST_TICK, ActorDataIDs.COMMAND_BLOCK_EXECUTE_ON_FIRST_TICK);
            put(EntityDataTypes.AMBIENT_SOUND_INTERVAL, ActorDataIDs.AMBIENT_SOUND_INTERVAL);
            put(EntityDataTypes.AMBIENT_SOUND_INTERVAL_RANGE, ActorDataIDs.AMBIENT_SOUND_INTERVAL_RANGE);
            put(EntityDataTypes.AMBIENT_SOUND_EVENT_NAME, ActorDataIDs.AMBIENT_SOUND_EVENT_NAME);
            put(EntityDataTypes.FALL_DAMAGE_MULTIPLIER, ActorDataIDs.FALL_DAMAGE_MULTIPLIER);
            put(EntityDataTypes.NAME_RAW_TEXT, ActorDataIDs.NAME_RAW_TEXT);
            put(EntityDataTypes.CAN_RIDE_TARGET, ActorDataIDs.CAN_RIDE_TARGET);
        }
    };

    public static ActorDataIDs actorDataIDs(EntityDataType<?> entityDataType) {
        return ENTITY_DATA_TYPE_ACTOR_DATA_IDS_MAP.get(entityDataType);
//        if (entityDataType == EntityDataTypes.CONTAINER_TYPE) {
//            return ActorDataIDs.CONTAINER_TYPE;
//        }
//        if (entityData.ordinal() >= ActorDataIDs.values().length)
//            return null;
//        return ActorDataIDs.values()[entityData.ordinal()];
    }

    public static DataItemType dataItemType(EntityDataType<?> entityDataType) {
        switch (entityDataType.getTypeName()) {
            case "java.lang.Byte":
                return DataItemType.Byte;
            case "java.lang.Short":
                return DataItemType.Short;
            case "org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition":
            case "java.lang.Integer":
                return DataItemType.Int_1;
            case "java.lang.Float":
                return DataItemType.Float_1;
            case "java.lang.String":
                return DataItemType.String_0;
            case "org.cloudburstmc.nbt.NbtMap":
                return DataItemType.CompoundTag;
            case "org.cloudburstmc.math.vector.Vector3i":
                return DataItemType.Pos;
            case "java.lang.Long":
                return DataItemType.Int64;
            case "org.cloudburstmc.math.vector.Vector3f":
                return DataItemType.Vec3;
            default:
                return DataItemType.Unknown_23;
        }

//        if (entityDataType == null)
//            return DataItemType.Unknown_23;
//        int index = entityDataType.ordinal() - 1;
//        if (index < 0)
//            index = DataItemType.values().length - 1;
//        return DataItemType.values()[index];
    }

    @NotImplemented
    public static ActorFlags actorFlags(EntityFlag entityFlag) {
        return null;
    }
}