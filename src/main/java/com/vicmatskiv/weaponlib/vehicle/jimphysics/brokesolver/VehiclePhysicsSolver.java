package com.vicmatskiv.weaponlib.vehicle.jimphysics.brokesolver;

import java.util.ArrayList;

import javax.vecmath.Vector2d;


import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.Engine;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.VehiclePhysUtil;

import net.minecraft.entity.MoverType;
import net.minecraft.util.math.Vec3d;

public class VehiclePhysicsSolver {
	

	
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
	
	
	public VehiclePhysicsSolver(EntityVehicle vehicle, double mass) {
		this.vehicle = vehicle;
		this.engine = vehicle.engine;
		initTestingVehicle();
	}
	
	public Vec3d getOreintationVector() {
		return Vec3d.fromPitchYaw(vehicle.rotationPitch, vehicle.rotationYaw);
	}
	
	public Vec3d getVelocityVector() {
		return velocity;
	}
	
	public void applyHandbrake() {
		
		rearAxel.applyHandbrake();
	}
	
	public void releaseHandbrake() {
		
		rearAxel.releaseHandbrake();
	}
	
	public double getSideSlipAngle() {
		Vec3d uVec = getOreintationVector();
		
		Vector2d u2D = new Vector2d(uVec.x, uVec.z);
		Vector2d v2D = new Vector2d(velocity.x, velocity.z);
		
		
	
		v2D.normalize();
		double mult = Math.signum(uVec.crossProduct(velocity).y);
		return u2D.angle(v2D) * mult;
	}
	
	public void initTestingVehicle() {
		frontAxel = new WheelAxel(this, 0.5, false);
		rearAxel = new WheelAxel(this,-0.5, true);
		WheelSolver f1 = new WheelSolver(this, frontAxel, 75.0, 0.33, false);
		WheelSolver f2 = new WheelSolver(this, frontAxel, 75.0, 0.33, false);
		WheelSolver r1 = new WheelSolver(this, rearAxel, 75.0, 0.33, true);
		WheelSolver r2 = new WheelSolver(this, rearAxel, 75.0, 0.33, true);
		frontAxel.addWheels(f1, f2);
		rearAxel.addWheels(r1, r2);
		
		
	}
	
	public double getLongitudinalSpeed() {
		if(Double.isNaN(velocity.lengthVector())) return vehicle.throttle;
		
		return velocity.lengthVector()+vehicle.throttle;
	}
	
	public void updateEngineForces() {
		double gearRatio = 2.785;
		
		int rpm = (int) VehiclePhysUtil.getEngineRPM(rearAxel.getWheelAngularVelocity(), gearRatio, 3.312);
		if(rpm < 1000) {
			rpm = 1001;
		}
		if(rpm > 7000) {
			rpm = 7000;
		}
		
		
		double torque = engine.getTorqueAtRPM(rpm);
		double drvT = VehiclePhysUtil.getDriveTorque(torque, gearRatio, 3.312, 1.0)*(vehicle.throttle);
		
		synthAccelFor += drvT*timeStep/10;
		
		
		rearAxel.applyDriveTorque(drvT);
	}
	
