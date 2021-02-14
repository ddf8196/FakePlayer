package com.ddf.fakeplayer.entity;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.AttributeData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Entity {
    public static final String LUCK = "minecraft:luck";
    public static final String HEALTH = "minecraft:health";
    public static final String ABSORPTION = "minecraft:absorption";
    public static final String MOVEMENT = "minecraft:movement";
    public static final String UNDERWATER_MOVEMENT = "minecraft:underwater_movement";
    public static final String LAVA_MOVEMENT = "minecraft:lava_movement";
    public static final String FOLLOW_RANGE = "minecraft:follow_range";
    public static final String ATTACK_DAMAGE = "minecraft:attack_damage";

    protected Vector3f position = Vector3f.ZERO;
    protected Vector3f motion = Vector3f.ZERO;
    protected Vector3f rotation  = Vector3f.ZERO;

    private String identifier;
    private int entityType;
    protected long runtimeEntityId;
    private long uniqueEntityId;
    private final Map<String, Attribute> attributes = new HashMap<>();

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getMotion() {
        return motion;
    }

    public void setMotion(Vector3f motion) {
        this.motion = motion;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public long getRuntimeEntityId() {
        return runtimeEntityId;
    }

    public void setRuntimeEntityId(long runtimeEntityId) {
        this.runtimeEntityId = runtimeEntityId;
    }

    public long getUniqueEntityId() {
        return uniqueEntityId;
    }

    public void setUniqueEntityId(long uniqueEntityId) {
        this.uniqueEntityId = uniqueEntityId;
    }


    public boolean isAlive() {
        return getAttributeValue(HEALTH) > 0.0f;
    }

    public float getAttributeValue(String name) {
        return attributes.get(name).getCurrent();
    }

    public Attribute getAttribute(String name) {
        return attributes.get(name);
    }

    public void setAttribute(Attribute attribute) {
        attributes.put(attribute.getName(), attribute);
    }

    public void setAttribute(String name, float current) {
        Attribute attribute = getAttribute(name);
        attribute.setCurrent(current);
    }

    public void setAttribute(String name, float base, float current, float max) {
        Attribute attribute = getAttribute(name);
        attribute.setBase(base);
        attribute.setCurrent(current);
        attribute.setMax(max);
    }

    public Collection<Attribute> getAttributes() {
        return attributes.values();
    }
}
