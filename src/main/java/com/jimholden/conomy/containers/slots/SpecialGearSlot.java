package com.jimholden.conomy.containers.slots;

import com.jimholden.conomy.items.EnumGear;
import com.jimholden.conomy.items.IGearType;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;

public class SpecialGearSlot extends SlotItemHandler {
	
	private EnumGear gearType;
	
	public SpecialGearSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, EnumGear gearType)
    {
        super(itemHandler, index, xPosition, yPosition);
        this.gearType = gearType;
    }
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		// TODO Auto-generated method stub
		//ItemStack
		
		if(stack.getItem() instanceof IGearType) {
			return ((IGearType) stack.getItem()).getGearType() == this.gearType;
		} else {
			return false;
		}
		
	}

}
