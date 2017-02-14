package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.state.ManagedState;

public enum WeaponState implements ManagedState {

	READY, 
	LOAD_REQUESTED, 
	LOAD(LOAD_REQUESTED), 
	UNLOAD_REQUESTED, 
	UNLOAD(UNLOAD_REQUESTED),
	FIRING, 
	STOPPED, 
	EJECT_SPENT_ROUND_REQUIRED, 
	EJECTED_SPENT_ROUND;

	private WeaponState permitRequestedState;
	
	private WeaponState() {
		this(null);
	}
	
	private WeaponState(WeaponState permitRequestedState) {
		this.permitRequestedState = permitRequestedState;
	}
	
	@Override
	public ManagedState permitRequested() {
		return permitRequestedState;
	}
	
	
}
