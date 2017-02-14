package com.vicmatskiv.weaponlib.state;

import com.vicmatskiv.weaponlib.network.UniversalObject;

public interface Aspect {

	public void setStateManager(StateManager stateManager);

	public void setPermitManager(PermitManager<UniversalObject> permitManager);
}
