package com.ddf.fakeplayer.entity;

import com.nukkitx.protocol.bedrock.data.AttributeData;

public class Attribute {
    private float base;
    private float current;
    private float max;
    private String name;

    public Attribute() {}

    public Attribute(AttributeData data) {
        base = data.getMinimum();
        current = data.getValue();
        max = data.getMaximum();
        name = data.getName();
    }
    public float getBase() {
        return base;
    }

    public void setBase(float base) {
        this.base = base;
    }

    public float getCurrent() {
        return current;
    }

    public void setCurrent(float current) {
        this.current = current;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
