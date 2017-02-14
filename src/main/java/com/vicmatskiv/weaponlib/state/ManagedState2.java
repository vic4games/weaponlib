package com.vicmatskiv.weaponlib.state;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ManagedState2 {
	
	private ManagedState2 base;
	private ManagedState2 permitRequestedPhase;
	@SuppressWarnings("unused")
	private int ordinal;
	private static AtomicInteger ordinalCounter = new AtomicInteger();
	
	private static ConcurrentMap<Aspect, Map<String, ManagedState2>> aspectStates = new ConcurrentHashMap<>();
	
	private String name;
	private Aspect aspect;
	
	public ManagedState2(Aspect aspect) {
		this(aspect, null);
	}
	
	public ManagedState2(Aspect aspect, String name) {
		this(aspect, name, null);
	}
	
	private ManagedState2(Aspect aspect, String name, ManagedState2 base) {
		Map<String, ManagedState2> states = aspectStates.computeIfAbsent(aspect, a -> new HashMap<>());
		this.aspect = aspect;
		this.name = name;
		this.ordinal = ordinalCounter.getAndIncrement();
		this.base = base;
		if(base == null) {
			this.permitRequestedPhase = new ManagedState2(aspect, name + " - requested", this);
		}
	}

	public ManagedState2 permitRequested() {
		return permitRequestedPhase;
	}
	
	public ManagedState2 base() {
		return base;
	}
	
	public static ManagedState2 fromOrdinal(int ordinal) {
		return null;
	}

	@Override
	public String toString() {
		return "[" + name + "]";
	}
}
