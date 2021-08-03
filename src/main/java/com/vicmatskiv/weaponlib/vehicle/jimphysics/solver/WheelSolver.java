package com.vicmatskiv.weaponlib.vehicle.jimphysics.solver;



import org.lwjgl.input.Keyboard;

import com.vicmatskiv.weaponlib.network.IEncodable;
import com.vicmatskiv.weaponlib.vehicle.collisions.InertiaKit;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.InterpolationKit;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.VehiclePhysUtil;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.stability.numerical.vehicle.RK4Wheel;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.stability.numerical.vehicle.WheelSolutionVector;

import io.netty.buffer.ByteBuf;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;

public class WheelSolver implements IEncodable<WheelSolver>{
	
	
	public double actualRideHeight = 0;
	
	public double springRate = 271;
	
	VehiclePhysicsSolver solver;
	
	public SuspensionSolver suspension;
	
	
	public WheelAxel axel;
	public double radius = 0.0;
	public double wheelAngularVelocity = 0.0;
	double wheelAngularAcceleration = 0.0;
	public double wheelAngle = 0.0;
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
	
	/*
	 * Set by external things
	 */
	public Vec3d relativePosition = Vec3d.ZERO;
	public double rideHeight;
	
	
	public double wheelRot = 0.0;
	public double prevWheelRot = 0.0;
	
	public WheelSolutionVector state = new WheelSolutionVector();
	
	
	/**
	 * https://tiresize.com/calculator/
	 * This is FANTASTIC!
	 * 
	 */
	
	/**
	 * Creates a new wheel solver
	 * 
	 * @param solver vehicle physics solver
	 * @param axel axel solver
	 * @param mass mass of tire in (kg)
	 * @param radius radius of tire in (m)
	 * @param thickness thickness of tire in (m)
	 * @param isDrive Does the wheel get powered from the engine?
	 */
	public WheelSolver(double springRate, double mass, double radius, double thickness, boolean isDrive) {
		
		this.suspension = new SuspensionSolver(springRate, 1.0);
		
		this.radius = radius;
		//this.axel = axel;
		this.solver = solver;
		
		// calculates the wheel's inertia, only ar
		this.wheelInertia = InertiaKit.inertiaTensorCylinder((float) mass, (float) radius, (float) thickness).m22;
		this.isDrive = isDrive;
	}
	
	public void assignSolver(VehiclePhysicsSolver solver) {
		this.solver = solver;
		
	}
	
	public double getInterpolatedWheelRotation() {
		return InterpolationKit.interpolateValue(prevWheelRot, wheelRot, Minecraft.getMinecraft().getRenderPartialTicks());
	}
	
	public Vec3d getSuspensionPosition() {
		Vec3d relative = this.relativePosition;
		relative = relative.addVector(0.0, getSuspension().getStretch()*-0.15, 0.0);
		return relative;
	}

	public void setRelativePosition(Vec3d rP) {
		this.relativePosition = rP;
	}
	 
	public boolean isDriveWheel() {
		return this.axel.isDriveWheel;
	}
	
	public void applySuspensionLoad(double force) {
		this.suspension.applyForce(force);
	}
	
	
	public SuspensionSolver getSuspension() {
		return this.suspension;
	}
	
	public double getRenderRideHeight() {
		double d = this.rideHeight;
		if(this.axel.solver.vehicle.rideOffset < 0) {
			d += this.axel.solver.vehicle.rideOffset*1.75;
		}
		return d;
	}
	
	
	/**
	 * Applies a braking force to the wheel
	 * @param magnitude 1.0-0.0, lower vals = higher braking
	 */
	public void applyBrake(double magnitude) {
		wheelAngularAcceleration = -magnitude;
		wheelAngularVelocity = 0;
	}
	
	double oldWheelVel = 0;
	
	public void doPhysics() {
		wheelAngularVelocity += wheelAngularAcceleration*solver.timeStep;
		
		
		// UPDATES THE WHEEL ROTATION
		prevWheelRot = wheelRot;
		wheelRot += wheelAngularVelocity*solver.timeStep*20;
		
		
		
		// prevents the user from going too fast backwards
		// in reverse. real life example = Mercedes G-Wagon.
		if(solver.transmission.isReverseGear) {
			if(wheelAngularVelocity > 20) wheelAngularVelocity = 20;
		}
		
		
		// RESETS THE WHEEL ANGULAR ACCELERATION
		wheelAngularAcceleration = 0;
		
		// update wheel oreintatio
		Vec3d omega = wheelOreintation.rotateYaw((float) wheelAngle);
		
		
		
		// get slip ratio
		double slipRatio = VehiclePhysUtil.getSlipRatio(wheelAngularVelocity, radius, solver.getLongitudinalSpeed());
		
		

	
		if(solver.getVelocityVector().lengthSquared() > 3 && solver.getVelocityVector().dotProduct(Vec3d.fromPitchYaw(0.0f, solver.vehicle.rotationYaw)) < 0) {
			   solver.velocity = solver.velocity.scale(0.03);
			}
		
		
		
		
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
		

		
		
		
		// REDUCES THE GRIP ON OTHER MATERIALS (DIRT)
		longitudinalForce = omega.scale(longForce);
		if(this.axel.solver.materialBelow != Material.ROCK) {
			longitudinalForce = longitudinalForce.scale(0.5);
		}
		
		// CALCULATES THE TRACTION TORQUE
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
		
		
		// LATERAL FORCE
		// https://www.edy.es/dev/docs/pacejka-94-parameters-explained-a-comprehensive-guide/
		// (SHOULD BE UPGRADED FROM '94 FORMULA)
		lateralForce = VehiclePhysUtil.pacejkaLong(loadOnWheel, slipAngleTire, 1.3, 1.0, 1.0, 4);
		
		// APPLIES THE FX OF HANDBRAKE
		if(axel.isHandbraking) {
			if(!(Math.abs(Math.toDegrees(solver.vehicle.steerangle)) > 4)) {
				if(this.axel.solver.materialBelow == Material.ROCK) {
					axel.applyBrakingForce(40);
				} else {
					axel.applyBrakingForce(10);
				}
			}
			lateralForce *= 0.15;
		}
		
		
		// REDUCES GRIP ON DIRT
		if(this.axel.COGoffset < 0 && this.axel.solver.materialBelow != Material.ROCK) {
			lateralForce *= 0.5;
		} 

		
		double absSlip = Math.abs(slipAngleTire);
		
		if(this.axel.COGoffset < 0) {
			if(this.axel.solver.materialBelow != Material.ROCK) {
				if(absSlip > 1.5) lateralForce *= 0.6;
			} else {
				
				
				if(absSlip > 0.2 && absSlip < 4.5) {
					
					lateralForce *= 0.4;
				} else if(absSlip > 4.5) {
					lateralForce *= 0.8;
				}

			}
		}
		
	
		
		
		
		
		
		// if(absSlip > 1.5 && this.axel.COGoffset < 0) lateralForce *= kC;
		
		// PREVENTS THE LATERAL FORCE VALUE FROM GOING NAN
		if(Double.isNaN(lateralForce)) {
			lateralForce = 0.0;
			lateralForceVec = Vec3d.ZERO;
		} else {
			this.lateralForceVec = wheelOreintation.rotateYaw((float) Math.toRadians(-90)).scale(lateralForce);
			
		}
		
		
		
	
		
		
		
		
		
	}

	public WheelSolver withRelativePosition(Vec3d vec) {
		setRelativePosition(vec);
		return this;
		
	}
	

	@Override
	public WheelSolver readFromBuf(ByteBuf buf) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void writeToBuf(ByteBuf buf) {
		
	}

}
