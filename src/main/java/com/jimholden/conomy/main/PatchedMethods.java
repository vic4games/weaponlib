package com.jimholden.conomy.main;

import net.minecraft.client.audio.ISound;
import net.minecraft.util.SoundCategory;

public class PatchedMethods {
	
	private static SoundCategory lastSoundCategory;
	private static String lastSoundName;
	private static ISound.AttenuationType lastSoundAtt;
	
	/**
	 * CALLED BY ASM INJECTED CODE!
	 */
	// For sounds that get played normally
	public static void onPlaySound(final float posX, final float posY, final float posZ, final int sourceID) {
		onPlaySound(posX, posY, posZ, sourceID, lastSoundCategory, lastSoundName, lastSoundAtt);
	}

	/**
	 * CALLED BY ASM INJECTED CODE!
	 */
	public static void onPlaySound(final float posX, final float posY, final float posZ, final int sourceID, SoundCategory soundCat, String soundName, ISound.AttenuationType attType) {
		//log(String.valueOf(posX)+" "+String.valueOf(posY)+" "+String.valueOf(posZ)+" - "+String.valueOf(sourceID)+" - "+soundCat.toString()+" - "+attType.toString()+" - "+soundName);
		
	}
	
	
}
