package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class CompatibleSound {
	
	private SoundEvent soundEvent;
	private ResourceLocation soundResourceLocation;
	
	public CompatibleSound(ResourceLocation soundResourceLocation) {
		this.soundResourceLocation = soundResourceLocation;
		this.soundEvent = new SoundEvent(soundResourceLocation);
	}

	public SoundEvent getSound() {
		return soundEvent;
	}

	public ResourceLocation getResourceLocation() {
		return soundResourceLocation;
	}
}
