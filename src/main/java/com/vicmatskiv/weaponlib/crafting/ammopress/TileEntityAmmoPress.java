package com.vicmatskiv.weaponlib.crafting.ammopress;

import com.vicmatskiv.weaponlib.crafting.base.TileEntityStation;

public class TileEntityAmmoPress extends TileEntityStation {
	
	
	private boolean makingBullets = false;
	private double currentWheelRotation = 0.0;
	private double prevWheelRotation = 0.0;
	
	public TileEntityAmmoPress() {
		// TODO Auto-generated constructor stub
	}
	
	public double getCurrentWheelRotation() {
		return currentWheelRotation;
	}
	
	public double getPreviousWheelRotation() {
		return prevWheelRotation;
	}
	
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		super.update();
		
		if(this.world.isRemote) {
			prevWheelRotation = currentWheelRotation;
			currentWheelRotation += Math.PI/32;
			
			if(currentWheelRotation >= 2*Math.PI) {
				prevWheelRotation = 0;
				currentWheelRotation = 0;
			}
			
			// pi/32 radians/tick
			
		}
		
	}

}
