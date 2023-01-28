package com.jimholden.conomy.drugs;

import net.minecraft.item.ItemStack;

public interface IChemical {
	
	public float getPotency(ItemStack stack);
	public int getDrugType(ItemStack stack);
	public float getToxicity(ItemStack stack);
	public void setPotency(ItemStack stack, float value);
	public void setToxicity(ItemStack stack, float value);

}
