package com.jimholden.conomy.clans.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ConcurrentExecutionManager {
    //Limit the number of active threads so we don't run the machine out of memory
    private static final ExecutorService EXECUTOR_SERVICE_ESSENTIAL = Executors.newFixedThreadPool(256);
    private static final ExecutorService EXECUTOR_SERVICE_NONESSENTIAL = Executors.newFixedThreadPool(128);

    public static void run(Runnable runnable) {
        if(!EXECUTOR_SERVICE_ESSENTIAL.isShutdown())
            EXECUTOR_SERVICE_ESSENTIAL.execute(runnable);
    }

    public static void runKillable(Runnable runnable) {
        if(!EXECUTOR_SERVICE_NONESSENTIAL.isShutdown())
            EXECUTOR_SERVICE_NONESSENTIAL.execute(runnable);
    }

    public static void waitForCompletion() throws InterruptedException {
        EXECUTOR_SERVICE_ESSENTIAL.shutdown();
        EXECUTOR_SERVICE_NONESSENTIAL.shutdownNow();
        EXECUTOR_SERVICE_ESSENTIAL.awaitTermination(1, TimeUnit.DAYS);
    }
}
