package com.vicmatskiv.weaponlib.state;

public interface StateContext/*<T extends State>*/ {

	public <T extends ManagedState> StateContainer<T> getStateContainer();
}
