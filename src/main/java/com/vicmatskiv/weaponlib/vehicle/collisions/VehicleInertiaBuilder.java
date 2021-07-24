package com.vicmatskiv.weaponlib.vehicle.collisions;

import java.util.ArrayList;

import javax.vecmath.Matrix3f;

import net.minecraft.client.renderer.Vector3d;
import net.minecraft.util.math.Vec3d;

/**
 * Inertia builder based on the paper 
 * 
 * A Comparison of Moment of Inertia Estimation Techniques for Vehicle Dynamics Simulation,
 * https://www.jstor.org/stable/44731313?read-now=1&seq=4#page_scan_tab_contents
 * 
 * @author Jim Holden, 2021
 *
 */
public class VehicleInertiaBuilder {
	
	private Matrix3f tensor = new Matrix3f();
	private double vehicleMass = 0;
	private ArrayList<InertiaObject> inertiaObjectList = new ArrayList<>();
	
	public VehicleInertiaBuilder(double mass) {
		this.vehicleMass = mass;
	}
	
	/*
	 * High level constructions
	 */
	
	public void basicSedanConstruct(Vec3d d, float heightOffGround, float wheelBase, float wheelRadius, float wheelThickness) {
		
		
		addBasicBody(new Vec3d(d.x, d.y/2, d.z), heightOffGround, 0.5f, 0.5f, 5.05f);
		addTop(new Vec3d(d.x, d.y/2, d.z), heightOffGround, (float) (d.y/2f), 0.5f, 0.2f, 3.03f);
		addWheelAssembly(2, heightOffGround, wheelBase, wheelRadius, wheelThickness, (float) d.x, 3.03f);
		addPowerLine(d.scale(0.2), wheelBase, heightOffGround, 0.1f);
		
		
	}
	
	/*
	 * Mid level constructions
	 */
	
	
	/*
	 * Advanced constructions
	 */
	
	
	/**
	 * adds top
	 */
	public void addTop(Vec3d bodyDimensions, float bodyHeight, float topHeight, float a, float b, float density) {
		Vec3d topDim = new Vec3d(bodyDimensions.x, topHeight, bodyDimensions.z-a-b);
		addCube(new Vec3d(0.0, bodyHeight+bodyDimensions.y, bodyDimensions.z-a), topDim, density);
	}
	
	/**
	 * Adds a basic body with weight distribution
	 * @param dimensions
	 * @param height
	 * @param rearDistro
	 * @param frontDistro
	 * @param density
	 */
	public void addBasicBody(Vec3d dimensions, float height, float rearDistro, float frontDistro, float density) {
		Vec3d halvedDim = new Vec3d(dimensions.x, dimensions.y, dimensions.z/2);
		addCube(new Vec3d(0.0, height, dimensions.z/2), halvedDim, density*frontDistro);
		addCube(new Vec3d(0.0, height, -dimensions.z/2), halvedDim, density*rearDistro);
		
	}
	
	
	/**
	 * Adds an engine, flywheel, transmission, and driveLine
	 * @param engineDimensions
	 * @param wheelBase
	 * @param height
	 * @param flywheelRadius
	 */
	public void addPowerLine(Vec3d engineDimensions, float wheelBase, float height, float flywheelRadius) {
		
		double sP = wheelBase/2;
		
		// add engine
		Matrix3f engineTensor = InertiaKit.inertiaTensorCube(1.0f, (float) engineDimensions.x, (float) engineDimensions.y, (float) engineDimensions.z);
		addCube(new Vec3d(0.0, height, sP), engineDimensions, 8.05);
		
		sP -= engineDimensions.z;
		
		// add flywheel
		Matrix3f flywheelTensor = InertiaKit.inertiaTensorCylinder(2.5f, flywheelRadius, 0.01f);
		addCylinder(new Vec3d(0.0, height-(engineDimensions.y/2), sP), flywheelRadius, 0.01f, 8.05);
		
		sP -= 0.01f;
		
		// add transmission
		float transmissionLen = (float) (engineDimensions.z/1.85f);
		float transmissionRadius = (float) ((engineDimensions.x + engineDimensions.z)/2.5f);
		Matrix3f transTensor = InertiaKit.inertiaTensorCylinder(1.0f, transmissionRadius, transmissionLen);
		addCylinder(new Vec3d(0.0, height-(engineDimensions.y/2), sP), transmissionRadius, transmissionLen, 3.03);
		
		sP -= transmissionLen;
		
		// add drive line
		
		float driveLineLength = (float) Math.abs((-wheelBase/2)-sP);
		float driveLineRadius = transmissionRadius/3f;
		Matrix3f driveLineTensor = InertiaKit.inertiaTensorCylinder(1.0f, driveLineRadius, driveLineLength);
		addCylinder(new Vec3d(0.0, height-(engineDimensions.y/1.8), sP), driveLineRadius, driveLineLength, 3.03);
		
		
		
		
		
		
		
	}
	
	
	/**
	 * Adds a wheel assembly
	 * 
	 * @param axelCount amount of axles
	 * @param height height of wheel assembly above ground
	 * @param wheelBase wheel base length
	 * @param radius wheel radius
	 * @param depth wheel thickness
	 * @param axelLen dist between wheels on an axis
	 * @param density density of objects
	 */
	public void addWheelAssembly(int axelCount, float height, float wheelBase, float radius, float depth, float axelLen, float density) {
		for(int n = 0; n < axelCount; ++n) {
			double distAlong = getPosInBetween(0, n, axelCount);
			double posAlongBase = interpValue(-wheelBase/2, wheelBase/2, distAlong);
			addAxel(height, radius, axelLen, (float) posAlongBase, depth, density);
		}
	}
	
