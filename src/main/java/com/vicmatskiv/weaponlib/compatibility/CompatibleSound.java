package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.util.ResourceLocation;

public class CompatibleSound {
    
    public static final CompatibleSound SNOWBALL_THROW = new CompatibleSound("random.bow");

	private String sound;
	private ResourceLocation soundResourceLocation;
	
	public CompatibleSound(ResourceLocation soundResourceLocation) {
		this.soundResourceLocation = soundResourceLocation;
		this.sound = soundResourceLocation.toString();
	}

	private CompatibleSound(String sound) {
        this.sound = sound;
    }

    public String getSound() {
		return sound;
	}

	public ResourceLocation getResourceLocation() {
		return soundResourceLocation;
	}
}
