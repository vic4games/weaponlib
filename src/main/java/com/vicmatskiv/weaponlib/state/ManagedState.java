package com.vicmatskiv.weaponlib.state;

import com.vicmatskiv.weaponlib.network.UniversallySerializable;

public interface ManagedState<T extends ManagedState<T>> extends UniversallySerializable {

	public T permitRequested();
	
	public T transactionFinalState();
	
	public default boolean isTransient() {
		return false;
	}
	
	public int ordinal();
}
