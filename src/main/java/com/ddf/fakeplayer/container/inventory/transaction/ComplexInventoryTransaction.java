package com.ddf.fakeplayer.container.inventory.transaction;

import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.container.inventory.InventoryAction;
import com.ddf.fakeplayer.container.inventory.InventorySource;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.ArrayList;
import java.util.Map;

public class ComplexInventoryTransaction {
    ComplexInventoryTransaction.Type mType;
    InventoryTransaction mTransaction;

    public ComplexInventoryTransaction(Type type) {
        this.mType = type;
        this.mTransaction = new InventoryTransaction();
    }

    public ComplexInventoryTransaction(ComplexInventoryTransaction.Type type, final InventoryTransaction transaction) {
        this.mType = type;
        this.mTransaction = new InventoryTransaction(transaction);
    }

    @NotImplemented
    public static ComplexInventoryTransaction fromType(ComplexInventoryTransaction.Type type, final InventoryTransaction transaction) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case ItemUseTransaction:
                return new ItemUseInventoryTransaction(transaction);
            case ItemUseOnEntityTransaction:
                return new ItemUseOnActorInventoryTransaction(transaction);
            case ItemReleaseTransaction:
                return new ItemReleaseInventoryTransaction(transaction);
            default:
                return new ComplexInventoryTransaction(type, transaction);
        }
    }

    public InventoryTransactionError handle(Player player, boolean isSenderAuthority) {
        this.mTransaction._logTransaction(player.getLevel().isClientSide());
        InventoryTransactionError error = this.mTransaction.verifyFull(player, isSenderAuthority);
        if (error == InventoryTransactionError.NoError) {
            for (Map.Entry<InventorySource, ArrayList<InventoryAction>> entry : this.mTransaction.getAllActions().entrySet()) {
                for (InventoryAction action : entry.getValue()) {
                    player.getTransactionManager().addExpectedAction(action);
                }
            }
            InventoryTransactionError executeError = this.mTransaction.executeFull(player, isSenderAuthority);
            if (executeError == InventoryTransactionError.NoError) {
                if (this.getType() == Type.InventoryMismatch_0)
                    player.sendInventory(true);
                return InventoryTransactionError.NoError;
            } else {
                return executeError;
            }
        } else {
            return error;
        }
    }

    public final ComplexInventoryTransaction.Type getType() {
        return this.mType;
    }

    public final InventoryTransaction getInventoryTransaction() {
        return this.mTransaction;
    }

    public enum Type {
        NormalTransaction,
        InventoryMismatch_0,
        ItemUseTransaction,
        ItemUseOnEntityTransaction,
        ItemReleaseTransaction
    }
}
