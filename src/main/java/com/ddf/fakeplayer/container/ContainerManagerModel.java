package com.ddf.fakeplayer.container;

import com.ddf.fakeplayer.actor.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public abstract class ContainerManagerModel implements IContainerManager {
    protected Player mPlayer;
    private ArrayList<ContainerItemStack> mLastSlots;
    private ContainerID mContainerId;
    private ContainerType mContainerType;
    private Function<ContainerManagerModel, Void> mInformControllerOfDestructionCallback;
    private HashMap<String, ContainerModel> mContainers;

    public ContainerManagerModel(ContainerID containerId, Player player) {
        this.mPlayer = player;
        this.mLastSlots = new ArrayList<>();
        this.mContainerId = containerId;
        this.mContainerType = ContainerType.NONE_2;
        this.mContainers = new HashMap<>();
    }

    public void init() {
    }

    public void setContainerType(ContainerType type) {
        this.mContainerType = type;
    }

    public abstract ArrayList<ContainerItemStack> getItems();
}
