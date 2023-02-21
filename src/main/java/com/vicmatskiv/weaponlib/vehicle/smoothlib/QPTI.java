package com.vicmatskiv.weaponlib.vehicle.smoothlib;

import com.vicmatskiv.weaponlib.vehicle.jimphysics.InterpolationKit;

import net.minecraft.client.Minecraft;

public class QPTI {
	
	public static float pti(float a, float b) {
		return (float) InterpolationKit.interpolateValue((double) a, (double) b, (double) Minecraft.getMinecraft().getRenderPartialTicks());
	}
	
	public static double pti(double a, double b) {
		return InterpolationKit.interpolateValue((double) a, (double) b, (double) Minecraft.getMinecraft().getRenderPartialTicks());
		
	}

}
