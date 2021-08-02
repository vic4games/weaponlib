package com.vicmatskiv.weaponlib.vehicle.collisions;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;

import net.minecraft.util.math.Vec3d;

public class VehicleMassObject {
	
	public double mass = 0.0;
	public Matrix3f inertia;
	public Vec3d centerOfGravity;
	
	public VehicleMassObject(double mass, Matrix3f inertia, Vec3d cog) {
		this.mass = mass;
		this.inertia = inertia;
		this.centerOfGravity = cog;
	}

}
