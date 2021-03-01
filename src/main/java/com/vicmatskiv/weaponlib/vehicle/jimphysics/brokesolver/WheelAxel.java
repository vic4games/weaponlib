package com.vicmatskiv.weaponlib.vehicle.jimphysics.brokesolver;

import java.sql.Driver;

import net.minecraft.util.math.Vec3d;

public class WheelAxel {
	
	public VehiclePhysicsSolver solver;
	
	public WheelSolver leftWheel;
	public WheelSolver rightWheel;
	double loadOnAxel;
	public boolean isDriveWheel;
	public double COGoffset;
	public boolean isHandbraking;
	
	public WheelAxel(VehiclePhysicsSolver solver, double offsetFromCOG, boolean isDriveWheel) {
		this.solver = solver;
		this.isDriveWheel = isDriveWheel;
		this.COGoffset = offsetFromCOG;
	}
	
	public void addWheels(WheelSolver left, WheelSolver right) {
		this.leftWheel = left;
		this.rightWheel = right;
	}
	
	
	public void applyHandbrake() {
		this.isHandbraking = true;
	}
	
	public void releaseHandbrake() {
		this.isHandbraking = false;
	}
	
	public void applyBrakingForce(double magnitude) {
		leftWheel.wheelAngularVelocity *= magnitude;
		rightWheel.wheelAngularVelocity *= magnitude;
		leftWheel.wheelAngularAcceleration *= magnitude;
		rightWheel.wheelAngularAcceleration *= magnitude;
	}
	
	public void setSteeringAngle(double angle) {
		leftWheel.wheelAngle = -angle;
		rightWheel.wheelAngle = -angle;
	}
	
	public Vec3d getLongitudinalForce() {
		return leftWheel.longitudinalForce.add(rightWheel.longitudinalForce);
	}
	
	public double latNonVec() {
		return leftWheel.lateralForce + rightWheel.lateralForce;
	}
	
	public double longNonVec() {
		return leftWheel.longForce + rightWheel.longForce;
	}
	
	public Vec3d getLateralForce() {
		return leftWheel.lateralForceVec.add(rightWheel.lateralForceVec);
	}
	
	public Vec3d adjLateralForce() {
		return getLateralForce().rotateYaw((float) Math.toRadians(-solver.vehicle.rotationYaw+solver.vehicle.driftTuner));
	}
	
	public double getWheelAngularVelocity() {
		return this.leftWheel.wheelAngularVelocity;
	}
	
	public void applyDriveTorque(double torque) {
		leftWheel.driveTorque += torque/2;
		rightWheel.driveTorque += torque/2;
		
	}
	
	public void distributeLoad(double load) {
		this.loadOnAxel = load;
		
		leftWheel.loadOnWheel = load;
		rightWheel.loadOnWheel = load;
	}
	
	public void doPhysics() {
		leftWheel.doPhysics();
		rightWheel.doPhysics();
		
		double drTorque = leftWheel.driveTorque + rightWheel.driveTorque;
		
		double totalTorque = drTorque + leftWheel.tractionTorque + rightWheel.tractionTorque;
		
		double inertia = leftWheel.wheelInertia + rightWheel.wheelInertia;
		double angularAccel = totalTorque/inertia;
		
		leftWheel.wheelAngularAcceleration = angularAccel;
		rightWheel.wheelAngularAcceleration = angularAccel;
		
		
		
		// resetTorque
		leftWheel.driveTorque = 0;
		rightWheel.driveTorque = 0;
		
		
	}

}
