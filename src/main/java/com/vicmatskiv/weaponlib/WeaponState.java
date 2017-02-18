package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.state.ManagedState;

import io.netty.buffer.ByteBuf;

public enum WeaponState implements ManagedState<WeaponState> {

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
	public WeaponState permitRequested() {
		return permitRequestedState;
	}

	@Override
	public void init(ByteBuf buf) {
		// not need to initialize anything, type registry will take care of everything
	}

	@Override
	public void serialize(ByteBuf buf) {
		// not need to serialize anything, parent type registry should take care of it
	}
	
	static {
		TypeRegistry.getInstance().register(WeaponState.class);
	}
	
}
