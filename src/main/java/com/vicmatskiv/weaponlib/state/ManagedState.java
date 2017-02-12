package com.vicmatskiv.weaponlib.state;

import java.util.concurrent.atomic.AtomicInteger;

public class ManagedState {
	
	private ManagedState base;
	private ManagedState permitRequestedPhase;
	@SuppressWarnings("unused")
	private int ordinal;
	private static AtomicInteger ordinalCounter = new AtomicInteger();
	private String name;
	
	public ManagedState(String name) {
		this(name, null);
	}
	
	public ManagedState() {
		this(null, null);
	}
	
	private ManagedState(String name, ManagedState base) {
		this.name = name;
		this.ordinal = ordinalCounter.getAndIncrement();
		this.base = base;
		if(base == null) {
			this.permitRequestedPhase = new ManagedState(name + " - requested", this);
		}
	}

	public ManagedState permitRequested() {
		return permitRequestedPhase;
	}
	
	public ManagedState base() {
		return base;
	}
	
	public static ManagedState fromOrdinal(int ordinal) {
		return null;
	}

	@Override
	public String toString() {
		return "[" + name + "]";
	}
}
