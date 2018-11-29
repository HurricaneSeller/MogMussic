package com.example.moan.mogmussic.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Pool {
    private final Executor singleThread;
    private final Executor cachedThread;

    private Pool(Executor singleThread, Executor cachedThread) {
        this.singleThread = singleThread;
        this.cachedThread = cachedThread;
    }

    public Pool() {
        this(Executors.newSingleThreadExecutor(), Executors.newCachedThreadPool());
    }
    public Executor getSingleThread() {
        return singleThread;
    }
    public Executor getCachedThread() {
        return cachedThread;
    }


}
