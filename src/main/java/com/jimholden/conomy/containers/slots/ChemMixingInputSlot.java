package com.jimholden.conomy.containers.slots;

import com.jimholden.conomy.items.ItemBaseComponent;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ChemMixingInputSlot extends SlotItemHandler{

	public ChemMixingInputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		return (stack.getItem() instanceof ItemBaseComponent);
	}
	
	
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		// TODO Auto-generated method stub
		return 1;
	}
	
	

}
