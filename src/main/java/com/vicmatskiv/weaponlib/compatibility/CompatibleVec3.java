package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.util.Vec3;

public class CompatibleVec3 {

	private Vec3 vec;

	public CompatibleVec3(Vec3 vec) {
		this.vec = vec;
	}
	
	public Vec3 getVec() {
		return vec;
	}
}
