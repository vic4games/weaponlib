package com.vicmatskiv.weaponlib.numerical;

public class FrameValue implements ISimulator {
	
	public double value;
	public double speed;
	
	public FrameValue(double speed) {
		this.speed = speed;
	}

	@Override
	public void update(double dt) {
		
		value -= speed;
		if(value < 0) {
			value = 0;
		}
		
	}

}
