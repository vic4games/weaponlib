package com.vicmatskiv.weaponlib.vehicle.jimphysics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Engine {
	private String engineName;
	private String engineBrand;
	
	public LinkedHashMap<Integer, Double> torqueCurve = new LinkedHashMap<Integer, Double>();
	
	public Engine(String name, String engineBrand) {
		this.engineName = name;
		this.engineBrand = engineBrand;
		setupTorqueCurve();
		
	}
	
	public void setupTorqueCurve() {}
	
	public double getTorqueAtRPM(int rpm) {

		if(rpm < 1000) return 0;
		
		if(torqueCurve.containsKey(rpm)) return torqueCurve.get(rpm);
		int firstBound = 0;
		int secondBound = 0;
		
		
		ArrayList<Integer> keys = new ArrayList<Integer>();
		keys.addAll(torqueCurve.keySet());
		for(int f = 0; f < keys.size()-1; ++f) {
			int min = keys.get(f);
			int max = keys.get(f+1);
			if(min < rpm && rpm < max) {
				firstBound = min;
				secondBound = max;
			}
		}
		
		
		// retrieve curve values
		double t1 = torqueCurve.get(firstBound);
		double t2 = torqueCurve.get(secondBound);
		
		// Calculate step
		double tStep = (((double) rpm)-firstBound)/(secondBound-firstBound);
		double stepped = t1 + (t2 - t1) * tStep;
	//	System.out.println("F: " + firstBound + " | S: " + secondBound + " | tS: " + tStep + " | T1: " + t1 + " | T2: " + t2 + " | st: " + stepped);
		
		return stepped;
		
	}
	
	
	
	public String getEngineBrand() {
		return engineBrand;
	}
	
	public String getEngineName() {
		return engineName;
	}

}
