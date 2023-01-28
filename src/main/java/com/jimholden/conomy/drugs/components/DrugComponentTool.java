package com.jimholden.conomy.drugs.components;

import java.util.Arrays;
import java.util.List;

import com.jimholden.conomy.drugs.DrugCombiner;
import com.jimholden.conomy.drugs.DrugProperty;
import com.jimholden.conomy.drugs.IChemical;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.ItemBottle;
import com.jimholden.conomy.items.ItemDrugBrick;
import com.jimholden.conomy.items.ItemDrugPowder;
import com.jimholden.conomy.items.ItemPackingMaterial;
import com.jimholden.conomy.items.ItemPill;

import net.minecraft.item.ItemStack;

public class DrugComponentTool {
	
	
	public static ItemStack getFromPreset(DrugProperty drug) {
		
		ItemStack stack;
		if(drug.isPowder) {
			stack = new ItemStack(ModItems.POWDER);
			((ItemDrugPowder) stack.getItem()).setPotency(stack, drug.potency);
			((ItemDrugPowder) stack.getItem()).setDrugName(stack, drug.name);
			((ItemDrugPowder) stack.getItem()).setDrugType(stack, drug.drugType);
			((ItemDrugPowder) stack.getItem()).setWeight(stack, drug.weight);
		} else {
			stack = new ItemStack(ModItems.DRUGBOTTLE);
			((ItemBottle) stack.getItem()).setPotency(stack, drug.potency);
			((ItemBottle) stack.getItem()).setDrugName(stack, drug.name);
			((ItemBottle) stack.getItem()).setDrugType(stack, drug.drugType);
			((ItemBottle) stack.getItem()).setWeight(stack, drug.weight);
		}
		
		
		return stack;
		
	}
	
	public static ItemStack makeBrick(ItemStack drug, ItemStack mat) {
		int drugType = ((ItemDrugPowder) drug.getItem()).getDrugType(drug);
		double weight = ((ItemDrugPowder) drug.getItem()).getWeight(drug);
		float potency = ((ItemDrugPowder) drug.getItem()).getPotency(drug);
		int packingMatType = ((ItemPackingMaterial) mat.getItem()).getMatType();
		ItemStack stackBrick = null;
		if(packingMatType == 1) {
			stackBrick = new ItemStack(ModItems.DRUGBRICKPAPER);
		}
		if(packingMatType == 2) {
			stackBrick = new ItemStack(ModItems.DRUGBRICKSARAN);
		}
		if(packingMatType == 3) {
			stackBrick = new ItemStack(ModItems.DRUGBRICKWAX);
		}
		
		((ItemDrugBrick) stackBrick.getItem()).setDrugType(stackBrick, drugType);
		((ItemDrugBrick) stackBrick.getItem()).setPotency(stackBrick, potency);
		((ItemDrugBrick) stackBrick.getItem()).setWeight(stackBrick, weight);
		
		return stackBrick;
	}
	
	public static ItemStack makePill(ItemStack drug, ItemStack cuttingAgent) {
		
		int drugType = ((ItemDrugPowder) drug.getItem()).getDrugType(drug);
		double weight = ((ItemDrugPowder) drug.getItem()).getWeight(drug);
		float potency = ((ItemDrugPowder) drug.getItem()).getPotency(drug);
		
		ItemStack stackPill = new ItemStack(ModItems.PILL);
		((ItemPill) stackPill.getItem()).setDrugType(stackPill, drugType);
		((ItemPill) stackPill.getItem()).setPotency(stackPill, potency);
		((ItemPill) stackPill.getItem()).setWeight(stackPill, weight);
		
		
		return stackPill;
		
	}
	
	
	public static ItemStack mixThreeCompounds(ItemStack stackOne, ItemStack stackTwo, ItemStack stackThree) {
		
		List<ItemStack> list = Arrays.asList(stackOne, stackTwo, stackThree); 

		float potencyTotal = 0.0F;
		float toxicityTotal = 0.0F;
		for(ItemStack stack : list) {
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof IChemical) {
					potencyTotal += ((IChemical) stack.getItem()).getPotency(stack);
					toxicityTotal += ((IChemical) stack.getItem()).getToxicity(stack);
				}
			}	
		}
		
		int drugTypeOne;
		if(stackOne != ItemStack.EMPTY) {
			drugTypeOne = ((IChemical) stackOne.getItem()).getDrugType(stackOne);
		} else {
			drugTypeOne = 0;
		}
		
		int drugTypeTwo;
		if(stackTwo != ItemStack.EMPTY) {
			drugTypeTwo = ((IChemical) stackTwo.getItem()).getDrugType(stackTwo);
		} else {
			drugTypeTwo = 0;
		}
		
		
		int drugTypeThree;
		if(stackThree != ItemStack.EMPTY) {
			drugTypeThree = ((IChemical) stackThree.getItem()).getDrugType(stackThree);
		} else {
			drugTypeThree = 0;
		}
		
		ItemStack newDrug = new ItemStack(ModItems.POWDER);
		((ItemDrugPowder) newDrug.getItem()).setDrugType(newDrug, DrugCombiner.combineType(drugTypeOne, drugTypeTwo, drugTypeThree));
		((ItemDrugPowder) newDrug.getItem()).setDrugName(newDrug, DrugCombiner.combineName(drugTypeOne, drugTypeTwo, drugTypeThree));
		((ItemDrugPowder) newDrug.getItem()).setPotency(newDrug, potencyTotal);
		((ItemDrugPowder) newDrug.getItem()).setToxicity(newDrug, toxicityTotal);
		return newDrug;
	}

}
