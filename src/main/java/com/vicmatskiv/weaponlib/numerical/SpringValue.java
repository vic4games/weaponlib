package com.vicmatskiv.weaponlib.numerical;

import com.vicmatskiv.weaponlib.animation.MatrixHelper;

import net.minecraft.client.Minecraft;

public class SpringValue {
	public double springConstant = 400;
	public double mass = 40;
	public double damping = 90;
	public double velocity = 0;
	public double position = 0;
	
	// for rendering
	public double prevPosition = 0;
	
	public SpringValue(double k, double mass, double damp) {
		this.springConstant = k;
		this.mass = mass;
		this.damping = damp;
	}
	
	public void update(double dt) {
		double force = -springConstant*(position);
		double dampingForce = damping*velocity;
		double appliedForce = force - dampingForce;
		double acceleration = appliedForce/mass;
		
		prevPosition = position;
		
		velocity += acceleration*dt;
		position += velocity*dt;
	}

	public double getSpringConstant() {
		return springConstant;
	}
	
	public void configure(double k, double mass, double dampening) {
		this.springConstant = k;
		this.mass = mass;
		this.damping = dampening;
	}

	public void setSpringConstant(double springConstant) {
		this.springConstant = springConstant;
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public double getDamping() {
		return damping;
	}

	public void setDamping(double damping) {
		this.damping = damping;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public double getPosition() {
		return position;
	}
	
	public double getLerpedPosition() {
		return MatrixHelper.solveLerp((float) this.prevPosition, (float) this.position, Minecraft.getMinecraft().getRenderPartialTicks());
	}
	
	public float getLerpedFloat() {
		return (float) getLerpedPosition();
	}

	public void setPosition(double position) {
		this.position = position;
	}

}
