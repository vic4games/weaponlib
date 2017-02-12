package com.vicmatskiv.weaponlib.state;

public abstract class Permit extends UniversalObject {
	
	public enum Status { GRANTED, DENIED, TIMED_OUT };
	
	public abstract Status getStatus();
	
	public abstract ManagedState getTargetState();
}