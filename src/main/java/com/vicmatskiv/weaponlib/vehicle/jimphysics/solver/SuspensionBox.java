package com.vicmatskiv.weaponlib.vehicle.jimphysics.solver;

import com.vicmatskiv.weaponlib.vehicle.jimphysics.InterpolationKit;

import net.minecraft.client.Minecraft;

public class SuspensionBox {
	
	public double prevRoll;
	public double roll;
	
	public double prevPitch;
	public double pitch;
	
	
	
	public SuspensionBox() {
		
	}
	
	public void applyForwardAccel(double inertia, double acceleration) {
	
	}
	
	public void update() {
		
	}
	
	
	
	public double pti(double o, double n) {
		return InterpolationKit.interpolateValue(o, n, Minecraft.getMinecraft().getRenderPartialTicks());
	}

}
