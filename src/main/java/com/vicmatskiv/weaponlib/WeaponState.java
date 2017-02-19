package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.state.ManagedState;

import io.netty.buffer.ByteBuf;

public enum WeaponState implements ManagedState<WeaponState> {

	READY(false), 
	LOAD_REQUESTED, 
	LOAD(LOAD_REQUESTED, null), 
	UNLOAD_REQUESTED, 
	UNLOAD(UNLOAD_REQUESTED, READY),
	FIRING, 
	STOPPED, 
	EJECT_SPENT_ROUND_REQUIRED, 
	EJECTED_SPENT_ROUND;

	private WeaponState permitRequestedState;
	
	private WeaponState transactionFinalState;
	
	private boolean isTransient;
	
	private WeaponState() {
		this(null, null);
	}
	
	private WeaponState(boolean isTransient) {
		this(null, null, isTransient);
	}
	
	private WeaponState(WeaponState permitRequestedState, WeaponState transactionFinalState) {
		this(permitRequestedState, transactionFinalState, true);
	}
	
	private WeaponState(WeaponState permitRequestedState, WeaponState transactionFinalState, boolean isTransient) {
		this.permitRequestedState = permitRequestedState;
		this.transactionFinalState = transactionFinalState;
		this.isTransient = isTransient;
	}
	
	@Override
	public boolean isTransient() {
		return isTransient;
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

	@Override
	public WeaponState transactionFinalState() {
		return transactionFinalState;
	}
	
}
