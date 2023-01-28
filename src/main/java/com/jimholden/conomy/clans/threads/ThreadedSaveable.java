package com.jimholden.conomy.clans.threads;

public interface ThreadedSaveable {
    /**
     * Make a save that may block the thread you're on.
     * This is generally bad for performance and should typically be done on a separate thread.
     */
    void blockingSave();
    ThreadedSaveHandler<?> getSaveHandler();

    default void save() {
        getSaveHandler().concurrentSave();
    }

    default void markChanged() {
    	System.out.println("The mark changed has been called.");
        getSaveHandler().markNeedsSave();
    }
}
