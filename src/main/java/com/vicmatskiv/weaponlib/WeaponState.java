package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.state.ManagedState;

import io.netty.buffer.ByteBuf;

public enum WeaponState implements ManagedState<WeaponState> {

	READY(false), 
	LOAD_REQUESTED, 
	LOAD(null, LOAD_REQUESTED, null, true), 
	
	UNLOAD_PREPARING, 
	UNLOAD_REQUESTED, 
	UNLOAD(UNLOAD_PREPARING, UNLOAD_REQUESTED, READY, true),
	
	FIRING,
	RECOILED,
	PAUSED,
	EJECT_REQUIRED,
	EJECTING,
	
//	STOPPED, 
//	EJECT_SPENT_ROUND_REQUIRED, 
//	EJECTED_SPENT_ROUND,
	
	MODIFYING;

	private WeaponState preparingPhase;
	
	private WeaponState permitRequestedPhase;
	
	private WeaponState commitPhase;
	
	private boolean isTransient;
	
	private WeaponState() {
		this(null, null, null, true);
	}
	
	private WeaponState(boolean isTransient) {
		this(null, null, null, isTransient);
	}
	
//	private WeaponState(WeaponState permitRequestedState, WeaponState transactionFinalState) {
//		this(permitRequestedState, transactionFinalState, true);
//	}
	
	private WeaponState(WeaponState preparingPhase, WeaponState permitRequestedState, WeaponState transactionFinalState, boolean isTransient) {
		this.preparingPhase = preparingPhase;
		this.permitRequestedPhase = permitRequestedState;
		this.commitPhase = transactionFinalState;
		this.isTransient = isTransient;
	}
	
	@Override
	public boolean isTransient() {
		return isTransient;
	}
	
	@Override
	public WeaponState preparingPhase() {
		return preparingPhase;
	}
	
	@Override
	public WeaponState permitRequestedPhase() {
		return permitRequestedPhase;
	}
	

	@Override
	public WeaponState commitPhase() {
		return commitPhase;
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
