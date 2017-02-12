package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.state.Permit;
import com.vicmatskiv.weaponlib.state.RegisteredUuid;
import com.vicmatskiv.weaponlib.state.ManagedState;
import com.vicmatskiv.weaponlib.state.StateContainer;
import com.vicmatskiv.weaponlib.state.StateContext;
import com.vicmatskiv.weaponlib.state.UniversalObject;

import io.netty.buffer.ByteBuf;

public class CommonWeaponAspectContext extends UniversalObject implements StateContext {
	Permit getPermit(com.vicmatskiv.weaponlib.state.ManagedState toState) {
		return null;
	}

	@Override
	protected RegisteredUuid getTypeUuid() {
		throw new UnsupportedOperationException("Implement me");
	}

	@Override
	protected void init(ByteBuf buf) {
		throw new UnsupportedOperationException("Implement me");
	}

	@Override
	public StateContainer<ManagedState> getStateContainer() {
		// TODO Auto-generated method stub
		return null;
	}
}
