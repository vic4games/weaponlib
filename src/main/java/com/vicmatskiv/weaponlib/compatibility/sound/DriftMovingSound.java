package com.vicmatskiv.weaponlib.compatibility.sound;

import java.util.function.Supplier;

import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;
import com.vicmatskiv.weaponlib.compatibility.CompatibleVec3;
import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;

public class DriftMovingSound extends AdvCompatibleMovingSound {
	
	public EntityVehicle vehicle;

	public DriftMovingSound(CompatibleSound sound, Supplier<CompatibleVec3> positionProvider,
			Supplier<Boolean> donePlayingProvider, EntityVehicle vehicle, boolean shouldFade) {
		super(sound, positionProvider, donePlayingProvider, shouldFade);
		
		this.vehicle = vehicle;
	}
	
	@Override
	public void update() {
		super.update();
		
		
		//System.out.println("fortnite: " + ((float) Math.abs(vehicle.getSolver().getSideSlipAngle())*2.0));
		//System.out.println(this.donePlaying);
		if(!isDonePlaying()) {
			this.volume = (float) ((float) Math.abs(vehicle.getSolver().getSideSlipAngle())*2.0);
			//this.pitch = (float) 2.0*vehicle.getSolver().getVelocityVector().normalize();
		}
	}

}