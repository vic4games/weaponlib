package com.jimholden.conomy.medical;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class ConsciousCapability implements IConscious {
	
	public static final int MAX_BLOOD = 36000;
	public static final int MAX_LEG_HEALTH = 24000;
	
	private int isDowned = 0;
	private int downedTimer = 2400;
	private int bleedTimer = MAX_BLOOD;
	private int waterLevel = 20;
	private boolean isBleeding = false;
	private boolean markDirty;
	private double weight;
	
	private double pain = 0;
	private double painApplicator = 0;
	
	// leg
	private int legHealth = MAX_LEG_HEALTH;
	private boolean hasSplint = false;
	
	public ArrayList<SystemDrug> drugs = new ArrayList<>();

	@Override
	public int isDowned() {
		return this.isDowned;
	}

	@Override
	public void setDowned(int val) {
		this.isDowned = val;
	}

	@Override
	public int getBlood() {
		return this.bleedTimer;
	}

	@Override
	public void setBlood(int val) {
		this.bleedTimer = val;
		if(this.bleedTimer > MAX_BLOOD) this.bleedTimer = MAX_BLOOD;
	}

	@Override
	public boolean isBleeding() {
		return this.isBleeding;
	}

	@Override
	public void setIsBleed(boolean state) {
		this.isBleeding = state;
	}

	@Override
	public void tickBleed() {
		this.bleedTimer -= 1;
		
	}
	
	@Override
	public void tickDowned() {
		this.downedTimer -= 1;
	}
	
	@Override
	public int getDownTimer() {
		return this.downedTimer;
	}
	
	@Override
	public void setDownTimer(int val) {
		this.downedTimer = val;
	}

	@Override
	public void markDirty(boolean state) {
		this.markDirty = state;
		
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return this.markDirty;
	}

	@Override
	public double getWeight() {
		// TODO Auto-generated method stub
		return this.weight;
	}

	@Override
	public void setWeight(double weight) {
		this.weight = weight;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWaterLevel(int val) {
		this.waterLevel = val;
		
	}

	@Override
	public int getWaterLevel() {
		// TODO Auto-generated method stub
		return this.waterLevel;
	}

	@Override
	public void decreaseWaterTick() {
		this.waterLevel -= 1;
		
	}

	@Override
	public void increaseWaterLevel(int amount) {
		this.waterLevel += amount;
		if(this.waterLevel > 20) {
			this.waterLevel = 20;
		}
		
	}

	@Override
	public double getPainLevel() {
		// TODO Auto-generated method stub
		return this.pain;
	}

	@Override
	public void setPain(double pain) {
		
		this.pain = pain;
		
		
		if(this.pain < 0.00001) this.pain = 0;
		
	}

	@Override
	public double getApplicator() {
		return this.painApplicator;
	}

	@Override
	public void setApplicator(double applicator) {
		this.painApplicator = applicator;
		
	}
	
	@Override
	public void addDrug(SystemDrug mod) {
		this.drugs.add(mod);
		
	}
	
	

	@Override
	public void updateApplicator() {
		this.painApplicator = 0;
		if(!this.drugs.isEmpty()) {
			
			Iterator<SystemDrug> itr = this.drugs.iterator();
			while(itr.hasNext()) {
				SystemDrug drug = itr.next();
				PainModifier mod = drug.getModifier();
				if(drug.getTime() <= 0) {
					itr.remove();
					continue;
				}
				drug.tickTime();
				this.painApplicator += mod.getAmount();
			}
			
		}
		
	}

	@Override
	public ArrayList<SystemDrug> getSystemDrugs() {
		return this.drugs;
	}

	@Override
	public boolean hasSplint() {
		return this.hasSplint;
	}

	@Override
	public int getLegHealth() {
		return this.legHealth;
	}

	@Override
	public void setLegHealth(int health) {
		this.legHealth = health;
	}
	
	@Override
	public void setHasSplint(boolean val) {
		this.hasSplint = val;
		
	}

	@Override
	public boolean hasDrugKey(String key) {
		for(SystemDrug drug : this.drugs) {
			if(drug.containsKey(key)) return true;
		}
		return false;
		
	}

	@Override
	public double applyDrugEffect(String key, double d) {
		for(SystemDrug drug : this.drugs) {
			d = drug.applyEffect(key, d);
		}
		return d;
	}
	



	
	
	

}
