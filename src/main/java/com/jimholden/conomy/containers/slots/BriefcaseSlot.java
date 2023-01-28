package com.jimholden.conomy.containers.slots;

import com.jimholden.conomy.items.ItemChemicalBriefcase;
import com.jimholden.conomy.items.ItemDrugPowder;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BriefcaseSlot extends SlotItemHandler {

	public BriefcaseSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		if(stack.getItem() instanceof ItemChemicalBriefcase) {
			return false;
		}
		if(stack.getItem() instanceof ItemDrugPowder) {
			return true;
		}
		return false;
	}
	
	

}
