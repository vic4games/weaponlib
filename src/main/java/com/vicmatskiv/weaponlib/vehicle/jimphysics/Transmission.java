package com.vicmatskiv.weaponlib.vehicle.jimphysics;

import java.util.ArrayList;
import java.util.LinkedList;

import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;

import net.minecraft.world.gen.structure.StructureNetherBridgePieces.End;

public class Transmission {
	
	
	/**
	 * Constant ratios
	 */
	public float differentialRatio = 0.0F;
	public float reverseGearRatio = 0.0F;
	
	
	/**
	 * Min & Max Gears
	 */
	public int currentGear = 0;
	public int highestGear = 0;
	
	
	/**
	 * Transmission RPM settings
	 */
	public int upshiftRPM = 0;
	public int downshiftRPM = 0;
	
	public int eUShift = 0;
	public int eDShift = 0;
	
	/**
	 * Transmission Alterable Settings
	 */
	public boolean isReverseGear = false;
	public boolean isOnEcoShift = false;
	public boolean declutched = false;
	
	
	/**
	 * Vehicle Gears
	 */
	public ArrayList<Gear> vehicleGears = new ArrayList<>();
	
	/**
	 * For animations and such
	 */
	public int startGear = 1;
	public int targetGear = 1;
	
	/**
	 * Shift timer
	 */
	public boolean runningAShift = false;
	public int maxShiftTime = 75;
	public int shiftTimer = 0;
	
	
	/**
	 * Creates a new Transmission with various paramets.
	 * WARNING: YOU MUST STILL ADD GEARS!!!
	 * 
	 * @param diffRatio The final drive ratio.
	 * @param rRatio The vehicle's reverse ratio.
	 * @param uRPM The RPM at which the transmission will attempt to upshift
	 * @param dRPM The RPM at which the transmission will try to downshift
	 */
	public Transmission(float diffRatio, float rRatio, int uRPM, int dRPM) {
		
		differentialRatio = diffRatio;
		upshiftRPM = uRPM;
		reverseGearRatio = rRatio;
		downshiftRPM = dRPM;
	}
	
	
	public int getCurrentGear() {
		if(isReverseGear) return -1;
		return currentGear+1;
	}
	
	public void addGear(Gear gear) {
		vehicleGears.add(gear);
		++highestGear;
	}
	
	/**
	 * ECO
	 */
	public void toggleECO() {
		this.isOnEcoShift = !this.isOnEcoShift;
	}
	
	public void setEcoState(boolean state) {
		this.isOnEcoShift = state;
	}
	
	public void ecoOff() {
		this.isOnEcoShift = false;
	}
	
	public void ecoOn() {
		this.isOnEcoShift = true;
	}
	
	public boolean isEcoModeOn() {
		return this.isOnEcoShift;
	}
	
	/**
	 * SHIFTING
	 */
	
	public void upShift() {
		//System.out.println("Trying to upshift @ " + getCurrentGear() + " BOOE: " + (getCurrentGear() < vehicleGears.size()));
		if(getCurrentGear() < vehicleGears.size()) {
			currentGear += 1;
		}
	}
	
	public void downShift() {
		if(currentGear > 0) {
			currentGear -= 1;
		}
	}
	
	public void forceShift(int targetGear) {
		currentGear = targetGear-1;
	}
	
	/**
	 * REVERSE
	 */
	
	public void enterReverse() {
		isReverseGear = true;
	}
	
	public void exitReverse() {
		isReverseGear = false;
	}
	
	public void notifyShift() {
		runningAShift = true;
		//shiftTimer = 0;
	}
	
	public void tickTransmission() {
		
		if(runningAShift) {
			
			shiftTimer += 1;
		}
		
		if(shiftTimer >= maxShiftTime) {
			System.out.println("SHIFT COMPLETED!");
			runningAShift = false;
			shiftTimer = 0;
			startGear = targetGear;
		}
	}
	
