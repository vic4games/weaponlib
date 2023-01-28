package com.jimholden.conomy.containers.slots;

import com.jimholden.conomy.items.ItemPackingMaterial;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class PackingMatInputSlot extends SlotItemHandler {

	public PackingMatInputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		// TODO Auto-generated constructor stub
	}
	
	public boolean isItemValid(ItemStack stack) {
		// TODO Auto-generated method stub
		return stack.getItem() instanceof ItemPackingMaterial;
	}
	
	
	
	

}
