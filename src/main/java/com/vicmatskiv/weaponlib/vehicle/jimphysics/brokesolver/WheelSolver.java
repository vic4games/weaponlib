package com.vicmatskiv.weaponlib.vehicle.jimphysics.brokesolver;



import com.vicmatskiv.weaponlib.vehicle.jimphysics.VehiclePhysUtil;

import net.minecraft.util.math.Vec3d;

public class WheelSolver {
	
	VehiclePhysicsSolver solver;
	
	private WheelAxel axel;
	double radius = 0.0;
	double wheelAngularVelocity = 0.0;
	double wheelAngularAcceleration = 0.0;
	double wheelAngle = 0.0;
	double wheelInertia = 0.0;
	double loadOnWheel = 0.0;
	Vec3d wheelOreintation = new Vec3d(0, 0, 1);
	
	double driveTorque;
	double tractionTorque;
	double lateralForce = 0.0;
	double longForce = 0.0;
	
	boolean isDrive;
	
	// LAT & LONG FORCE
	public Vec3d longitudinalForce = new Vec3d(0, 0, 0);
	public Vec3d lateralForceVec = new Vec3d(0, 0, 0);
	
	
	public WheelSolver(VehiclePhysicsSolver solver, WheelAxel axel, double mass, double radius, boolean isDrive) {
		this.radius = radius;
		this.axel = axel;
		this.solver = solver;
		this.wheelInertia = VehiclePhysUtil.inertiaOfACylinder(mass, radius);
		this.isDrive = isDrive;
	}

	
	public void doPhysics() {
		
		
		
		// update angular velocity
		wheelAngularVelocity += wheelAngularAcceleration*solver.timeStep;
	
		
		// reset accel
		wheelAngularAcceleration = 0;
		
		// update wheel oreintation
		Vec3d omega = wheelOreintation.rotateYaw((float) wheelAngle);
		
		
		
		// get slip ratio
		double slipRatio = VehiclePhysUtil.getSlipRatio(wheelAngularVelocity, radius, solver.getLongitudinalSpeed());
	
		
		// sometimes this can actually be NaN which can cause errors.
		if(Double.isNaN(slipRatio)) {
			longitudinalForce = Vec3d.ZERO;
			return;
		}
		
		
		
		// get longitundinal force
		longForce = VehiclePhysUtil.pacejkaLong(loadOnWheel, slipRatio, 1.65, 1, 0.97, 10);
		if(Double.isNaN(longForce)) {
			longForce = 0.0;
		}
		
		longitudinalForce = omega.scale(longForce);
		
		
		// calculate the traction torque
		
		//tractionTorque = longForce*0.9*loadOnWheel*radius*-1/10000;
		
	   tractionTorque = longForce*radius*-1;
		
		
		
		
		
		
		/*
		 * LATERAL FORCES :)
		 */
		
		
		// finds yawspeed - I put absoplute around the offset so might cause errors?
		double yawspeed = solver.wheelBase * axel.COGoffset * solver.angularVelocity;
		
		// finds rotAngle
		double rot_angle = Math.atan(yawspeed / solver.getLongitudinalSpeed());
		
		// finds the sideslip angle
		double sideSlip = solver.getSideSlipAngle();
		
		//System.out.println("YAW: " + Math.toDegrees(yawspeed) + " | ROT: " + Math.toDegrees(rot_angle) + " | SIDE: " + Math.toDegrees(sideSlip));
		// calculates the slipangle
		
	
		double slipAngleTire;
		if(axel.COGoffset < 0) {
			
			slipAngleTire = sideSlip - rot_angle - wheelAngle;
		} else {
			slipAngleTire = sideSlip + rot_angle - wheelAngle;
		}
		slipAngleTire = Math.toDegrees(slipAngleTire);
		
		
		
		// useful sysout for debugging.
		//System.out.println("YAW: " + yawspeed + " | ROT_ANGLE: " + rot_angle + " | SIDE SLIP: " + sideSlip);
		
		
		// calculates the lateral forces
		
		lateralForce = VehiclePhysUtil.pacejkaLong(loadOnWheel, slipAngleTire, 1.3, 1, 0.97, 10);
		
		
		/*
		if(axel.isHandbraking) {
			if(axel.COGoffset > 0) {
				lateralForce *= solver.vehicle.driftTuner;
			} else {
				lateralForce *= 0.5;
			}
		}
		*/
		
		
		if(axel.isHandbraking) {
			lateralForce *= 0.5;
		}
		
		if(Double.isNaN(lateralForce)) {
			lateralForce = 0.0;
			lateralForceVec = Vec3d.ZERO;
		} else {
			this.lateralForceVec = wheelOreintation.rotateYaw((float) Math.toRadians(-90)).scale(lateralForce);
			
		}
		
		
		
	
		
		
		
		
		
	}

}
