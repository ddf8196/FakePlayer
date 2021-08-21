package com.ddf.fakeplayer.actor.player;

import com.ddf.fakeplayer.container.inventory.transaction.ComplexInventoryTransaction;
import com.ddf.fakeplayer.container.inventory.transaction.InventoryTransaction;
import com.ddf.fakeplayer.level.GameType;
import com.ddf.fakeplayer.level.Level;

import java.util.UUID;

public class RemotePlayer extends Player {
    public RemotePlayer(Level level, String name, UUID uuid) {
        super(level, null, GameType.Undefined, null, (byte) 0, uuid, "", null, "", "");
        super.setName(name);
    }

    @Override
    public void sendInventoryTransaction(InventoryTransaction transaction) {
    }

    @Override
    public void sendComplexInventoryTransaction(ComplexInventoryTransaction transaction) {
    }

    @Override
    public boolean isLocalPlayer() {
        return false;
    }

    @Override
    public void normalTick() {
        super.normalTick();
    }
}
