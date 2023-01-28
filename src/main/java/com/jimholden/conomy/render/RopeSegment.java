package com.jimholden.conomy.render;

import net.minecraft.util.math.Vec3d;

public class RopeSegment {
	
	public Vec3d A1;
	public Vec3d A2;
	public Vec3d A3;
	public Vec3d A4;
	
	public Vec3d B1;
	public Vec3d B2;
	public Vec3d B3;
	public Vec3d B4;



	
	public RopeSegment(Vec3d a1, Vec3d a2, Vec3d a3, Vec3d a4, Vec3d b1, Vec3d b2, Vec3d b3, Vec3d b4) {
		this.A1 = a1;
		this.A2 = a2;
		this.A3 = a3;
		this.A4 = a4;
		
		this.B1 = b1;
		this.B2 = b2;
		this.B3 = b3;
		this.B4 = b4;
	}
}
