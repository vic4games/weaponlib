package com.jimholden.conomy.medical;

import java.util.ArrayList;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IConscious {
	
	
	public void tickDowned();
	public int isDowned();
	public void setDowned(int val);
	public int getBlood();
	public void setBlood(int val);
	public boolean isBleeding();
	public void setIsBleed(boolean state);
	public void tickBleed();
	public int getDownTimer();
	public void setDownTimer(int val);
	public void markDirty(boolean state);
	public boolean isDirty();
	public double getWeight();
	public void setWeight(double weight);
	public void setWaterLevel(int val);
	public int getWaterLevel();
	public void decreaseWaterTick();
	public void increaseWaterLevel(int amount);
	
	public double getPainLevel();
	public void setPain(double pain);
	
	public void updateApplicator();
	
	public void addDrug(SystemDrug mod);
	public ArrayList<SystemDrug> getSystemDrugs();
	
	public double getApplicator();
	public void setApplicator(double applicator);
	
	public void setHasSplint(boolean val);
	public boolean hasSplint();
	public int getLegHealth();
	public void setLegHealth(int health);

	
	public boolean hasDrugKey(String key);
	public double applyDrugEffect(String key, double d);
}
