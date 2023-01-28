package com.jimholden.conomy.medical;

import net.minecraft.nbt.NBTTagCompound;

public class PainModifier {
	
	public static final PainModifier NONE = new PainModifier(0);
	
	
	public double amount = 0;
	
	public PainModifier(double amt) {
		this.amount = amt;
	}
	
	

	
	public NBTTagCompound writeNBT() {
		NBTTagCompound comp = new NBTTagCompound();
		comp.setDouble("amount", this.amount);
		return comp;
	}
	
	public static PainModifier readNBT(NBTTagCompound nbt) {
		double amt = nbt.getDouble("amount");
		return new PainModifier(amt);
	}

	public double getAmount() {
		return amount;
	}
	
	

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
