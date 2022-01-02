package com.ddf.fakeplayer.util.threading;

public class LockGuard<T extends Lock> implements AutoCloseable {
    private final T lock;

    public LockGuard(T lock) {
        this.lock = lock;
        lock.lock();
    }

    @Override
    public void close() {
        lock.unlock();
    }
}