	/**
	 * AUTOMATIC TRANSMISSION RUNNER
	 * @param engineRPM
	 */
	
	public void runAutomaticTransmission(EntityVehicle vehicle, int engineRPM) {
		
		//tick
		tickTransmission();
		
		// cancels automatic transmission update if
		// the car is in reverse
		if(isReverseGear) return;
		
		int uShift = 0;
		int dShift = 0;
		
		
		if(!isOnEcoShift) {
			uShift = this.upshiftRPM;
			dShift = this.downshiftRPM;
		 } else {
			 uShift = this.eUShift;
			 dShift = this.eDShift;
		 }
		
        
        if(engineRPM > uShift && this.getCurrentGear() != highestGear && vehicle.throttle > 0.1) {
        	if(runningAShift) {
        		double median = maxShiftTime/2.0;
        		if(shiftTimer > median) {
        			startGear = getCurrentGear();
        			shiftTimer = maxShiftTime-shiftTimer;
        		} 
        		
        		
        	} else {
        		startGear = getCurrentGear();
        	}
        	
            upShift();
            vehicle.notifyOfShift(getCurrentGear());
            targetGear = getCurrentGear();
            notifyShift();
            //System.out.println("Shifted up to gear " + getCurrentGear() + " RPM : " + engineRPM);
        }
        
        if(engineRPM < dShift && this.getCurrentGear() != 1 /*&& vehicle.throttle < 0.5*/) {
        	if(runningAShift) {
        		double median = maxShiftTime/2.0;
        		if(shiftTimer > median) {
        			startGear = getCurrentGear();
        			shiftTimer = maxShiftTime-shiftTimer;
        		} 
        		
        		
        	} else {
        		startGear = getCurrentGear();
        	}
            downShift();
            vehicle.notifyOfShift(getCurrentGear());
            targetGear = getCurrentGear();
            notifyShift();
            //System.out.println("Shifted down to gear " + getCurrentGear() + " RPM : " + engineRPM);
        }
	}
	
	public Transmission cloneTransmission() {
		Transmission t = this;
		Transmission newT = new Transmission(t.differentialRatio, t.reverseGearRatio, t.upshiftRPM, t.downshiftRPM);
		for(Transmission.Gear g : t.vehicleGears) {
			newT.addGear(g);
		}
		return newT;
	}
	
	/**
	 * GET RATIOS
	 * @return
	 */
	
	public float getCurrentGearRatio() {
		if(isReverseGear) return reverseGearRatio;
		return vehicleGears.get(currentGear).gearRatio;
	}
	
	public float getDifferentialRatio() {
		return differentialRatio;
	}
	
	/**
	 * GEARS
	 */
	
	
	class Gear {
		public float gearRatio = 0.0F;
		
		public Gear(float gR) {
			gearRatio = gR;
		}
		
		
	}
	
	public void gearWithRatio(float gR) {
		vehicleGears.add(new Gear(gR));
		highestGear += 1;
	}
	
	public Transmission withEcoShift(int upshift, int downShift) {
		this.eUShift = upshift;
		this.eDShift = downShift;
		return this;
	}
	
	/**
	 * QUICK INITIALIZERS FOR EASY TRANSMISSION GENERATION
	 */
	
	public Transmission quickSixSpeed(float one, float two, float three, float four, float five, float six) {
		gearWithRatio(one);
		gearWithRatio(two);
		gearWithRatio(three);
		gearWithRatio(four);
		gearWithRatio(five);
		gearWithRatio(six);
		return this;
	}
	
	public Transmission quickFiveSpeed(float one, float two, float three, float four, float five) {
		gearWithRatio(one);
		gearWithRatio(two);
		gearWithRatio(three);
		gearWithRatio(four);
		gearWithRatio(five);
		return this;
	}
	
	public Transmission quickFourSpeed(float one, float two, float three, float four) {
		gearWithRatio(one);
		gearWithRatio(two);
		gearWithRatio(three);
		gearWithRatio(four);
		return this;
	}


}

