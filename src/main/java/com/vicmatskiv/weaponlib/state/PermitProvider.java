package com.vicmatskiv.weaponlib.state;

public interface PermitProvider<Context> {

	public void set(Permit permit, ManagedState targetState, Context context);
	
	public Permit get(ManagedState targetState, Context context);
}
