package com.vicmatskiv.weaponlib.state;

public interface ManagedState {

	public ManagedState permitRequested();
	
	public default boolean isTransient() {
		return false;
	}
}
