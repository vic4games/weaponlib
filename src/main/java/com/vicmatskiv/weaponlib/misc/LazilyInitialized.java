package com.vicmatskiv.weaponlib.misc;

import java.util.function.Function;

public class LazilyInitialized<T, I> {
    private Function<I, T> initializer;
    
    public LazilyInitialized() {}
    
    LazilyInitialized(Function<I, T> initializer) {
        this.initializer = initializer;
    }

    private T resolved;
    public T get(I i) {
        if(initializer == null) {
            return null;
        }
        if(resolved == null) {
            resolved = initializer.apply(i);
        }
        return resolved;
    }
}