	public void updateLoad() {
		double weight = vehicle.mass*9.81;
		double accel = synthAccelFor;
		double weightFront = (frontAxel.COGoffset/wheelBase)*weight - (COGHeight/wheelBase)*vehicle.mass*accel;
		double weightRear = (rearAxel.COGoffset/wheelBase)*weight - (COGHeight/wheelBase)*vehicle.mass*accel;
		
		
		
		double newSynth = ((Math.abs(synthAccelFor) * 0.8))*Math.signum(synthAccelFor);
		double newSynthSide = Math.toDegrees(vehicle.steerangle)/5;

		synthAccelFor = newSynth;
		
		vehicle.forwardLean = accel/6;
		vehicle.sideLean = (accel/12) + newSynthSide;
		
		//System.out.println(weightFront + " | " + weightRear);
		
		rearAxel.distributeLoad(vehicle.mass*9.81);
		frontAxel.distributeLoad(vehicle.mass*9.81);
	}
	
	
	
	
	public void updateWheels() {
		if(vehicle.isBraking) {
			synthAccelFor -= 3;
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
		
		// the -1 is a hack
		
		double torqueContributionRear = rearAxel.latNonVec()*rearAxel.COGoffset;
		double torqueContributionFront = Math.cos(vehicle.steerangle)*frontAxel.latNonVec()*frontAxel.COGoffset;
		//System.out.println(torqueContributionFront + " | " + torqueContributionRear + " | " + (torqueContributionFront + torqueContributionRear));
		
		//System.out.println(frontAxel.latNonVec());
		
		/*
		double torqueContR = rearAxel.adjLateralForce().lengthVector()*rearAxel.COGoffset;
		double torqueContF = Math.cos(vehicle.steerangle)*frontAxel.adjLateralForce().lengthVector()*frontAxel.COGoffset;
		*/
		
		double totalAxelTorque = torqueContributionFront + torqueContributionRear;
		
		double angAccel = totalAxelTorque/2200;
		angularVelocity *= 0.99;
		angularVelocity += timeStep*angAccel;
		vehicle.rotationYaw += Math.toDegrees(timeStep*angularVelocity);
		
		vehicle.rotationYaw += vehicle.driftTuner;
		
		vehicle.steerangle += Math.toDegrees(timeStep*angularVelocity*-1)*0.02;
		
	}
	
	
	
	public void updatePosition() {
		timeStep = 0.01;
		// calculate acceleration
		Vec3d lForce = rearAxel.getLongitudinalForce().rotateYaw((float) Math.toRadians(-vehicle.rotationYaw+vehicle.driftTuner));
		
		//System.out.println(lForce);
		
		//System.out.println(lForce);
		//Vec3d latForce = frontAxel.adjLateralForce().add(rearAxel.adjLateralForce());
		
		Vec3d latForce = rearAxel.adjLateralForce().add(frontAxel.adjLateralForce().scale(Math.cos(vehicle.steerangle)));
		
		Vec3d destructive = calculateResistiveForces(velocity);
		
		Vec3d vertForce = Vec3d.ZERO;
		//System.out.println(vehicle.onGround);
		if(!vehicle.onGround) {
			vertForce = new Vec3d(0, -vehicle.mass*9.81, 0);
		}
		
		Vec3d net = (lForce).add(latForce).add(destructive).add(vertForce);
		
		Vec3d acceleration = new Vec3d(net.x/vehicle.mass, net.y/vehicle.mass, net.z/vehicle.mass);
		
		
		if(acceleration == null) return;
		
		
		// calculate velocity
		double xV = velocity.x + timeStep*acceleration.x;
		double yV = velocity.y + timeStep*acceleration.y;
		double zV = velocity.z + timeStep*acceleration.z;
		Vec3d newVel = new Vec3d(xV, yV, zV);
	
		/*
		if(this.stepCount == 4 && this.secStep == 4) {
			Vec3d tVec = velocity.rotateYaw((float) Math.toRadians(vehicle.rotationYaw));
			Vec3d nComp = newVel.rotateYaw((float) Math.toRadians(vehicle.rotationYaw));
			double x = Math.round(tVec.z*100)/100.0;
			double x2 = Math.round(nComp.z*100)/100.0;
			double diff = (x2-x)/timeStep;
			if(!vehicle.isBraking) {
				diff = Math.abs(diff);
			}
			diff = diff;
			sideForAccel = new Vec3d(diff, 0, 0);
		}
		
		//sideForAccel = new Vec3d(diff, 0, 0); 
		//System.out.println(x2-x);
		if(this.stepCount == 4) {
			double tLat = Math.sin(Math.toRadians(vehicle.rotationYaw)) * velocity.lengthVector();
			double tLong = Math.cos(Math.toRadians(vehicle.rotationYaw)) *  velocity.lengthVector();
			Vector2d tLongLat = new Vector2d(tLong, tLat);
			Vector2d diffVec = new Vector2d();
			diffVec.sub(tLongLat, longLatVal);
			//System.out.println(diffVec.x);
			longLatVal = tLongLat;
		}*/
		
		
		
		
		
		
		//Vec3d tV2 = nComp.subtract(tVec);
		//System.out.println(tV2);
		//Vec3d acP = newVel.subtract(velocity);
		//sideForAccel = new Vec3d(acP.x/timeStep, acP.y/timeStep, acP.z/timeStep).rotateYaw((float) Math.toRadians(vehicle.rotationYaw));
		//double ac = (newVel.lengthVector()-velocity.lengthVector())/timeStep;
		velocity = newVel;
		
		//velocity = new Vec3d(0.1, 0.0, 0.1);
		
		
		//System.out.println(velocity + " | " + Math.toDegrees(getSideSlipAngle()));
		
		// calculate position
		
		double xP = timeStep*velocity.x;
		double yP = timeStep*velocity.y;
		double zP = timeStep*velocity.z;
		
		/*
		Vec3d newPos = new Vec3d(xP, yP, zP);
		double sFAX = (newPos.lengthVector()-positonDelta.lengthVector())/timeStep;
		sideForAccel = new Vec3d(sFAX, 0, 0);
		*/
		
		
		
		
		
		this.vehicle.move(MoverType.SELF, xP, yP, zP);
		
	}
	
	
	
	
	
	
	public void updatePhysics() {
		vehicle.rotationYaw -= vehicle.driftTuner;
		updateEngineForces();
		updateLoad();
		updateWheels();
		updateRotationalVelocity();
		updatePosition();
	}
	

}
