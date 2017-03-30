package com.vicmatskiv.weaponlib;

import java.util.function.Function;

public class Container<T, I> {
    Function<I, T> initializer;
    Container() {}
    Container(Function<I, T> initializer) {
        this.initializer = initializer;
    }

    T resolved;
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
