package com.vicmatskiv.weaponlib.compatibility.sound;

import java.util.function.Supplier;

import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;
import com.vicmatskiv.weaponlib.compatibility.CompatibleVec3;
import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.renderer.vertex.VertexBuffer;

public class EngineMovingSound extends AdvCompatibleMovingSound {

	public EntityVehicle vehicle;
	
	public EngineMovingSound(CompatibleSound sound, Supplier<CompatibleVec3> positionProvider,
			Supplier<Boolean> donePlayingProvider, EntityVehicle vehicle, boolean shouldFade) {
		super(sound, positionProvider, donePlayingProvider, shouldFade);
		
		this.vehicle = vehicle;
	}
	
	@Override
	public void update() {
		
		// TODO Auto-generated method stub
		super.update();
		double rpm = vehicle.getSolver().getCurrentRPM();
		this.repeatDelay = -1;
		this.attenuationType = ISound.AttenuationType.NONE;
		
		
		if(vehicle.isDead) {
			donePlaying = true;
		}
		
		if(!vehicle.isVehicleRunning()) donePlaying = true;
		
		if(!isDonePlaying()) {
			//System.out.println(this.pitch);
			//System.out.println(this.pitch);
			float suggested = (float) ((float) 1.0*(vehicle.solver.getCurrentRPM()/3500.0F)+1.0f);
			
			this.pitch = (float) Math.min(suggested, 2.0);
			this.volume = 2f;
			//this.pitch = 1.0f;
			//this.pitch = 1.0f;
		}
		
	}

}
