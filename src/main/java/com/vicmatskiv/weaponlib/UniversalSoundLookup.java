package com.vicmatskiv.weaponlib;

import java.util.HashMap;
import java.util.Map.Entry;

import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;

import net.minecraft.util.SoundEvent;

public class UniversalSoundLookup {
	
	private static HashMap<String, CompatibleSound> registry = new HashMap<>();
	
	
	public static void initialize(ModContext context) {
		for(Entry<String, CompatibleSound> entry : registry.entrySet()) {
			
			registry.put(entry.getKey(), context.registerSound(entry.getKey()));	
		//	System.out.println("Properly initialized " + entry.getKey() + " | " + registry.get(entry.getKey()));
		}
	}
	
	public static boolean hasSound(String name) {
		return registry.containsKey(name.toLowerCase());
	}
	
	public static CompatibleSound lookupSound(String soundName) {
		return registry.get(soundName.toLowerCase());
	}
	
	public static void registerSoundToLookup(String name) {
		registry.put(name.toLowerCase(), null);
	}
	

}
