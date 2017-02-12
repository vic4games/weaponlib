package com.vicmatskiv.weaponlib.state;

public interface StateContainer<T extends ManagedState> {

	public boolean compareAndSetState(ManagedState expectedState, ManagedState updateToState);

	public T get();
}
