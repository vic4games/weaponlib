package com.vicmatskiv.weaponlib.compatibility;

import cpw.mods.fml.common.event.FMLInitializationEvent;

public class CompatibleFmlInitializationEvent {

	private FMLInitializationEvent event;

	public CompatibleFmlInitializationEvent(FMLInitializationEvent event) {
		this.event = event;
	}

	public FMLInitializationEvent getEvent() {
		return event;
	}

}
