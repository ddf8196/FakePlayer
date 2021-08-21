package com.ddf.fakeplayer.container.inventory.transaction;

import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.container.inventory.InventoryAction;

import java.util.ArrayList;

public class InventoryTransactionManager {
    private final Player mPlayer;
    private InventoryTransaction mCurrentTransaction = null;
    private final ArrayList<InventoryAction> mExpectedActions = new ArrayList<>();

    public InventoryTransactionManager(Player player) {
        this.mPlayer = player;
    }

    public final void _logExpectedActions() {
    }

    public final void reset() {
        this.mCurrentTransaction = null;
        this.resetExpectedActions();
    }

    public final void resetExpectedActions() {
        this.mExpectedActions.clear();
    }

    public final void addAction(InventoryAction action) {
        if (!this.checkActionExpected(action)) {
            if (!action.getFromItem().equals(action.getToItem())){
                if (this.mCurrentTransaction == null) {
                    this.mCurrentTransaction = new InventoryTransaction(this.mPlayer.getLevel().getItemRegistry());
                }
                this.mCurrentTransaction.addAction(action);
                if (this.mCurrentTransaction.verifyBalance()) {
                    this.mPlayer.sendInventoryTransaction(this.mCurrentTransaction);
                    this.mCurrentTransaction = null;
                }
            }
        }
    }

    public final void addExpectedAction(final InventoryAction action) {
        this.mExpectedActions.add(action);
    }

    public final boolean checkActionExpected(final InventoryAction action) {
        if (this.mExpectedActions.contains(action)) {
            this.mExpectedActions.remove(action);
            return true;
        }
        return false;
    }

    public final void forceBalanceTransaction() {
        if (this.mCurrentTransaction != null) {
            this.mCurrentTransaction.forceBalanceTransaction();
            if (this.mCurrentTransaction.verifyBalance()) {
                this.mPlayer.sendInventoryTransaction(this.mCurrentTransaction);
                this.mCurrentTransaction = null;
            }
        }
    }

    public final InventoryTransaction getCurrentTransaction() {
        return this.mCurrentTransaction;
    }
}
