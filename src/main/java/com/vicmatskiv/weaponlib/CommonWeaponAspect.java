package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.state.ManagedState;
import com.vicmatskiv.weaponlib.state.StateManager;

public class CommonWeaponAspect implements Aspect {
	static final ManagedState READY = new ManagedState();

	protected StateManager stateManager;
	
	@Override
	public void setStateManager(StateManager stateManager) {
		this.stateManager = stateManager;
	}
}
