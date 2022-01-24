package com.vicmatskiv.weaponlib.numerical;

import com.vicmatskiv.weaponlib.animation.MatrixHelper;

import net.minecraft.client.Minecraft;

public class LerpedValue {
	
	public double previousValue;
	public double currentValue;
	
	
	
	public LerpedValue() {
		
	}
	
	public void update(double newValue) {
		this.previousValue = this.currentValue;
		this.currentValue = newValue;
	}
	
	public double getLerped() {
		return MatrixHelper.solveLerp(this.previousValue, this.currentValue, Minecraft.getMinecraft().getRenderPartialTicks());
	}
	
	public float getLerpedFloat() {
		return (float) getLerped();
	}
	

}
