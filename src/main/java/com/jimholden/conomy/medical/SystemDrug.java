package com.jimholden.conomy.medical;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class SystemDrug {
	
	public static class DrugAbility {
		
		private String key;
		private double value;
		private int operation;
		// 0 = add, 1 = mutliply, 2 = divide, 3 = pow of
		
		public DrugAbility(String key, double value, int operation) {
			this.key = key;
			this.value = value;
			this.operation = operation;
		}
		
		public double apply(double d) {
			switch(operation) {
			case 0:
				return d + getValue();
			case 1:
				return d * getValue();
			case 2:
				return d / getValue();
			case 3:
				return Math.pow(d, getValue());
			
			}
			return d;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		public int getOperation() {
			return operation;
		}

		public void setOperation(int operation) {
			this.operation = operation;
		}
		
		public static DrugAbility readNBT(NBTTagCompound comp) {
			int operation = comp.getInteger("op");
			double val = comp.getDouble("value");
			String key = comp.getString("key");
			return new DrugAbility(key, val, operation);
		}
		
		public NBTTagCompound writeNBT() {
			NBTTagCompound ability = new NBTTagCompound();
			ability.setString("key", getKey());
			ability.setDouble("value", getValue());
			ability.setInteger("op", getOperation());
			return ability;
		}
		
	}
	
	private int time;
	private PainModifier modifier;
	private ArrayList<DrugAbility> abilities = new ArrayList<>();
	
	
	public SystemDrug(PainModifier mod, int time) {
		this.modifier = mod;
		this.time = time;
	}
	
	public boolean containsKey(String key) {
		for(DrugAbility ab : this.abilities) {
			if(ab.getKey().equals(key)) return true;
		}
		return false;
	}
	
	public double applyEffect(String key, double d) {
		for(DrugAbility ab : this.abilities) {
			if(ab.getKey().equals(key)) {
				d = ab.apply(d);
			}
		}
		return d;
	}
	
	public void addAbility(String key, double val, int op) {
		addAbility(new DrugAbility(key, val, op));
	}
	
	public void addAbility(DrugAbility ab) {
		this.abilities.add(ab);
	}
	
	public void tickTime() {
		this.time--;
	}
	
	public int getTime() {
		return this.time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}

	public PainModifier getModifier() {
		return modifier;
	}

	public void setModifier(PainModifier modifier) {
		this.modifier = modifier;
	}
	
	public NBTTagCompound writeNBT() {
		NBTTagCompound comp = new NBTTagCompound();
		comp.setTag("painMod", this.modifier.writeNBT());
		comp.setInteger("time", this.time);
		
		NBTTagList list = new NBTTagList();
		for(DrugAbility ab : getAbilities()) {
			list.appendTag(ab.writeNBT());
		}
		comp.setTag("abilities", list);
		System.out.println("Project: " + comp);
		return comp;
	}
	
	public static SystemDrug readNBT(NBTTagCompound comp) {
		System.out.println("the droog reader goteem: " + comp);
		PainModifier modifier = PainModifier.readNBT(comp.getCompoundTag("painMod"));
		int t = comp.getInteger("time");
		SystemDrug drug = new SystemDrug(modifier, t);
		
		NBTTagList list = comp.getTagList("abilities", NBT.TAG_COMPOUND);
		ArrayList<DrugAbility> abilities = new ArrayList<>();
		for(int i = 0; i < list.tagCount(); ++i) {
			abilities.add(DrugAbility.readNBT(list.getCompoundTagAt(i)));
			System.out.println("adding " + DrugAbility.readNBT(list.getCompoundTagAt(i)).key);
		}
		drug.setAbilities(abilities);
		
		
		return drug;
	}

	public ArrayList<DrugAbility> getAbilities() {
		return abilities;
	}

	public void setAbilities(ArrayList<DrugAbility> abilities) {
		this.abilities = abilities;
	}

}
