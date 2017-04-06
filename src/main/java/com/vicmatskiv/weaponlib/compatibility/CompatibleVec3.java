package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.util.math.Vec3d;

public class CompatibleVec3 {

	private Vec3d vec;

	public CompatibleVec3(Vec3d vec) {
		this.vec = vec;
	}
	
	public Vec3d getVec() {
		return vec;
	}
}
