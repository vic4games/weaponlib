package com.jimholden.conomy.containers.slots;

import com.jimholden.conomy.items.ItemBaseComponent;
import com.jimholden.conomy.items.ItemDrugPowder;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CuttingAgentSlot extends SlotItemHandler{

	public CuttingAgentSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}


	
	@Override
	public boolean isItemValid(ItemStack stack) {
		if(stack.getItem() instanceof ItemDrugPowder) {
			
			if(((ItemDrugPowder) stack.getItem()).getDrugType(stack) == 4) return true;
			else return false;
			
		} else {
			return false;
		}
	
	}
	
	
	

	
	

}
