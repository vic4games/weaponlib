package com.jimholden.conomy.entity;

import javax.vecmath.Vector2d;

import org.lwjgl.input.Keyboard;

import com.jimholden.conomy.main.ModEventClientHandler;
import com.jimholden.conomy.util.VectorUtil;
import com.jimholden.conomy.vehicletest.Engine;
import com.jimholden.conomy.vehicletest.MarcTest;
import com.jimholden.conomy.vehicletest.VehiclePhysUtil;
import com.jimholden.conomy.vehicletest.newphysics.VehiclePhysicsSolver;
import com.jimholden.conomy.vehicletest.parts.engines.EvoIVEngine;
import com.jimholden.conomy.vehicletest.physics.VehicleForceHandler;
import com.jimholden.conomy.vehicletest.test.Marco;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreIngredient;

public class EntityTestVes extends EntityBoat {

	public int revs = 0;
	public double steeringAngle = 0.0;
	public boolean isRevving = false;
	public boolean isBraking = false;
	public boolean isHandbraking = false;
	public double weight = 1352;
	public VehicleForceHandler fHandle;
	public Engine engine;
	
	public EntityTestVes(World worldIn, double x, double y, double z) {
		super(worldIn);
		fHandle = new VehicleForceHandler(this);
		engine = new EvoIVEngine("EvoIVEngine", "Mitsubishi Motors");
		setPosition(x, y, z);
	}
	
	public EntityTestVes(World world) {
		super(world);
		fHandle = new VehicleForceHandler(this);
		engine = new EvoIVEngine("EvoIVEngine", "Mitsubishi Motors");
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void move(MoverType type, double x, double y, double z) {
		// TODO Auto-generated method stub
		super.move(type, x, y, z);
	}
	
	@Override
	public boolean startRiding(Entity entityIn, boolean force) {
		

		return super.startRiding(entityIn, force);
	}
	
	@Override
	public void dismountRidingEntity() {
		
		super.dismountRidingEntity();
	}
	
	public double angle = 0;
	public double angularvelocity = 0;
	public double steerangle = 0;
	public int throttle = 0;
	public int brake = 0;
	public double mass = 1352;
	public double inertia = 1500;
	public Vector2d velocity_wc = new Vector2d();
	public Vector2d position_wc = new Vector2d();
	public MarcTest mt = null;
	public VehiclePhysicsSolver solver;
	
	public void abonUpdate() {
		if(!this.world.isRemote) return;
		if(!this.isBeingRidden()) return;
		Entity player = getPassengers().get(0);
		if(player == null) return;
		
		// make sure solver ain't null bruv
		if(solver == null) {
			solver = new VehiclePhysicsSolver(this, 1352);
		}
		
		/*
		// get the look vector
		float yaw = player.rotationYaw;
		float pitch = player.rotationPitch;
		float f = 1.0F;
		double motionX = (double)(-MathHelper.sin(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI) * f);
		double motionZ = (double)(MathHelper.cos(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI) * f);
		double motionY = (double)(-MathHelper.sin((pitch) / 180.0F * (float)Math.PI) * f);
		Vec3d dirVec = new Vec3d(motionX, 0, motionZ);
		
		// get vehicle oreintation
		Vec3d oreintVec = Vec3d.fromPitchYaw(this.rotationPitch, this.rotationYaw);
		
		//get the steering angle
		double det = dirVec.crossProduct(oreintVec).y;
		if(det > 0) {
			det = 1;
		} else {
			det = -1;
		}
		double aT = Math.toDegrees(VectorUtil.angleBetweenVec(dirVec, oreintVec));
		double tempSteeringAngle = aT*det;
		if(aT < -45.0F) {
			aT = -45.0F;
		}
		if(aT > 45.0F) {
			aT = 45.0F;
		}
		steerangle = Math.toRadians(tempSteeringAngle);
		*/
		
		// run car controls
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			if( throttle < 1) throttle += 0.1;
		}  else {
			if(throttle > 0) throttle -= 0.1;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			if( throttle >= 0) throttle -= 0.1;
			isBraking = true;
		} else isBraking = false;
		if(throttle < 0) throttle = 0;
		if(throttle > 1) throttle = 1;
		
		steerangle *= 0.5;
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
		{
	     if( steerangle > - Math.PI/5.0 ) steerangle -= Math.PI/32.0;
		} else if( Keyboard.isKeyDown(Keyboard.KEY_D) )
	    {
	       if( steerangle <  Math.PI/5.0 ) steerangle += Math.PI/32.0;
	    }
		
		solver.updatePhysics();
		
		/*
		if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			brake = 10000;
			throttle = 0;
		} else brake = 0;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			mt.front_slip = 1;
			mt.rear_slip = 1;
		} else {
			mt.front_slip = 0;
			mt.rear_slip = 0;
		}*/
		
		
		//super.onUpdate();
	}
	
