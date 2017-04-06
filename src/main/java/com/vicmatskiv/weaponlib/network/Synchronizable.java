package com.vicmatskiv.weaponlib.network;

public interface Synchronizable<T, SyncContext> {

	public T sync(SyncContext context);
}
