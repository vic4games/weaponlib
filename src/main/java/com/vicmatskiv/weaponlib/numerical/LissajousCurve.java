package com.vicmatskiv.weaponlib.numerical;

import com.vicmatskiv.weaponlib.animation.ClientValueRepo;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class LissajousCurve {
	
	public static float getXOffsetOnCurve(double amp, double a, double b, double c, double tick) {
         return (float) ((float) amp*Math.sin(a*tick+c));
	}
	
	public static float getYOffsetOnCurve(double amp, double a, double b, double c, double tick) {
		return (float) ((float) amp*Math.sin(b*tick));
	}

}
