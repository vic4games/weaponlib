package com.vicmatskiv.weaponlib.vehicle.network;

import java.util.HashMap;

import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;

public class VehiclePacketLatencyTracker {
	
	
	public static HashMap<EntityVehicle, Long> lastUpdate = new HashMap<>();
	
	public static void push(EntityVehicle vehicle) {
		long delta = System.currentTimeMillis()-lastUpdate.get(vehicle);
		lastUpdate.put(vehicle, System.currentTimeMillis());
	}
	
	

}
