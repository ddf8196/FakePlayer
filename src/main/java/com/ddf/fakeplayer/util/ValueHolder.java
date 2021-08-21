package com.ddf.fakeplayer.util;

public class ValueHolder<T> {
    private T value;

    public ValueHolder(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }
}
