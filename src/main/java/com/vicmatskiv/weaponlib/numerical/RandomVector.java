package com.vicmatskiv.weaponlib.numerical;

import java.util.Random;

import com.vicmatskiv.weaponlib.animation.ClientValueRepo;
import com.vicmatskiv.weaponlib.animation.MatrixHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.util.datafix.fixes.MinecartEntityTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class RandomVector {
	
	private double dirX, dirY, dirZ;
	private double x, y, z;
	private double prevX, prevY, prevZ;
	
	public RandomVector() {
		
	}
	
	
	public void update(double speed, double dt) {
		
		prevX = x; 
		prevY = y;
		prevZ = z;
		
		x *= 0.6;
		y *= 0.5;
		
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public double getZ() {
		return this.z;
	}
	
	public Vec3d getVector() {
		
		if(Math.random() < 0.2) {
			double mag =  0.2;
			this.dirX = Math.random()*mag - (mag/2);
			this.dirY = Math.random()*mag - (mag/2);
		}
		
		
		this.x += this.dirX*0.5;
		this.y += this.dirY*0.5;
	
		
		
		
		return null;
	}
	
	public Vec3d getVector(double amplitude) {
		return new Vec3d(this.x*amplitude, this.y*amplitude, this.z*amplitude);
	}
	
	public Vec3d getInterpolatedVector(double amplitude) {
		float ticks = Minecraft.getMinecraft().getRenderPartialTicks();
		return new Vec3d(MatrixHelper.solveLerp(this.prevX, this.x, ticks),
				MatrixHelper.solveLerp(this.prevY, this.y, ticks),
				MatrixHelper.solveLerp(this.prevZ, this.z, ticks)).scale(amplitude);
	}

}
