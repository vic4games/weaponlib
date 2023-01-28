package com.jimholden.conomy.containers.slots;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotLooting extends SlotItemHandler{
	
	private boolean slotEnabled = false;
	IItemHandler itemHandler = null;
	int index;

	public SlotLooting(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		this.itemHandler = itemHandler;
		this.index = index;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isEnabled() {
		return !itemHandler.getStackInSlot(index).isEmpty();
	}
	
	public void disable() {
		slotEnabled = false;
	}
	
	public void enable() {
		slotEnabled = true;
	}

}
