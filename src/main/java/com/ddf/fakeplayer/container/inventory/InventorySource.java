package com.ddf.fakeplayer.container.inventory;

import com.ddf.fakeplayer.container.ContainerID;

public class InventorySource {
    private final InventorySourceType mType;
    private ContainerID mContainerId;
    private final InventorySource.InventorySourceFlags mFlags;

    public InventorySource(ContainerID containerId) {
        this.mType = InventorySourceType.ContainerInventory;
        this.mContainerId = containerId;
        this.mFlags = InventorySourceFlags.NoFlag;
    }

    public InventorySource(InventorySourceType sourceType) {
        this.mType = sourceType;
        this.mContainerId = ContainerID.CONTAINER_ID_NONE;
        this.mFlags = InventorySourceFlags.NoFlag;
    }

    public InventorySource(InventorySourceType sourceType, InventorySource.InventorySourceFlags flags) {
        this.mType = sourceType;
        this.mContainerId = ContainerID.CONTAINER_ID_NONE;
        this.mFlags = flags;
    }

    public static InventorySource fromContainerWindowID(ContainerID containerId) {
        return new InventorySource(containerId);
    }

    public static InventorySource fromWorldInteraction(InventorySource.InventorySourceFlags flags) {
        return new InventorySource(InventorySourceType.WorldInteraction, flags);
    }

    public static InventorySource fromCreativeInventory() {
        return new InventorySource(InventorySourceType.CreativeInventory, InventorySourceFlags.NoFlag);
    }

    public static InventorySource fromGlobalInventory() {
        return new InventorySource(InventorySourceType.GlobalInventory);
    }

    public static InventorySource fromUntrackedInteractionUI(ContainerID containerId) {
        InventorySource result = new InventorySource(InventorySourceType.UntrackedInteractionUI);
        result.mContainerId = containerId;
        return result;
    }

    public static InventorySource fromNONIMPLEMENTEDTODO(ContainerID containerId) {
        InventorySource result = new InventorySource(InventorySourceType.NonImplementedFeatureTODO);
        result.mContainerId = containerId;
        return result;
    }

    public static InventorySource fromInvalid() {
        return new InventorySource(InventorySourceType.InvalidInventory);
    }

    public final InventorySourceType getType() {
        return this.mType;
    }

    public final ContainerID getContainerId() {
        return this.mContainerId;
    }

    public final InventorySourceFlags getFlags() {
        return this.mFlags;
    }

    public enum InventorySourceFlags {
        NoFlag,
        WorldInteraction_Random,
        UNKNOWN
    }
}
