package com.vicmatskiv.weaponlib.vehicle.jimphysics.engines;

import com.vicmatskiv.weaponlib.vehicle.jimphysics.Engine;

public class EvoIVEngine extends Engine {

	public EvoIVEngine(String name, String engineBrand) {
		super(name, engineBrand);
	}
	
	@Override
	public void setupTorqueCurve() {
		torqueCurve.put(1000, 100.0);
		torqueCurve.put(1500, 200.0);
		torqueCurve.put(2000, 275.0);
		torqueCurve.put(2500, 345.0);
		torqueCurve.put(3000, 578.0);
		torqueCurve.put(3500, 390.0);
		torqueCurve.put(4000, 385.0);
		torqueCurve.put(4500, 380.0);
		torqueCurve.put(5000, 373.0);
		torqueCurve.put(6000, 325.0);
		torqueCurve.put(7000, 250.0);
	}

}
