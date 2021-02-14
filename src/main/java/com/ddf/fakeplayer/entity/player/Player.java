package com.ddf.fakeplayer.entity.player;

import com.ddf.fakeplayer.Client;
import com.ddf.fakeplayer.entity.Entity;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.data.AuthoritativeMovementMode;

import java.util.*;

public class Player extends Entity {
    public static final String HUNGER = "minecraft:player.hunger";
    public static final String EXHAUSTION = "minecraft:player.exhaustion";
    public static final String SATURATION = "minecraft:player.saturation";
    public static final String EXPERIENCE = "minecraft:player.experience";
    public static final String LEVEL = "minecraft:player.level";

    private final Client client;
    private String name;
    private UUID uuid;

    private Vector3i spawnPosition  = Vector3i.ZERO;
    private boolean initialized = false;

    private AuthoritativeMovementMode movementMode;

    public Player(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
        this.client = null;
    }

    public Player(String name, UUID uuid, Client client) {
        this.name = name;
        this.uuid = uuid;
        this.client = client;
    }

    public boolean isRemote() {
        return client == null;
    }

    public void setSpawnPosition(Vector3i spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AuthoritativeMovementMode getMovementMode() {
        return movementMode;
    }

    public void setMovementMode(AuthoritativeMovementMode movementMode) {
        this.movementMode = movementMode;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
