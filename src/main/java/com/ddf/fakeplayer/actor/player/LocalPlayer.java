package com.ddf.fakeplayer.actor.player;

import com.ddf.fakeplayer.Client;
import com.ddf.fakeplayer.container.ContainerID;
import com.ddf.fakeplayer.container.inventory.transaction.ComplexInventoryTransaction;
import com.ddf.fakeplayer.container.inventory.transaction.InventoryTransaction;
import com.ddf.fakeplayer.container.inventory.transaction.ItemUseInventoryTransaction;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.item.VanillaItems;
import com.ddf.fakeplayer.level.GameType;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.network.NetworkBlockPosition;
import com.ddf.fakeplayer.util.DataConverter;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.Vec3;
import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.ClientPlayMode;
import com.nukkitx.protocol.bedrock.data.InputMode;
import com.nukkitx.protocol.bedrock.packet.MobEquipmentPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerAuthInputPacket;
import com.nukkitx.protocol.bedrock.packet.RespawnPacket;
import com.nukkitx.protocol.bedrock.packet.TextPacket;

import java.util.UUID;

public class LocalPlayer extends Player {
    private ItemStack mSelectedItemPrev = ItemStack.EMPTY_ITEM;
    private int mSelectedSlotPrev = 0;
    private PlayerRespawnState mClientRespawnState = PlayerRespawnState.ReadyToSpawn;
    private Vec3 mClientRespawnPotentialPosition = new Vec3(Vec3.ZERO);
    private long playerAuthInputTick = 0;

    public LocalPlayer(Level level, String name, UUID uuid, Client client) {
        super(level, client.getPacketSender(), GameType.Undefined, null, (byte) 0, uuid, "", null, "", "");
        super.setName(name);
        super.getSupplies().addListener(slot -> {
            if (slot == super.getSupplies().getSelectedSlot().mSlot) {
                super.getSupplies().selectSlot(slot, ContainerID.CONTAINER_ID_INVENTORY);
            }
        });
    }

    @Override
    public boolean isLocalPlayer() {
        return true;
    }

    public final void setClientRespawnState(PlayerRespawnState state) {
        this.mClientRespawnState = state;
    }

    public final void setClientRespawnPotentialPosition(final Vec3 position) {
        this.mClientRespawnPotentialPosition = position;
    }

    @Override
    public void sendInventoryTransaction(InventoryTransaction transaction) {
        this.sendComplexInventoryTransaction(ComplexInventoryTransaction.fromType(ComplexInventoryTransaction.Type.NormalTransaction, transaction));
    }

    @Override
    public void sendComplexInventoryTransaction(ComplexInventoryTransaction transaction) {
        this.sendNetworkPacket(DataConverter.inventoryTransactionPacket(transaction));
    }

    @Override
    public void sendNetworkPacket(BedrockPacket packet) {
        super.mPacketSender.send(packet);
    }

    @Override
    public final void normalTick() {
        super.normalTick();

        if (this.mClientRespawnState == PlayerRespawnState.SearchingForSpawn && this.isInitialSpawnDone()) {
            RespawnPacket packet = new RespawnPacket();
            packet.setPosition(Vector3f.ZERO);
            packet.setState(RespawnPacket.State.CLIENT_READY);
            packet.setRuntimeEntityId(super.getRuntimeID());
            this.sendNetworkPacket(packet);
        }

        if (super.getLevel().isServerAuthoritativeMovement()) {
            sendPlayerAuthInput();
        }

        ItemStack selectedItem = super.getSelectedItem();
        int selectedSlot = super.getSelectedItemSlot();
        if (!this.mSelectedItemPrev.equals(selectedItem) || this.mSelectedSlotPrev != selectedSlot) {
            this.mSelectedItemPrev = new ItemStack(selectedItem);
            this.mSelectedSlotPrev = selectedSlot;
            MobEquipmentPacket packet = new MobEquipmentPacket();
            packet.setRuntimeEntityId(super.getRuntimeID());
            packet.setContainerId(ContainerID.CONTAINER_ID_INVENTORY.getValue());
            packet.setInventorySlot(selectedSlot);
            packet.setHotbarSlot(selectedSlot);
            packet.setItem(DataConverter.itemData(selectedItem));
            this.sendNetworkPacket(packet);
        }
        super.updateInventoryTransactions();
        if (selectedItem.getItem() == VanillaItems.mTrident) {
            if (!isUsingItem()) {
                startUseSelectedItem();
            } else if(VanillaItems.mTrident.getMaxUseDuration(getCarriedItem()) - super.mItemInUseDuration > 10) {
                super.getGameMode().releaseUsingItem();
            }
        }
    }

    @Override
    public void respawn() {
        if (this.mRespawnReady) {
            super.respawn();
        }
    }

    private void startUseSelectedItem() {
        ItemUseInventoryTransaction transaction = new ItemUseInventoryTransaction(super.getLevel().getItemRegistry());
        transaction.setSelectedItem(super.getSelectedItem())
                .setSelectedSlot(super.getSelectedItemSlot())
                .setBlockPosition(new NetworkBlockPosition(0, 0, 0))
                .setFacing(0xFF)
                .setClickPosition(new Vec3(0.0f, 0.0f, 0.0f))
                .setFromPosition(this.getPos())
                .setActionType(ItemUseInventoryTransaction.ActionType.Use_1);
        super.getGameMode().useItem(super.getSelectedItem());
        this.sendNetworkPacket(DataConverter.inventoryTransactionPacket(transaction));
    }

    public final void sendInventoryMismatch() {
        ComplexInventoryTransaction transaction = ComplexInventoryTransaction.fromType(ComplexInventoryTransaction.Type.InventoryMismatch_0, null);
        sendComplexInventoryTransaction(transaction);
    }

    public final void sendChatMessage(String message) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.CHAT);
        packet.setNeedsTranslation(false);
        packet.setSourceName(super.getName());
        packet.setMessage(message);
        packet.setXuid(Long.toString(super.getClientUUID().getLeastSignificantBits()));
        this.sendNetworkPacket(packet);
    }

    public final void sendPlayerAuthInput() {
        PlayerAuthInputPacket packet = new PlayerAuthInputPacket();
        packet.setPosition(DataConverter.vector3f(super.getPos()));
        packet.setRotation(DataConverter.vector3f(super.getRotation(), super.getYHeadRot()));
        packet.setMotion(Vector2f.ZERO);
        packet.setInputMode(InputMode.TOUCH);
        packet.setPlayMode(ClientPlayMode.SCREEN);
        packet.setTick(playerAuthInputTick++);
        packet.setDelta(Vector3f.ZERO);
        this.sendNetworkPacket(packet);
    }

    @NotImplemented
    @Override
    public void displayChatMessage(final String author, final String message) {
    }

    @NotImplemented
    @Override
    public void displayClientMessage(final String message) {
    }
}
