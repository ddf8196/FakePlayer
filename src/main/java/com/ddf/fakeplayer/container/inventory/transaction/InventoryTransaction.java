package com.ddf.fakeplayer.container.inventory.transaction;

import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.container.inventory.PlayerInventoryProxy;
import com.ddf.fakeplayer.container.ContainerID;
import com.ddf.fakeplayer.container.inventory.InventoryAction;
import com.ddf.fakeplayer.container.inventory.InventorySource;
import com.ddf.fakeplayer.container.inventory.InventorySourceType;
import com.ddf.fakeplayer.container.slot.ArmorSlot;
import com.ddf.fakeplayer.container.slot.PlayerUISlot;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InventoryTransaction {
    private final HashMap<InventorySource, ArrayList<InventoryAction>> mActions = new HashMap<>();
    private final ArrayList<InventoryTransactionItemGroup> mContents = new ArrayList<>();

    public InventoryTransaction() {
    }

    public InventoryTransaction(final InventoryTransaction a2) {
        if (a2 == null) {
            return;
        }
        this.mActions.putAll(a2.mActions);
        this.mContents.addAll(a2.mContents);
    }

    public final void _logTransaction(boolean isClientSide) {
    }

    private void _dropCreatedItems(Player p) {
        for (Map.Entry<InventorySource, ArrayList<InventoryAction>> entry : this.mActions.entrySet()) {
            for (InventoryAction action : entry.getValue()) {
                if (action.getSource().getContainerId() == ContainerID.CONTAINER_ID_PLAYER_ONLY_UI && action.getSlot() == PlayerUISlot.CreatedItemOutput.getValue()) {
                    int toDropCount = action.getFromItem().getStackSize() - action.getToItem().getStackSize();
                    if (toDropCount > 0) {
                        ItemStack toDrop = new ItemStack(action.getFromItem());
                        toDrop.setStackSize(toDropCount);
                        InventoryTransactionManager TransactionManager = p.getTransactionManager();
                        TransactionManager.addAction(new InventoryAction(InventorySource.fromWorldInteraction(InventorySource.InventorySourceFlags.NoFlag), 1, toDrop, ItemStack.EMPTY_ITEM));
                        p.drop(toDrop, false);
                        ItemStack currentItem = p.getPlayerUIItem(PlayerUISlot.CreatedItemOutput);
                        ItemStack other = action.getFromItem();
                        if (currentItem.isStackable(other)) {
                            ItemStack toLeave = new ItemStack(currentItem);
                            int toLeaveSize = currentItem.getStackSize() - toDropCount;
                            if (toLeaveSize < 0)
                                toLeaveSize = currentItem.getStackSize();
                            if (toLeaveSize != action.getToItem().getStackSize()) {
                                toLeaveSize = action.getToItem().getStackSize();
                            }
                            toLeave.setStackSize(toLeaveSize);
                            p.getTransactionManager().addAction(new InventoryAction(InventorySource.fromContainerWindowID(ContainerID.CONTAINER_ID_INVENTORY), PlayerUISlot.CreatedItemOutput.getValue(), currentItem, toLeave));
                            p.setPlayerUIItem(PlayerUISlot.CreatedItemOutput, toLeave);
                        }
                    }
                }
            }
        }
    }

    public final void addAction(final InventoryAction action) {
        if (!this.mActions.containsKey(action.getSource())) {
            this.mActions.put(action.getSource(), new ArrayList<>());
        }
        ArrayList<InventoryAction> actions = this.mActions.get(action.getSource());
        for (int i = 0; i < actions.size(); i++) {
            InventoryAction existingAction = actions.get(i);
            if (existingAction.getSlot() == action.getSlot()) {
                if (action.getFromItem().equals(existingAction.getToItem())) {
                    actions.set(i, new InventoryAction(existingAction.getSource(), existingAction.getSlot(), existingAction.getFromItem(), action.getToItem()));
                    this.addActionToContent(action);
                    return;
                }
            }
        }
        actions.add(action);
        this.addActionToContent(action);
    }

    public final void addActionToContent(final InventoryAction action) {
        ItemStack from = action.getFromItem();
        ItemStack to = action.getToItem();
        if (to.isEmptyStack()) {
            this.addItemToContent(from, -from.getStackSize());
        } else {
            if (!from.isEmptyStack()) {
                this.addItemToContent(from, -from.getStackSize());
            }
            this.addItemToContent(to, to.getStackSize());
        }
    }

    public final void addItemToContent(final ItemStack item, int count) {
        for (InventoryTransactionItemGroup c : this.mContents) {
            if (c.add(item, count))
                return;
        }
        this.mContents.add(new InventoryTransactionItemGroup(item, count));
    }

    public final HashMap<InventorySource, ArrayList<InventoryAction>> getAllActions() {
        return this.mActions;
    }

    @NotImplemented
    public final VerifyFunction getVerifyFunction(final InventorySource source) {
        InventorySourceType Type = source.getType();
        if (Type != InventorySourceType.ContainerInventory) {
            switch (Type) {
                case WorldInteraction:
                    return (player, action, isSenderAuthority) -> {
                        if (action.getSlot() == 0 && !action.getFromItem().toBoolean() && action.getToItem().toBoolean())
                            return InventoryTransactionError.NoError;
                        if (action.getSlot() != 1 || !action.getFromItem().toBoolean() || action.getToItem().toBoolean())
                            return InventoryTransactionError.Unknown_4;
                        if (isSenderAuthority)
                            return InventoryTransactionError.NoError;
                        return InventoryTransactionError.AuthorityMismatch;
                    };
                case CreativeInventory:
                    return (player, action, isSenderAuthority) -> {
                        if (!isSenderAuthority && !player.isInCreativeMode())
                            return InventoryTransactionError.AuthorityMismatch;
                        if (action.getSlot() == 0) {
                            if (!action.getFromItem().toBoolean() && action.getToItem().toBoolean()) {
                                return InventoryTransactionError.NoError;
                            }
                        }
                        if (action.getSlot() != 1) {
                            return InventoryTransactionError.Unknown_4;
                        }
                        if (!action.getFromItem().toBoolean() || action.getToItem().toBoolean()) {
                            return InventoryTransactionError.Unknown_4;
                        }
//                      ------------------------------------------
//                      ------------------------------------------
//                      ------------------------------------------
//                      ------------------------------------------
//                      ------------------------------------------
//                      ------------------------------------------
                        return InventoryTransactionError.NoError;
                    };
                case NonImplementedFeatureTODO:
                    return (player, action, isSenderAuthority) -> InventoryTransactionError.NoError;
                default:
                    return (player, action, isSenderAuthority) -> InventoryTransactionError.Unknown_4;
            }
        } else {
            ContainerID ContainerId = source.getContainerId();
            if (ContainerId == ContainerID.CONTAINER_ID_INVENTORY) {
                return (player, action, isSenderAuthority) -> {
                    //---------------------------------------
                    //---------------------------------------
                    //---------------------------------------
                    return InventoryTransactionError.NoError;
                };
            } else {
               return (player, action, isSenderAuthority) -> {
                   PlayerInventoryProxy Supplies = player.getSupplies();
                   InventorySource Source = action.getSource();
                   if (action.getSlot() >= Supplies.getContainerSize(Source.getContainerId()))
                       return InventoryTransactionError.SizeMismatch;
                   ItemStack currentItem = Supplies.getItem(action.getSlot(), action.getSource().getContainerId());
                   if (currentItem.matches(action.getFromItem()) || isSenderAuthority)
                       return InventoryTransactionError.NoError;
                   return InventoryTransactionError.SourceItemMismatch;
               };
            }
        }
    }

    @NotImplemented
    public final ExecuteFunction getExecuteFunction(final InventorySource source) {
        InventorySourceType Type = source.getType();
        if (Type != InventorySourceType.ContainerInventory) {
            switch (Type) {
                case WorldInteraction:
                    return (player, action) -> {
                        if (action.getSlot() != 0 || action.getFromItem().toBoolean() || !action.getToItem().toBoolean())
                            return action.getSlot() == 1 && action.getFromItem().toBoolean() && !action.getToItem().toBoolean()
                                    ? InventoryTransactionError.NoError : InventoryTransactionError.Unknown_4;
                        player.drop(action.getToItem(), action.getSource().getFlags() != InventorySource.InventorySourceFlags.NoFlag);
                        return InventoryTransactionError.NoError;
                    };
                case CreativeInventory:
                    return (player, action) -> {
                        if (action.getSlot() != 0 || action.getFromItem().toBoolean() || !action.getToItem().toBoolean()) {
                            if (action.getSlot() == 1 && action.getFromItem().toBoolean() && !action.getToItem().toBoolean()){
                                return InventoryTransactionError.NoError;
                            }
                            return InventoryTransactionError.Unknown_4;
                        } else {
                            return InventoryTransactionError.NoError;
                        }
                    };
                case NonImplementedFeatureTODO:
                    return (player, action) -> InventoryTransactionError.NoError;
                default:
                    return (player, action) -> InventoryTransactionError.Unknown_4;
            }
        } else {
            ContainerID containerId = source.getContainerId();
            if (containerId != ContainerID.CONTAINER_ID_INVENTORY) {
                switch (containerId) {
                    case CONTAINER_ID_OFFHAND:
                        return (player, action) -> {
                            player.setOffhandSlot(action.getToItem());
                            return InventoryTransactionError.NoError;
                        };
                    case CONTAINER_ID_ARMOR:
                        return (player, action) -> {
                            player.setArmor(ArmorSlot.toArmorSlot(action.getSlot()), action.getToItem());
                            return InventoryTransactionError.NoError;
                        };
                    case CONTAINER_ID_PLAYER_ONLY_UI:
                        return (player, action) -> {
                            if (action.getSlot() < 0x33) {
                                if (player.isClient() && action.getSlot() == PlayerUISlot.CreatedItemOutput.getValue()) {
                                    ItemStack currentItem = player.getPlayerUIItem(PlayerUISlot.CreatedItemOutput);
                                    ItemStack toItem = action.getToItem();
                                    player.getTransactionManager().addAction(
                                            new InventoryAction(
                                                    InventorySource.fromContainerWindowID(ContainerID.CONTAINER_ID_PLAYER_ONLY_UI),
                                                    PlayerUISlot.CreatedItemOutput.getValue(),
                                                    currentItem,
                                                    toItem
                                            ));
                                } else {
                                    player.setPlayerUIItem(PlayerUISlot.getByValue(action.getSlot()), action.getToItem());
                                }
                            }
                            return InventoryTransactionError.NoError;
                        };
                    default:
                        return (player, action) -> {
                            //---------------------------------------
                            //---------------------------------------
                            //---------------------------------------
                            return InventoryTransactionError.NoError;
                        };
                }
            } else {
                return (player, action) -> {
                    player.getSupplies().setItem(action.getSlot(), action.getToItem(), action.getSource().getContainerId());
                    return InventoryTransactionError.NoError;
                };
            }
        }
    }

    public final InventoryTransactionError executeFull(Player p, boolean isSenderAuthority) {
        for (Map.Entry<InventorySource, ArrayList<InventoryAction>> entry : this.mActions.entrySet()) {
            InventorySource source = entry.getKey();
            VerifyFunction checkFunction = this.getVerifyFunction(source);
            ExecuteFunction executeFunction = this.getExecuteFunction(source);
            for (InventoryAction action : entry.getValue()) {
                InventoryTransactionError error  = checkFunction.call(p, action, isSenderAuthority);
                if (error != InventoryTransactionError.NoError) {
                    return error;
                }
                InventoryTransactionError error2 = executeFunction.call(p, action);
                if (error2 != InventoryTransactionError.NoError) {
                    return error2;
                }
            }
        }
        return InventoryTransactionError.NoError;
    }

    public final boolean verifyBalance() {
        for (InventoryTransactionItemGroup item : mContents) {
            if (item.getCount() != 0 || item.hasOverflow()) {
                return false;
            }
        }
        return true;
    }

    public final InventoryTransactionError verifyFull(Player p, boolean isSenderAuthority) {
        if (this.verifyBalance()) {
            for (Map.Entry<InventorySource, ArrayList<InventoryAction>> source : this.mActions.entrySet()) {
                VerifyFunction checkFunction = this.getVerifyFunction(source.getKey());
                for (InventoryAction action : source.getValue()) {
                    InventoryTransactionError error = checkFunction.call(p, action, isSenderAuthority);
                    if ( error != InventoryTransactionError.NoError) {
                        this._dropCreatedItems(p);
                        return error;
                    }
                }
            }
            return InventoryTransactionError.NoError;
        } else {
            return InventoryTransactionError.BalanceMismatch;
        }
    }

    @SuppressWarnings("all")
    public void forceBalanceTransaction() {
        int size = this.mContents.size();
        for (int index = 0; index < size; ++index) {
            InventoryTransactionItemGroup item = this.mContents.get(index);
            if (!item.hasOverflow() && item.getCount() != 0) {
                if (item.getCount() <= 0) {
                    int count_0 = -item.getCount();
                    ItemStack instance_0 = item.getItemInstance();
                    instance_0.setStackSize(instance_0.getMaxStackSize());
                    for (int i = 0; i < count_0 / item.getItemInstance().getMaxStackSize(); i++) {
                        this.addAction(new InventoryAction(InventorySource.fromCreativeInventory(), 0, ItemStack.EMPTY_ITEM, instance_0));
                        count_0 -= instance_0.getMaxStackSize();
                    }
                    if (count_0 != 0) {
                        instance_0.setStackSize(count_0);
                        this.addAction(new InventoryAction(InventorySource.fromCreativeInventory(), 0, ItemStack.EMPTY_ITEM, instance_0));
                    }
                } else {
                    int count = item.getCount();
                    int v16 = count / item.getItemInstance().getMaxStackSize();
                    ItemStack instance = item.getItemInstance();
                    instance.setStackSize(instance.getMaxStackSize());
                    for (int i = 0; i < v16; i++) {
                        this.addAction(new InventoryAction(InventorySource.fromCreativeInventory(), 1, instance, ItemStack.EMPTY_ITEM));
                        count -= instance.getMaxStackSize();
                    }
                    if (count != 0) {
                        instance.setStackSize(count);
                        this.addAction(new InventoryAction(InventorySource.fromCreativeInventory(), 1, instance, ItemStack.EMPTY_ITEM));
                    }
                }
            }
        }
    }

    @FunctionalInterface
    public interface ExecuteFunction {
        InventoryTransactionError call(Player player, InventoryAction action);
    }

    @FunctionalInterface
    public interface VerifyFunction {
        InventoryTransactionError call(Player player, final InventoryAction action, boolean isSenderAuthority);
    }
}
