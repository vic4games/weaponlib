package com.vicmatskiv.weaponlib.state;

public interface ManagedStateContainer<T extends ManagedState> {

	public boolean compareAndSetManagedState(ManagedState expectedState, ManagedState updateToState);

	public T getManagedState();
	
	public long getLastManagedStateUpdateTimestamp();
}