	public void addAxel(float height, float wheelRadius, float axelLen, float distForward, float wheelThickness, float density) {
		
		addCylinder(new Vec3d(-axelLen/2, height, distForward), wheelRadius, wheelThickness, density);
		
		// axel
		addCylinder(new Vec3d(0, height, distForward), wheelRadius/3, axelLen, density);
		
		addCylinder(new Vec3d(axelLen/2, height, distForward), wheelRadius, wheelThickness, density);
		
		
		
	}
	
	
	
	/*
	 * Basic building objects -
	 * objects that form the basis for constructing a real
	 * inertia object.
	 */
	
	
	
	
	/**
	 * Adds an inertia cube to the builder
	 * 
	 * @param offset
	 * @param dimensions (h, w, d)
	 * @param volume
	 * @param density
	 */
	public void addCube(Vec3d offset, Vec3d dimensions, double density) {
		double volume = dimensions.x*dimensions.y*dimensions.z;
		Matrix3f tens = InertiaKit.inertiaTensorCube(1.0f, (float) dimensions.x, (float) dimensions.y, (float) dimensions.z);
		inertiaObjectList.add(new InertiaObject(offset, tens, 0.0, volume, density));
	}
	
	
	public void addCylinder(Vec3d offset, float radius, float depth, double density) {
		double volume = Math.PI*radius*radius*depth;
		Matrix3f tens = InertiaKit.inertiaTensorCylinder(1.0f, radius, depth);
		inertiaObjectList.add(new InertiaObject(offset, tens, 0.0, volume, density));
	}
	
	
	
	
	public void assignMass() {
		
		double sum = 0;
		for(InertiaObject iO : inertiaObjectList) {
			double massFactor = iO.getMassFactor();
			sum += massFactor;
		}
		
		for(InertiaObject iO : inertiaObjectList) {
			double r = (iO.getMassFactor()/sum)*this.vehicleMass;
			iO.setRealMass(r);
			
		}
		
 	}
	
	
	public Matrix3f build() {
		
		assignMass();
		
		Vec3d local = Vec3d.ZERO;
		double mass = 0;
		for(InertiaObject iO : inertiaObjectList) {
			mass += iO.mass;
			local = local.add(iO.pos.scale(iO.mass));
		}
		
		double iM = 1/mass;
		local = local.scale(iM);
		
		for(InertiaObject iO : inertiaObjectList) {
			
			Vec3d toLoc = local.subtract(iO.pos);
			double dist = toLoc.dotProduct(toLoc);
			
			
			Matrix3f oP = outerProduct(toLoc, toLoc);
			Matrix3f tensMat = new Matrix3f();
			tensMat.setIdentity();
			tensMat.mul((float) dist);
			tensMat.sub(oP);
			tensMat.mul((float) iO.mass);
			
			Matrix3f oLocalTensor = (Matrix3f) iO.tensor.clone();
			oLocalTensor.add(tensMat);
			this.tensor.add(oLocalTensor);
			
		}
		
		
		
		
		return tensor;
		
	}
	
	
	/*
	 * Required maths
	 */
	
	public Matrix3f outerProduct(Vec3d one, Vec3d two) {
		Matrix3f mat = new Matrix3f(
				(float)(one.x*two.x), (float)(one.x*two.y), (float)(one.x*two.z),
				(float)(one.y*two.x), (float)(one.y*two.y), (float)(one.y*two.z),
				(float)(one.z*two.x), (float)(one.z*two.y), (float)(one.z*two.z));
		return mat;
	}
	
	public double getPosInBetween(double start, double pos, double end) {
		return (pos-start)/(end-start);
	}
	
	public double interpValue(double start, double end, double mu) {
		return start + (end-start)*mu;
	}
	
	
	class InertiaObject {
		
		public Vec3d pos = Vec3d.ZERO;
		public double volume = 0;
		public double density = 0;
		public Matrix3f tensor = new Matrix3f();
		public double mass = 0;
		
		public InertiaObject(Vec3d p,  Matrix3f t, double m, double volume, double density) {
			this.pos = p;
			this.tensor = t;
			this.mass = m;
			this.density = density;
			this.volume = volume;
		}
		
		public double getMassFactor() {
			return density*volume;
		}
		
		public void setRealMass(double mass) {
			this.tensor.mul((float) mass);
			this.mass = mass;
		}
		
		
		
		
	}
	

}
