package com.ddf.fakeplayer.world;

import com.ddf.fakeplayer.entity.Entity;
import com.ddf.fakeplayer.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class World {
    private List<Entity> entities = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private int time = 0;

    public void addEntity(Entity entity) {
        entities.add(entity);
        if (entity instanceof Player) {
            players.add((Player) entity);
        }
    }

    public Entity getEntityByRuntimeId(long runtimeEntityId) {
        for (Entity entity : entities) {
            if (entity.getRuntimeEntityId() == runtimeEntityId) {
                return entity;
            }
        }
        return null;
    }

    public Entity getEntityByUniqueId(long uniqueEntityId) {
        for (Entity entity : entities) {
            if (entity.getUniqueEntityId() == uniqueEntityId) {
                return entity;
            }
        }
        return null;
    }

    public Player getPlayerByName(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    public void removeEntity(long uniqueEntityId) {
        Entity entity = getEntityByUniqueId(uniqueEntityId);
        if (entity == null) {
            return;
        }
        entities.remove(entity);
        if (entity instanceof Player) {
            players.remove(entity);
        }
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
