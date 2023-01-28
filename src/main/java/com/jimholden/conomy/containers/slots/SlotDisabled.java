package com.jimholden.conomy.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotDisabled extends Slot {

	public SlotDisabled(Slot slot) {
		super(slot.inventory, slot.getSlotIndex(), slot.xPos, slot.yPos);
	}
	
	public SlotDisabled(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

}
