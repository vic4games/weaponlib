package com.jimholden.conomy.drugs;

import com.jimholden.conomy.drugs.components.DrugComponentPreset;
import com.jimholden.conomy.drugs.components.DrugComponentTool;
import com.jimholden.conomy.items.ItemDrugPowder;
import com.jimholden.conomy.util.NumberUtil;

import net.minecraft.item.ItemStack;

public class DrugExtractRecipe {
	
	DrugProperty one;
	DrugProperty two;
	DrugProperty three;
	
	float weightOne;
	float weightTwo;
	float weightThree;
	
	public DrugExtractRecipe(DrugProperty one, DrugProperty two, DrugProperty three, float weightOne, float weightTwo, float weightThree) {
		this.one = one;
		this.two = two;
		this.three = three;
		this.weightOne = weightOne;
		this.weightTwo = weightTwo;
		this.weightThree = weightThree;
	}
	
	
	public ItemStack getItemOne(double weight) {
		ItemStack stack = DrugComponentTool.getFromPreset(this.one);
		((ItemDrugPowder) stack.getItem()).setWeight(stack, NumberUtil.roundToDecimal(this.weightOne*weight, 2));
		return stack;
	}
	
	public ItemStack getItemTwo(double weight) {
		ItemStack stack = DrugComponentTool.getFromPreset(this.two);
		((ItemDrugPowder) stack.getItem()).setWeight(stack, NumberUtil.roundToDecimal(this.weightTwo*weight, 2));
		return stack;
	}
	
	public ItemStack getItemThree(double weight) {
		ItemStack stack = DrugComponentTool.getFromPreset(this.three);
		((ItemDrugPowder) stack.getItem()).setWeight(stack, NumberUtil.roundToDecimal(this.weightThree*weight, 2));
		return stack;
	}


}
