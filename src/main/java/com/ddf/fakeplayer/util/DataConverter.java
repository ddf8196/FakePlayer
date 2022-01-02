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
import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlag;
import com.nukkitx.protocol.bedrock.data.inventory.InventoryActionData;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.data.inventory.TransactionType;
import com.nukkitx.protocol.bedrock.packet.ChangeDimensionPacket;
import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;
import com.nukkitx.protocol.bedrock.packet.RespawnPacket;

import java.util.ArrayList;
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
        ItemStack itemStack = new ItemStack(itemData.getId(), itemData.getCount(), itemData.getDamage(), compoundTag(itemData.getTag()));
        itemStack.setBlockingTick(itemData.getBlockingTicks());
        itemStack.clientInitNetId(itemData.getNetId());
        return itemStack;
    }

    public static ItemData itemData(ItemStack itemStack) {
        return ItemData.builder()
                .id(itemStack.getId())
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
                packet.setTransactionType(TransactionType.NORMAL);
            }
            break;
            case InventoryMismatch_0: {
                packet.setTransactionType(TransactionType.INVENTORY_MISMATCH);
            }
            break;
            case ItemUseTransaction: {
                ItemUseInventoryTransaction transaction = (ItemUseInventoryTransaction) complexInventoryTransaction;
                packet.setTransactionType(TransactionType.ITEM_USE);
                packet.setItemInHand(itemData(transaction.getSelectedItem()));
                packet.setHotbarSlot(transaction.getSelectedSlot());
                packet.setBlockPosition(vector3i(transaction.getBlockPosition()));
                packet.setBlockFace(transaction.getFacing());
                packet.setClickPosition(vector3f(transaction.getClickPosition()));
                packet.setPlayerPosition(vector3f(transaction.getFromPosition()));
                packet.setActionType(transaction.getActionType().ordinal());
            }
            break;
            case ItemUseOnEntityTransaction: {
                ItemUseOnActorInventoryTransaction transaction = (ItemUseOnActorInventoryTransaction) complexInventoryTransaction;
                packet.setTransactionType(TransactionType.ITEM_USE_ON_ENTITY);
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
                packet.setTransactionType(TransactionType.ITEM_RELEASE);
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

    public static InventorySource inventorySource(com.nukkitx.protocol.bedrock.data.inventory.InventorySource inventorySource) {
        switch (inventorySource.getType()) {
//            case INVALID:
//                return null;
            case CONTAINER:
                return InventorySource.fromContainerWindowID(ContainerID.getByValue(inventorySource.getContainerId()));
//            case GLOBAL:
//                return null;
            case WORLD_INTERACTION:
                return InventorySource.fromWorldInteraction(InventorySource.InventorySourceFlags.values()[inventorySource.getFlag().ordinal()]);
            case CREATIVE:
                return InventorySource.fromCreativeInventory();
            case UNTRACKED_INTERACTION_UI:
            case NON_IMPLEMENTED_TODO:
            default:
                return null;
        }
    }

    public static com.nukkitx.protocol.bedrock.data.inventory.InventorySource inventorySource(InventorySource inventorySource) {
        switch (inventorySource.getType()) {
//            case InvalidInventory:
//                return null;
            case ContainerInventory:
                return com.nukkitx.protocol.bedrock.data.inventory.InventorySource.fromContainerWindowId(inventorySource.getContainerId().getValue());
//            case GlobalInventory:
//                return null;
            case WorldInteraction:
                return com.nukkitx.protocol.bedrock.data.inventory.InventorySource.fromWorldInteraction(com.nukkitx.protocol.bedrock.data.inventory.InventorySource.Flag.values()[inventorySource.getFlags().ordinal()]);
            case CreativeInventory:
                return com.nukkitx.protocol.bedrock.data.inventory.InventorySource.fromCreativeInventory();
            case UntrackedInteractionUI:
            case NonImplementedFeatureTODO:
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

    public static GameType gameType(com.nukkitx.protocol.bedrock.data.GameType gameType) {
        return GameType.getByValue(gameType.ordinal());
    }

    public static com.nukkitx.protocol.bedrock.data.GameType gameType(GameType gameType) {
        if (gameType == GameType.Undefined) {
            return null;
        }
        return com.nukkitx.protocol.bedrock.data.GameType.from(gameType.getValue());
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

    public static ActorDataIDs actorDataIDs(EntityData entityData) {
        if (entityData.ordinal() >= ActorDataIDs.values().length)
            return null;
        return ActorDataIDs.values()[entityData.ordinal()];
    }

    public static DataItemType dataItemType(EntityData.Type entityDataType) {
        if (entityDataType == null)
            return DataItemType.Unknown_23;
        int index = entityDataType.ordinal() - 1;
        if (index < 0)
            index = DataItemType.values().length - 1;
        return DataItemType.values()[index];
    }

    @NotImplemented
    public static ActorFlags actorFlags(EntityFlag entityFlag) {
        return null;
    }
}