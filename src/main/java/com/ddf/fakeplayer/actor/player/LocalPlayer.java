package com.ddf.fakeplayer.actor.player;

import com.ddf.fakeplayer.Client;
import com.ddf.fakeplayer.container.ContainerID;
import com.ddf.fakeplayer.container.inventory.transaction.ComplexInventoryTransaction;
import com.ddf.fakeplayer.container.inventory.transaction.InventoryTransaction;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.level.GameType;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.util.*;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.MobEquipmentPacket;
import com.nukkitx.protocol.bedrock.packet.RespawnPacket;

import java.util.UUID;

public class LocalPlayer extends Player {
    private ItemStack mSelectedItemPrev = ItemStack.EMPTY_ITEM;
    private int mSelectedSlotPrev = 0;
    private PlayerRespawnState mClientRespawnState = PlayerRespawnState.ReadyToSpawn;
    private Vec3 mClientRespawnPotentialPosition = new Vec3(Vec3.ZERO);

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
    public void normalTick() {
        super.normalTick();

        if (this.mClientRespawnState == PlayerRespawnState.SearchingForSpawn && this.isInitialSpawnDone()) {
            RespawnPacket packet = new RespawnPacket();
            packet.setPosition(Vector3f.ZERO);
            packet.setState(RespawnPacket.State.CLIENT_READY);
            packet.setRuntimeEntityId(super.getRuntimeID());
            this.sendNetworkPacket(packet);
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
    }

    @Override
    public void respawn() {
        if (this.mRespawnReady) {
            super.respawn();
        }
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
