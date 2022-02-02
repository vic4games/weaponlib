package com.vicmatskiv.weaponlib.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vicmatskiv.weaponlib.model.Bullet556;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;

public class ShellParticleTest {
	
	public static Bullet556 bulletModel = new Bullet556();
	
	
	public static ArrayList<Shell> shells = new ArrayList<>();
	
	public static class Shell {
		public int age = 0;
		public boolean onGround = false;
		public boolean shouldDie = false;
		public Vec3d pos = Vec3d.ZERO;
		public Vec3d prevPos = Vec3d.ZERO;
		
		public Vec3d velocity = Vec3d.ZERO;
		public Vec3d rot = Vec3d.ZERO;
		public Vec3d rotImpulse = Vec3d.ZERO;
		
		public Shell(Vec3d pos, Vec3d rot, Vec3d velocity) {
			this.pos = pos;
			this.rot = rot;
			this.velocity = velocity;
			
			this.rotImpulse = new Vec3d(360*Math.random(), 360*Math.random(), 360*Math.random());
		}
		
		
	}
	
	
	
	public static void update(double dt) {
		
		
		
		Iterator<Shell> itr = shells.iterator();
		while(itr.hasNext()) {
			
			
			
			Shell shell = itr.next();
			
			/*
			List<AxisAlignedBB> box = Minecraft.getMinecraft().player.world.getCollisionBoxes(null,
					new AxisAlignedBB(-1, -1, -1, 1, 1, 1).offset(shell.pos.x, shell.pos.y, shell.pos.z));
			*/
			/*
			for(AxisAlignedBB b : box) {
				if(b.intersects(shell.pos, shell.pos.add(shell.velocity.scale(0.02)))) {
					shell.velocity = shell.velocity.scale(-0.2);
					//shell.rotImpulse = shell.rotImpulse.scale(0.05);
				}
				//if(b.)
			}
			*/
			
			RayTraceResult rtr = Minecraft.getMinecraft().world.rayTraceBlocks(shell.pos, shell.pos.add(shell.velocity.scale(0.03)),
					false, true, false);
			if(rtr != null && rtr.typeOfHit == Type.BLOCK) {
				//shell.velocity = shell.velocity.scale(-0.05);
				Vec3d hitDir = rtr.hitVec.subtract(shell.pos);
				if(hitDir.y < hitDir.x && hitDir.y < hitDir.z) {
					shell.onGround = true;
					shell.velocity = Vec3d.ZERO;
					shell.pos = rtr.hitVec;
					shell.rotImpulse = new Vec3d(0, 0, shell.rotImpulse.z);
					shell.rot = new Vec3d(-90, 0, shell.rot.z);
				} else {
					shell.velocity = new Vec3d(-shell.velocity.x*0.1, shell.velocity.y, -shell.velocity.z*0.1);
				}
				
				
				
			} 
			
			
			
			if(!shell.onGround) {
				
				shell.velocity = shell.velocity.addVector(0, -0.5, 0);
				
			} else {
				shell.rotImpulse = shell.rotImpulse.scale(0.5);
			}
			
			shell.age++;
			if(shell.age > 40) {
				itr.remove();
				continue;
			}
			shell.prevPos = shell.pos;
			shell.pos = shell.pos.add(shell.velocity.scale(dt));
			shell.rot = shell.rot.add(shell.rotImpulse.scale(dt));
		}
		
		
	}

}
