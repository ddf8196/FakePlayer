package com.ddf.fakeplayer.level;

import com.ddf.fakeplayer.item.ItemRegistry;
import com.ddf.fakeplayer.network.PacketSender;
import com.nukkitx.protocol.bedrock.packet.TickSyncPacket;

public class MultiPlayerLevel extends Level {
    private long mLastSyncServerTick = 0;

    public MultiPlayerLevel(ItemRegistry itemRegistry, PacketSender packetSender) {
        super(itemRegistry, null, null, null, true, null, null, null, null, null, null);
        this.mPacketSender = packetSender;
    }

    public void tick() {
        if (!this.mIsFinishedInitializing)
            return;
        super.tick();
        this._trySyncServerTick();

//        if (this.getGameRules().getBool(1)) {
//            this.setTime(this.getTime() + 1);
//        }
//        this._handleDelayedEntityDeletion();
    }

    private void _trySyncServerTick() {
        long now = System.currentTimeMillis();
        if (now - this.mLastSyncServerTick >= 1800000)
            this._forceSyncServerTick(now);
    }

    private void _forceSyncServerTick(long time) {
        this.mLastSyncServerTick = time;
        TickSyncPacket packet = new TickSyncPacket();
        packet.setRequestTimestamp(this.getCurrentTick());
        super.getPacketSender().send(packet);
    }
}
