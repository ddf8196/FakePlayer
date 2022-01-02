package com.ddf.fakeplayer.container.inventory;

import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.container.ContainerType;
import com.ddf.fakeplayer.container.FillingContainer;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.item.MapConstants;
import com.ddf.fakeplayer.item.VanillaItems;
import com.ddf.fakeplayer.item.items.MapItem;
import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.util.DataConverter;
import com.ddf.fakeplayer.util.NotImplemented;
import com.nukkitx.protocol.bedrock.packet.InventorySlotPacket;

@NotImplemented
public class Inventory extends FillingContainer {
    public Inventory(Player player) {
        super(player, 36, ContainerType.INVENTORY);
    }

    public void init() {
        this.setupDefault();
    }

    private void setupDefault() {
        this.clearInventory(36);
        if (this.mPlayer != null) {
            if (this.mPlayer.getLevel().hasStartWithMapEnabled()) {
                ItemStack map = new ItemStack(VanillaItems.mEmptyMap, 1, 2);
                CompoundTag tag = new CompoundTag();
                tag.putInt(MapItem.TAG_MAP_SCALE, this._getDefaultStartingMapScale());
                map.setUserData(tag);
                this.setItem(this.getContainerSize() - 36, map);
            }
        }
    }

    private int _getDefaultStartingMapScale() {
        if (this.mPlayer != null && this.mPlayer.getLevel().getLevelData().isLegacyLevel()) {
            return MapConstants.DEFAULT_SCALE_LEGACY_STARTING_MAP;
        } else {
            return MapConstants.DEFAULT_SCALE_STARTING_MAP;
        }
    }

    public int getEmptySlotsCount() {
        return this._getEmptySlotsCount(0, this.getContainerSize());
    }

    public int getFirstEmptySlot() {
        int end = this.getContainerSize();
        for (int i = 0; i < end; ++i) {
            ItemStack item = this.getItem(i);
            if (item != null && !item.toBoolean())
                return i;
        }
        return -1;
    }

    public void dropAll(boolean onlyClearContainer) {
        this.dropAll(0, this.getContainerSize(), onlyClearContainer);
    }

    public final void tick(int selectedSlot) {
        if (this.mPlayer != null) {
            for (int i = 0; i < this.mItems.size(); ++i ) {
                ItemStack item = this.mItems.get(i);
                if (item != null && item.toBoolean()) {
                    boolean itemIsDirty = item.inventoryTick(this.mPlayer/*.getRegion()*/.getLevel(), this.mPlayer, i, selectedSlot == i);
                    if (!this.mPlayer.isLocalPlayer() && itemIsDirty) {
                        this.mPlayer.getSupplies().containerContentChanged(i);
                        InventorySlotPacket packet = new InventorySlotPacket();
                        packet.setContainerId(this.mPlayer.getSupplies().getSelectedContainerId().getValue());
                        packet.setSlot(i);
                        packet.setItem(DataConverter.itemData(item));
                        this.mPlayer.sendNetworkPacket(packet);
                    }
                }
            }
        }
    }
}
