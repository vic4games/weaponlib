package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.util.ResourceLocation;

public class CompatibleSound {

	private String sound;
	private ResourceLocation soundResourceLocation;
	
	public CompatibleSound(ResourceLocation soundResourceLocation) {
		this.soundResourceLocation = soundResourceLocation;
		this.sound = soundResourceLocation.toString();
	}

	public String getSound() {
		return sound;
	}

	public ResourceLocation getResourceLocation() {
		return soundResourceLocation;
	}
}
