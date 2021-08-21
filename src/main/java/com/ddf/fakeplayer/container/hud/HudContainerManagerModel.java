package com.ddf.fakeplayer.container.hud;

import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.container.ContainerID;
import com.ddf.fakeplayer.container.ContainerItemStack;
import com.ddf.fakeplayer.container.ContainerManagerModel;
import com.ddf.fakeplayer.container.ContainerType;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.ArrayList;

public class HudContainerManagerModel extends ContainerManagerModel {
    ArrayList<ContainerItemStack> mLastSlots;

    public HudContainerManagerModel(ContainerID containerId, Player player) {
        super(containerId, player);
        this.mLastSlots = new ArrayList<>();
        super.setContainerType(ContainerType.CONTAINER);
        this.init();
    }

    @NotImplemented
    @Override
    public final void init() {
    }

    public ArrayList<ContainerItemStack> getItems() {
        return new ArrayList<>();
    }
}
