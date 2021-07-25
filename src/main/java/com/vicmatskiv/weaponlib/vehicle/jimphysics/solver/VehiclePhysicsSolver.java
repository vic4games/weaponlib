package com.vicmatskiv.weaponlib.vehicle.jimphysics.solver;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector2d;


import com.vicmatskiv.weaponlib.KeyBindings;
import com.vicmatskiv.weaponlib.network.IEncodable;
import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;
import com.vicmatskiv.weaponlib.vehicle.VehicleTransmissionStrategy.DefaultTransmissionStrategy;
import com.vicmatskiv.weaponlib.vehicle.collisions.GJKResult;
import com.vicmatskiv.weaponlib.vehicle.collisions.InertiaKit;
import com.vicmatskiv.weaponlib.vehicle.collisions.MathHelper;
import com.vicmatskiv.weaponlib.vehicle.collisions.OBBCollider;
import com.vicmatskiv.weaponlib.vehicle.collisions.OreintedBB;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.Engine;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.Transmission;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.VehiclePhysUtil;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleClientPacket;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleClientPacketHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class VehiclePhysicsSolver implements IEncodable<VehiclePhysicsSolver> {
	

	
	public Matrix3d rotMat;
	
	public WheelAxel frontAxel;
	public WheelAxel rearAxel;
	public EntityVehicle vehicle;
	public Engine engine;
	int revolutions = 0;
	public double timeStep = 0.05;
	public Vec3d positonDelta = new Vec3d(0, 0, 0);
	double mass;
	public Vec3d velocity = new Vec3d(0, 0, 0);
	double brakeTorque = 12000;
	double COGHeight = 0.3;
	public Transmission transmission;
	
	
	public float[] angles;
	
	
	public int currentRPM = 0;
	public int prevRPM = 0;
	
	// forces
	double angularVelocity = 0;
	double yawspeed = 0;
	
	// various properties
	double wheelBase = 1.0;
	
	// aceel
	public double synthAccelFor = 0.0;
	public double synthAccelSide = 0.0;
	
	
	// 
	
	public double rotationalImpulse = 0.0;
	
	// side and forward accel
	Vec3d sideForAccel = new Vec3d(0, 0, 0);
	Vector2d longLatVal = new Vector2d(0, 0);
	Vector2d accelLongLat = new Vector2d(0, 0);
	
	// KONSEI DORIFTO?!
	public boolean isDrifting = true;
	
	
	public Material materialBelow;
	
	
	public ArrayList<WheelSolver> wheels = new ArrayList<>();
	
	public VehiclePhysicsSolver(EntityVehicle vehicle, double mass) {
		this.vehicle = vehicle;
		this.engine = vehicle.engine;
		this.transmission = vehicle.getConfiguration().getVehicleTransmission().cloneTransmission();
		initTestingVehicle();
	}
	
	public Vec3d getOreintationVector() {

		return Vec3d.fromPitchYaw(/*vehicle.rotationPitch*/0f, vehicle.rotationYaw);

	}
	
	public Vec3d getVelocityVector() {
		if(velocity == null) return Vec3d.ZERO;
		return velocity;
	}
	
	public void applyHandbrake() {
		this.isDrifting = true;
		rearAxel.applyHandbrake();
	}
	
	public double getSyntheticAcceleration() {
		if(Double.isNaN(synthAccelFor)) return 0.0;
		return synthAccelFor;
	}
	
	public void releaseHandbrake() {
		this.isDrifting = false;
		rearAxel.releaseHandbrake();
	}
	
	public double getSideSlipAngle() {
		try {

			
			Vec3d uVec = getOreintationVector();
			
			
			//System.out.println(Vec3d.fromPitchYaw(0.0f, vehicle.rotationYaw) + " | " + uVec);
			

			Vector2d u2D = new Vector2d(uVec.x, uVec.z);
			Vector2d v2D = new Vector2d(velocity.x, velocity.z);
			
			
		
			v2D.normalize();
			double mult = Math.signum(uVec.crossProduct(velocity).y);
			double result = u2D.angle(v2D) * mult;
			
			if(Double.isNaN(result)) {
				return 0.0;
			}
			
			return result;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0.0;
		
	}
	
	
	
	
	/**
	 * Initializes a testing vehicle with 225/45R17 tires
	 * 
	 */
	public void initTestingVehicle() {
		frontAxel = new WheelAxel(this, 0.5, false);
		rearAxel = new WheelAxel(this,-0.5, true);
		WheelSolver f1 = new WheelSolver(this, frontAxel, 22500, 75.0, 0.3175, 0.225, false);
		WheelSolver f2 = new WheelSolver(this, frontAxel, 22500, 75.0, 0.3175, 0.225, false);
		WheelSolver r1 = new WheelSolver(this, rearAxel,  22500, 75.0, 0.3175, 0.225, true);
		WheelSolver r2 = new WheelSolver(this, rearAxel,  22500, 75.0, 0.3175, 0.225, true);
		
		
		f2.setRelativePosition(new Vec3d(-1.7, 0.0, 1.75));
		f1.setRelativePosition(new Vec3d(0.5, 0.0, 1.75));
		r2.setRelativePosition(new Vec3d(-1.7, 0.0, -1.75));
		r1.setRelativePosition(new Vec3d(0.5, 0.0, -1.75));
		
		frontAxel.addWheels(f1, f2);
		rearAxel.addWheels(r1, r2);
		
		
	}
	
	public double getLongitudinalSpeed() {
		if(Double.isNaN(velocity.lengthVector())) return vehicle.throttle;
		

		return velocity.lengthVector()+(vehicle.throttle/10);

	}
	
	public void updateSuspensionPlatform() { 
		
		
		Vec3d pointA = rearAxel.leftWheel.getSuspensionPosition();
		Vec3d pointB = rearAxel.rightWheel.getSuspensionPosition();
		Vec3d pointC = frontAxel.rightWheel.getSuspensionPosition();
		
		double height = (pointA.y+pointB.y+pointC.y)/3.0;
		//System.out.println(height);
		vehicle.rideOffset = height;
		
		 
		System.out.println(pointA + " | " + pointB + " | " + pointC);
		
		Vec3d ab = pointB.subtract(pointA);
		Vec3d ac = pointC.subtract(pointA);
		
		Vec3d planeNormal = ab.crossProduct(ac);
		
		
		
		
		Vec3d oreintation = getOreintationVector();
		
		Vec3d forwardVec = planeNormal.crossProduct(oreintation).crossProduct(oreintation);
		
		Vec3d bitangent = planeNormal.crossProduct(forwardVec);
		
		
		float[] angles = anglesFromVectors(forwardVec.normalize(), planeNormal.normalize());
		
		this.angles = angles;
		
		//System.out.println(planeNormal +  " | " + forwardVec);
		
		System.out.println("Angles (YPR): " + angles[0] + " | " + angles[1] + " | " + angles[2]);
		
		
		this.rotMat = new Matrix3d(planeNormal.x, planeNormal.y, planeNormal.z,
				forwardVec.x, forwardVec.y, forwardVec.z,
				bitangent.x, bitangent.y, bitangent.z);
		
		
		
	}
	
	public float[] anglesFromVectors(Vec3d forward, Vec3d up)
	{
		float[] angles = new float[3];
	    // Yaw is the bearing of the forward vector's shadow in the xy plane.
	    float yaw = (float) Math.atan2(forward.y, forward.x);

	    // Pitch is the altitude of the forward vector off the xy plane, toward the down direction.
	    float pitch = (float) -Math.asin(forward.z);

	    // Find the vector in the xy plane 90 degrees to the right of our bearing.
	    float planeRightX = (float) Math.sin(yaw);
	    float planeRightY = (float) -Math.cos(yaw);

	    // Roll is the rightward lean of our up vector, computed here using a dot product.
	    float roll = (float) Math.asin(up.x*planeRightX + up.y*planeRightY);
	    // If we're twisted upside-down, return a roll in the range +-(pi/2, pi)
	    if(up.z < 0)
	        roll = (float) (Math.signum(roll) * Math.PI - roll);

	    // Convert radians to degrees.
	    angles[0]   =   (float) (yaw * 180 / Math.PI);
	    angles[1] = (float) (pitch * 180 / Math.PI);
	    angles[2]  =  (float) (roll * 180 / Math.PI);
	    return angles;
	}
	
	
	public void updateEngineForces() {
		prevRPM = currentRPM;
		
		if(!vehicle.isVehicleRunning()) {
			if(currentRPM > 0) {
				currentRPM -= 10;
			}
			if(currentRPM < 0) currentRPM = 0;
		}
		
		// If the engine is off, this code should not
		// be run.
		if(!vehicle.isVehicleRunning()) return;
		
		Transmission t = transmission;
		double gearRatio = t.getCurrentGearRatio();
		double finalDriveRatio = t.getDifferentialRatio();


		
		int rpm = 0;
		if(!t.isEngineDeclutched()) {
			rpm = (int) VehiclePhysUtil.getEngineRPM(rearAxel.getWheelAngularVelocity(), gearRatio, finalDriveRatio);
			if(Math.abs(rpm-currentRPM) > 1000) {
				
				double bruv = rpm-currentRPM;
				currentRPM += bruv*0.2;
				rpm = currentRPM;
			}
		} else {
			
			this.currentRPM += 50*vehicle.throttle;
			this.currentRPM -= 10*Math.pow(currentRPM/7000.0+1.0, 2);
			
			
			
		
			
			rpm = currentRPM;
		}
		
	

		if(rpm < 1000) {
			rpm = 1000;
		}
		if(rpm > 7000) {
			rpm = 7000;
		}
		
		// for smoothing purposes
		
		currentRPM = rpm;
		
		
		
		Engine engine = vehicle.getConfiguration().getEngine();
		
		double torque = engine.getTorqueAtRPM(currentRPM);
		double drvT = VehiclePhysUtil.getDriveTorque(torque, gearRatio, finalDriveRatio, 1.0)*(vehicle.throttle);
	
		

		// if the engine is declutched,
		// do not apply any force to the wheels.
		// As it is not analog, there is no
		// slippage.
		//System.out.println(t.isEngineDeclutched());
		if(t.isEngineDeclutched()) drvT = 0;

		
		synthAccelFor += drvT*timeStep/10;
		
		// FIX THIS IN THE FUTURE

		//System.out.println("Drive torque: " + drvT);

		rearAxel.applyDriveTorque(drvT);
		
		transmission.runAutomaticTransmission(vehicle, currentRPM);
	}
	
	public void updateLoad() {
		double weight = vehicle.mass*9.81;
		double accel = synthAccelFor;
		double weightFront = (frontAxel.COGoffset/wheelBase)*weight - (COGHeight/wheelBase)*vehicle.mass*accel;
		double weightRear = (rearAxel.COGoffset/wheelBase)*weight - (COGHeight/wheelBase)*vehicle.mass*accel;
		
		
		
		double newSynth = ((Math.abs(synthAccelFor) * 0.8))*Math.signum(synthAccelFor);
		double newSynthSide = Math.toDegrees(vehicle.steerangle)/5;

		synthAccelFor = newSynth;
		/*
		vehicle.forwardLean = accel/6;
		if(vehicle.forwardLean < 0) vehicle.forwardLean /= 5;
		*/
		
		vehicle.sideLean = (accel/12) + newSynthSide;
		
		//System.out.println(weightRear);
		
		rearAxel.applySuspensionLoad(weightRear*9.81);
		frontAxel.applySuspensionLoad(weightFront*-9.81);
		
		
		rearAxel.distributeLoad(vehicle.mass*9.81);
		frontAxel.distributeLoad(vehicle.mass*9.81);
	}
	
	
	
	
	public void updateWheels() {
		if(vehicle.isBraking) {
			synthAccelFor -= 3;
			frontAxel.applyBrakingForce(0.3);
			rearAxel.applyBrakingForce(0.3);
		}
		frontAxel.setSteeringAngle(vehicle.steerangle);
		frontAxel.doPhysics();
		rearAxel.doPhysics();
	}
	
	public Vec3d calculateResistiveForces(Vec3d speed) {
		Vec3d drag = VehiclePhysUtil.realDrag(0.3F, speed, 2.2);
		Vec3d rolling = VehiclePhysUtil.rollingResistance(12.8F, speed);
		return drag.add(rolling);
		
	}
	
	public void updateRotationalVelocity() {
		
		
		/*
		double rad = VehiclePhysUtil.doubleRadiusOfLSTurn(2.25, vehicle.steerangle);
		double angVel = VehiclePhysUtil.carTurnRate(getVelocityVector(), rad);
		
		
		
		vehicle.rotationYaw += angVel;
		//System.out.println(vehicle.rotationYaw);
		
		
		if(1+1 == 2) return;
		*/
		

		double rC = transmission.isReverseGear ? -1 : 1;
		
		double torqueContributionRear = rearAxel.latNonVec()*rearAxel.COGoffset*rC;
		double torqueContributionFront = Math.cos(vehicle.steerangle)*frontAxel.latNonVec()*frontAxel.COGoffset*rC;

		//System.out.println(torqueContributionFront + " | " + torqueContributionRear + " | " + (torqueContributionFront + torqueContributionRear));
		
		//System.out.println(frontAxel.latNonVec());
		
		/*
		double torqueContR = rearAxel.adjLateralForce().lengthVector()*rearAxel.COGoffset;
		double torqueContF = Math.cos(vehicle.steerangle)*frontAxel.adjLateralForce().lengthVector()*frontAxel.COGoffset;
		*/
		
		double totalAxelTorque = torqueContributionFront + torqueContributionRear;
		
		
		Matrix3f inertia = InertiaKit.inertiaTensorCube((float) vehicle.mass, 2.15f, 2.25f, 6f);
		
		// add roll impulse
		if(rotationalImpulse != 0.0) {
			vehicle.rotationRoll += rotationalImpulse;
			rotationalImpulse = 0.0;
		}
		
		
		
		// https://suspensionsecrets.co.uk/calculating-ideal-spring-and-roll-bar-rates/
		double rollTorque = (velocity.lengthVector()*getSideSlipAngle());
		vehicle.rotationRoll += (float) Math.toDegrees(rollTorque/(inertia.m00));
		double diff = 1.0*Math.sin(Math.toRadians(vehicle.rotationRoll));
		if(vehicle.rotationRoll < 0) {
			 vehicle.rotationRoll += 1.5f*Math.abs(diff);
		} else if (vehicle.rotationRoll > 0 ) {
			vehicle.rotationRoll -= 1.5f*Math.abs(diff);
		}
		
	
		
		
		
		
		double angAccel = totalAxelTorque/inertia.m11;
		
		if(getVelocityVector().lengthSquared() < 1.0) {
			angAccel = 0.0;
			
			// add it back as a roll impulse so it's not abrupt
			if(angularVelocity != 0.0) {
				rotationalImpulse += -Math.signum(getSideSlipAngle()) * angularVelocity*1.8;
			}
			
			angularVelocity *= 0.2;
			
			
		}
		
		angularVelocity *= 0.99;
		angularVelocity += timeStep*angAccel;
		vehicle.rotationYaw += Math.toDegrees(timeStep*angularVelocity);
		
		vehicle.rotationYaw += vehicle.driftTuner;
		

		vehicle.steerangle += Math.toDegrees(timeStep*angularVelocity*-1)*0.02*rC;

		
		
		// pitching
		
		double forwardG = getVelocityVector().lengthVector()/getSideSlipAngle();
		vehicle.forwardLean = forwardG/inertia.m22;
		if(Double.isNaN(vehicle.forwardLean)) vehicle.forwardLean = 0.0;
		 
		
		
		
	}
	
	
	
	public void updatePosition() {
		timeStep = 0.01;
		Vec3d lForce = rearAxel.getLongitudinalForce()/*.rotatePitch((float) Math.toRadians(vehicle.rotationPitch))*/.rotateYaw((float) Math.toRadians(-vehicle.rotationYaw+vehicle.driftTuner));
		
		//lForce = lForce.scale(vehicle.rotationPitch/20);
		

		Vec3d latForce = rearAxel.adjLateralForce().add(frontAxel.adjLateralForce().scale(Math.cos(vehicle.steerangle)));
		Vec3d destructive = calculateResistiveForces(velocity);
		
		
		Vec3d vertForce = Vec3d.ZERO;
		
		
		if(!vehicle.onGround) {
		
			
			vertForce = new Vec3d(0, -vehicle.mass*(9.81)*2, 0);
		}
		
	
		
		
		Vec3d net = (lForce).add(latForce).add(destructive).add(vertForce);
		Vec3d acceleration = new Vec3d(net.x/vehicle.mass, net.y/vehicle.mass, net.z/vehicle.mass);
		;
		
		if(acceleration == null) return;
		
		
		// calculate velocity
		double xV = velocity.x + timeStep*acceleration.x;
		double yV = velocity.y + timeStep*acceleration.y;
		double zV = velocity.z + timeStep*acceleration.z;
		Vec3d newVel = new Vec3d(xV, yV, zV);
		velocity = newVel;
		
		
		double oYV = yV;
		
		
		
		
		
		
		
		velocity = new Vec3d(velocity.x, oYV, velocity.z);
		

		/*
		 * REVERSE VEHICLE, BAD METHOD, BUT IT WORKS.
		 */
		double rG = 1.0;
		if(transmission.isReverseGear) {
			rG = -1;
		}
		
		
		/*
		 * Some stability features
		 * 
		 */
		

		
			boolean wheelThrottle = vehicle.throttle == 0.0 || transmission.isEngineDeclutched();
		
			if(velocity.lengthVector() < 0.5 && wheelThrottle ) {
				velocity = velocity.scale(0.01);
				
			}
			
			if(velocity.lengthVector() < 0.03 && wheelThrottle) {

				
				velocity = Vec3d.ZERO;
			}
		
		
		
		// calculate position
		double xP = timeStep*velocity.x * rG;
		double yP = timeStep*velocity.y;
		double zP = timeStep*velocity.z * rG;

	
		//System.out.println(yP);
		
		
		this.vehicle.move(MoverType.SELF, xP, yP, zP);
	
		
	}
	
	
	

	
	public void updatePhysics() {
		
		vehicle.rotationYaw -= vehicle.driftTuner;
		updateEngineForces();
		updateLoad();
		updateWheels();
		//updateSuspensionPlatform();
		updateRotationalVelocity();
		updatePosition();
		
		
		
		
	}

	@Override
	public VehiclePhysicsSolver readFromBuf(ByteBuf buf) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * 
	 * 	
	public WheelAxel frontAxel;
	public WheelAxel rearAxel;
	public EntityVehicle vehicle;
	public Engine engine;
	int revolutions = 0;
	public double timeStep = 0.05;
	public Vec3d positonDelta = new Vec3d(0, 0, 0);
	double mass;
	public Vec3d velocity = new Vec3d(0, 0, 0);
	double brakeTorque = 12000;
	double COGHeight = 0.3;
	public Transmission transmission;
	
	public int currentRPM = 0;
	public int prevRPM = 0;
	
	// forces
	double angularVelocity = 0;
	double yawspeed = 0;
	
	// various properties
	double wheelBase = 1.0;
	
	// aceel
	public double synthAccelFor = 0.0;
	public double synthAccelSide = 0.0;
	
	
	// side and forward accel
	Vec3d sideForAccel = new Vec3d(0, 0, 0);
	Vector2d longLatVal = new Vector2d(0, 0);
	Vector2d accelLongLat = new Vector2d(0, 0);
	 */
	
	@Override
	public void writeToBuf(ByteBuf buf) {
		
		
		
		
		
	}
	

}