	@Override
	public void onUpdate() {
		
		if(!this.world.isRemote) return;
		
		//fHandle = new VehicleForceHandler(this);
		
		if(!this.isBeingRidden()) return;
		Entity player = getPassengers().get(0);
		if(player == null) return;
		if(this.isBeingRidden()) {
			ModEventClientHandler.etV = this;
		} else {
			ModEventClientHandler.etV = null;
		}
		
		float yaw = player.rotationYaw;
		float pitch = player.rotationPitch;
		float f = 1.0F;
		double motionX = (double)(-MathHelper.sin(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI) * f);
		double motionZ = (double)(MathHelper.cos(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI) * f);
		double motionY = (double)(-MathHelper.sin((pitch) / 180.0F * (float)Math.PI) * f);
		Vec3d dirVec = new Vec3d(motionX, 0, motionZ);
		
		
		Vec3d oreintVec = Vec3d.fromPitchYaw(this.rotationPitch, this.rotationYaw);
		
		
		double det = dirVec.crossProduct(oreintVec).y;
		if(det > 0) {
			det = 1;
		} else {
			det = -1;
		}
		double aT = Math.toDegrees(VectorUtil.angleBetweenVec(dirVec, oreintVec));
		steeringAngle = aT*det;
		if(aT < -45.0F) {
			aT = -45.0F;
		}
		if(aT > 45.0F) {
			aT = 45.0F;
		}
		steerangle = Math.toRadians(-steeringAngle);
		
		
		
		
		if(mt == null) {
			mt = new MarcTest(this);
		}
	//	System.out.println(steeringAngle);
		
		/*
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			steeringAngle -= 0.5;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			steeringAngle += 0.5;
		}*/
		
		
		//System.out.println("RPM: " + revs);
		
		/* cancel all the good shit FML
		if(isRevving) {
			revs += 60;
			if(revs > 7000) revs = 7000;
			
			fHandle.applyTractionForce(oreintVec, revs);
		} else {
			if(revs > 0) {
				revs -= 5;
			}
			
			fHandle.tractionForce = Vec3d.ZERO;
		}
		
		if(isBraking) {
			revs = 0;
			fHandle.applyBrakingForce(oreintVec, 200);
		} else {
			fHandle.brakingForce = Vec3d.ZERO;
		}
		*/
		
		// experimental
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			if( throttle < 6900) throttle += 100;
		}  else {
			if(throttle > 0) throttle -= 10;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			if( throttle >= 10) throttle -= 10;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
		{
	     if( steerangle > - Math.PI/5.0 ) steerangle -= Math.PI/32.0;
		} else if( Keyboard.isKeyDown(Keyboard.KEY_D) )
	    {
	       if( steerangle <  Math.PI/5.0 ) steerangle += Math.PI/32.0;
	    }
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			brake = 10000;
			throttle = 0;
		} else brake = 0;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			mt.front_slip = 1;
			mt.rear_slip = 1;
		} else {
			mt.front_slip = 0;
			mt.rear_slip = 0;
		}
		
		//fHandle.slipTest(revs);
		/*
		if(isRevving) {
			fHandle.applyTractionForce(oreintVec, 3184);
		} else {
			fHandle.tractionForce = Vec3d.ZERO;
		}
		
		if(isBraking) {
			fHandle.applyBrakingForce(fHandle.velocity, 0.1);
		} else {
			fHandle.brakingForce = Vec3d.ZERO;
		} */
		
		mt.do_physics(0.05);
		rotationYaw = -(float) Math.toDegrees(angle);	
		
		// old fhandle
		//fHandle.runForces();
		//move(MoverType.SELF, fHandle.position.x, fHandle.position.y, fHandle.position.z);
		
		
		//setPosition(posX + fHandle.position.x, posY + fHandle.position.y, posZ + fHandle.position.z);
		//System.out.println("fuck " + fHandle.position.x);
		
		
	}
	
	
	public void backupOnUpdate() {
if(!this.world.isRemote) return;
		
		if(this.isBeingRidden()) {
			ModEventClientHandler.etV = this;
		} else {
			ModEventClientHandler.etV = null;
		}
		
		if(!this.isBeingRidden()) return;
		Entity player = getPassengers().get(0);
		if(player == null) return;
		//if(this.input)
		//System.out.println(isRevving);
		if(isRevving) {
			revs += 50;
		} else if(ticksExisted % 2 == 0 && revs > 0) {
			revs -= 50;
		}
		
		Vec3d netForce = new Vec3d(0.0, 0.0, 0.0);
		
		
		//float wheelForce = EngineUtil.calculateWheelForce(2.785F, 3.312F, revs/100, 0.33F);
		
		
		
		// calculates our u vec
		float yaw = player.rotationYaw;
		float pitch = player.rotationPitch;
		float f = 1.0F;
		double motionX = (double)(-MathHelper.sin(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI) * f);
		double motionZ = (double)(MathHelper.cos(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI) * f);
		double motionY = (double)(-MathHelper.sin((pitch) / 180.0F * (float)Math.PI) * f);
		Vec3d dirVec = new Vec3d(motionX, 0, motionZ);
		
		if(isRevving) {
			Vec3d velocityVec = VehiclePhysUtil.simpleTractionForce(dirVec, revs/500.0F);
			this.addVelocity(velocityVec.x, velocityVec.y, velocityVec.z);
		}
		
		/*
		// Calculate the brake vector
		if(isBraking && dirVec != null) {
			Vec3d brakingVec = dirVec.scale(-0.2);
			this.addVelocity(brakingVec.x, brakingVec.y, brakingVec.z);
			
		}
		*/
		
		double weight = 4000;
		
		// Calculate the drag vector
		Vec3d mVec = new Vec3d(this.motionX, this.motionY, this.motionZ);
		Vec3d dragVector = VehiclePhysUtil.simpleDragForce(0.12F, mVec);
		this.addVelocity(dragVector.x, dragVector.y, dragVector.z);
		System.out.println(dragVector);
		
		// Calculate the rolling resistance
		Vec3d mVec2 = new Vec3d(this.motionX, this.motionY, this.motionZ);
		Vec3d rollingRes = mVec2.scale(-0.012);
		this.addVelocity(rollingRes.x, rollingRes.y, rollingRes.z);

		// Calculate the brake vector
		if(isBraking) {
	
			Vec3d brakingVec = VehiclePhysUtil.brakingForce(0.6F, dirVec);
			this.addVelocity(brakingVec.x, brakingVec.y, brakingVec.z);
		}
		
		
		/*
		// Calculate the drag vector
		Vec3d mVec = new Vec3d(this.motionX, this.motionY, this.motionZ);
		Vec3d dragVector = mVec.scale(-0.3).scale(mVec.lengthVector());
		this.addVelocity(dragVector.x, dragVector.y, dragVector.z);
		
		
		// Calculate the rolling resistance
		Vec3d mVec2 = new Vec3d(this.motionX, this.motionY, this.motionZ);
		Vec3d rollingRes = mVec2.scale(-0.012);
		//netForce.add(rollingRes);
		this.addVelocity(rollingRes.x, rollingRes.y, rollingRes.z);
		*/
		
		double calcD = (mVec.lengthVector()*40)*-0.3;
		double calcR = (mVec.lengthVector()*40)*-0.12;
		double calcS = (mVec.lengthVector()*40);
		//System.out.println(calcD + " | " + calcR + " | " + calcS);
		//System.out.println((calcD + calcR + calcS));
		double acceleration = (calcD + calcR + calcS)/(weight/4000);
		//System.out.println("accel: " + acceleration);
		Vec3d cog = this.getPositionVector().addVector(0.0, 0.5, 0.0);
		
		//System.out.println(motionX + " | " + motionY + " | " + motionZ);
		//System.out.println(netForce);
		//this.addVelocity(netForce.x, netForce.y, netForce.z);
		
		
		//motionX = 0;
		//motionY = 0;
		//motionZ = 0;
		//System.out.println();
		
		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		//System.out.println(motionX + " | " + motionY + " | " + motionZ);
		//super.onUpdate();
	}

	
	

	
	

}
