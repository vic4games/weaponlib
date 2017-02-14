package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.network.UniversalObject;
import com.vicmatskiv.weaponlib.state.Aspect;
import com.vicmatskiv.weaponlib.state.ManagedState;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.StateManager;

public class CommonWeaponAspect implements Aspect {
//	static final ManagedState READY = new ManagedState();

	protected StateManager stateManager;
	protected PermitManager<UniversalObject> permitManager;
	
	@Override
	public void setStateManager(StateManager stateManager) {
		this.stateManager = stateManager;
	}

	@Override
	public void setPermitManager(PermitManager<UniversalObject> permitManager) {
		this.permitManager = permitManager;
	}
}
