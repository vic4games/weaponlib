package com.vicmatskiv.weaponlib.render.shells;

import java.util.ArrayList;

import com.vicmatskiv.weaponlib.animation.MatrixHelper;
import com.vicmatskiv.weaponlib.model.Bullet556;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import scala.collection.mutable.ResizableArray;
import net.minecraft.util.math.Vec3d;

public class ShellParticleSimulator {
	
	public static final double CHUNK_SIZE = 1.5;
	
	private static final int SHELL_LIFE = 60;
	private static final double RESTITUTION = 0.5;
	
	public static Bullet556 bulletModel = new Bullet556();
	
	

	public static class Shell {
		public int age = 0;
		public boolean onGround = false;
		public boolean shouldDie = false;
		public Vec3d pos = Vec3d.ZERO;
		public Vec3d prevPos = Vec3d.ZERO;
		
		public Vec3d velocity = Vec3d.ZERO;
		public Vec3d prevRot = Vec3d.ZERO;
		public Vec3d rot = Vec3d.ZERO;
		public Vec3d rotImpulse = Vec3d.ZERO;
		
		private double height;
		
		
		
		public Shell(Vec3d pos, Vec3d rot, Vec3d velocity) {
			this.pos = pos;
			this.rot = rot;
			this.velocity = velocity;
			
			
			this.height = -2;
			
			double spin = 2000;
			this.rotImpulse = new Vec3d(spin*Math.random()-(spin/2), spin*Math.random()-(spin/2), spin*Math.random()-(spin/2));
		}
		
		public void ageShell() {
			this.age++;
		}
		
		public int getAge() {
			return this.age;
		}
		
		public void setHeight(double height) {
			this.height = height;
		}
		
		public double getHeight() {
			return this.height;
		}
	
		
		public boolean shouldDie() {
			return this.shouldDie;
		}
		
		public void kill() {
			setShouldDie(true);
		}
		
		public void setShouldDie(boolean state) {
			this.shouldDie = state;
		}
		
	}
	

	
	
	
	public void update(ArrayList<Shell> shells, double dt) {
		
	//	dt = 0.1;
		
		
		
		// Removes old shells that were marked for death
		shells.removeIf((s) -> s.shouldDie());
		
		
		
		
		// Don't use an iterator to prevent concurrent
		// modification errors
		for(int i = 0; i < shells.size(); ++i) {
			
			
			
			Shell sh = shells.get(i);
			
			
			if(sh.getAge() > SHELL_LIFE) {
				sh.kill();
			}
			sh.ageShell();
			
			sh.prevPos = sh.pos;
			sh.prevRot = sh.rot;
			
			sh.velocity = sh.velocity.addVector(0, -9.81*dt, 0);
			
			RayTraceResult direction = Minecraft.getMinecraft().world.rayTraceBlocks(sh.prevPos, sh.prevPos.add(sh.velocity.scale(dt)), false, true, false);
			if(direction != null) {
				
				double randomIntensity = 1;			
				double newX = sh.velocity.x * -RESTITUTION + ((Math.random()*randomIntensity) - randomIntensity/2);
				double newY = sh.velocity.y * -RESTITUTION + ((Math.random()*randomIntensity) - randomIntensity/2);
				double newZ = sh.velocity.z * -RESTITUTION + ((Math.random()*randomIntensity) - randomIntensity/2);
				
				sh.velocity = new Vec3d(newX, newY, newZ);
			}
			
			sh.pos = sh.pos.add(sh.velocity.scale(dt));
			sh.rot = sh.rot.add(sh.rotImpulse.scale(dt));
			
			if(sh.velocity.lengthVector() < 2) {
				sh.rotImpulse = Vec3d.ZERO;
			}
			
			
		}
		
		
		
		
		
		
	}






}
