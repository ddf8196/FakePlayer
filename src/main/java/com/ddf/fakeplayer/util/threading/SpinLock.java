package com.ddf.fakeplayer.util.threading;

import java.util.concurrent.atomic.AtomicReference;

public class SpinLock implements Lock {
    private final AtomicReference<Thread> mOwnerThread = new AtomicReference<>(null);
    private /*uint32_t*/long mOwnerRefCount = 0;

    private boolean _try_lock(Thread thread) {
        if (thread == this.mOwnerThread.get()) {
            ++this.mOwnerRefCount;
            return true;
        }
        if (this.mOwnerThread.compareAndSet(null, thread)) {
            this.mOwnerRefCount = 1;
            return true;
        }
        return false;
    }

    public void lock() {
        int loopCount = 3000;
        while (!this._try_lock(Thread.currentThread())){
            if (loopCount > 0)
                --loopCount;
            else
                Thread.yield();
        }
    }

    public boolean try_lock() {
        return this._try_lock(Thread.currentThread());
    }

    public void unlock() {
        if (this.mOwnerThread.get() != Thread.currentThread() || this.mOwnerRefCount == 0)
            throw new RuntimeException("Operation not permitted");
        if (this.mOwnerRefCount == 1) {
            this.mOwnerRefCount = 0;
            this.mOwnerThread.set(null);;
        } else {
            --this.mOwnerRefCount;
        }
    }
}
