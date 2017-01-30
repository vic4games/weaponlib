package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.util.MathHelper;

public class CompatibleMathHelper {

	public float cos(float in) {
		return MathHelper.cos(in);
	}
	
	public float sin(float in) {
		return MathHelper.sin(in);
	}
	
	public float sqrt_float(float in) {
		return MathHelper.sqrt_float(in);
	}
	
	public float sqrt_double(double in) {
		return MathHelper.sqrt_double(in);
	}
}
